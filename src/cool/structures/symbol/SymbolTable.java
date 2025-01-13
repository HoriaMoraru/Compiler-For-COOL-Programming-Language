package cool.structures.symbol;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import cool.parser.CoolParser;
import cool.structures.scope.DefaultScope;
import cool.structures.scope.Scope;
import org.antlr.v4.runtime.*;

import cool.compiler.Compiler;

public class SymbolTable {
    // Built-in Classes
    private static final String COOL_OBJECT_NAME = "Object";
    private static final String COOL_INT_NAME = "Int";
    private static final String COOL_BOOL_NAME = "Bool";
    private static final String COOL_STRING_NAME = "String";
    private static final String COOL_IO_NAME = "IO";
    private static final String COOL_SELF_TYPE_NAME = "SELF_TYPE";

    // Built-in methods
    private static final String[] OBJECT_METHODS = {"abort", "type_name", "copy"};
    private static final String[] STRING_METHODS = {"length", "concat", "substr"};
    private static final String[] IO_METHODS = {"in_string", "in_int", "out_string", "out_int"};

    private static Map<TypeSymbol, ClassSymbol> classes;

    private static Scope globals;
    
    private static boolean semanticErrors;
    private static ClassSymbol baseInheritanceClass;

    private SymbolTable() {}

    public static void defineBasicClasses() {
        globals = new DefaultScope(null);
        semanticErrors = false;
        classes = new LinkedHashMap<>();

        // Create built-in classes
        baseInheritanceClass = createObjectClass();
        createClass(COOL_INT_NAME, baseInheritanceClass);
        createClass(COOL_BOOL_NAME, baseInheritanceClass);
        ClassSymbol stringClass = createClass(COOL_STRING_NAME, baseInheritanceClass);
        ClassSymbol ioClass = createClass(COOL_IO_NAME, baseInheritanceClass);
        createClass(COOL_SELF_TYPE_NAME, baseInheritanceClass);

        // Add built-in methods
        addClassMethods(baseInheritanceClass, OBJECT_METHODS);
        addClassMethods(stringClass, STRING_METHODS);
        addClassMethods(ioClass, IO_METHODS);
    }

    private static ClassSymbol createClass(String name, ClassSymbol parent) {
        ClassSymbol classSymbol = new ClassSymbol(name, parent);
        classes.put(new TypeSymbol(name), classSymbol);
        globals.add(classSymbol);
        return classSymbol;
    }

    private static ClassSymbol createObjectClass() {
        ClassSymbol objectClass = new ClassSymbol(COOL_OBJECT_NAME, globals);
        classes.put(new TypeSymbol(COOL_OBJECT_NAME), objectClass);
        globals.add(objectClass);
        return objectClass;
    }

    private static void addClassMethods(ClassSymbol classSymbol, String[] methods) {
        for (String method : methods) {
            classSymbol.add(new MethodSymbol(method, null, classSymbol, null));
        }
    }
    
    /**
     * Displays a semantic error message.
     * 
     * @param ctx Used to determine the enclosing class context of this error,
     *            which knows the file name in which the class was defined.
     * @param info Used for line and column information.
     * @param str The error message.
     */
    public static void error(ParserRuleContext ctx, Token info, String str) {
        while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();
        
        String message = "\"" + new File(Compiler.fileNames.get(ctx)).getName()
                + "\", line " + info.getLine()
                + ":" + (info.getCharPositionInLine() + 1)
                + ", Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static void error(String str) {
        String message = "Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static boolean hasSemanticErrors() {
        return semanticErrors;
    }

    public Map<TypeSymbol, ClassSymbol> getClasses() {
        return classes;
    }

    public static ClassSymbol getBaseInheritanceClass() {
        return baseInheritanceClass;
    }

    public static Scope getGlobals() {
        return globals;
    }
}
