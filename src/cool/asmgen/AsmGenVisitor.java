package cool.asmgen;

import cool.ast.abstracts.BinaryExpression;
import cool.ast.abstracts.Feature;
import cool.ast.interfaces.ASTVisitor;
import cool.ast.nodes.*;
import cool.structures.scope.Scope;
import cool.structures.symbol.ClassSymbol;
import cool.structures.symbol.MethodSymbol;
import cool.structures.symbol.SymbolTable;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.*;

public class AsmGenVisitor implements ASTVisitor<ST> {
    // 0 label = For Object
    // 1 label = For IO
    // 2 label = For Int
    // 3 label = For String
    // 4 label = For Bool
    private static int tagCounter = 5;
    private final STGroupFile templates;

    private Scope currentScope;
    private final List<ST> stringData;
    private final List<ST> intData;
    private final List<ST> methodData;
    private final Map<Integer, Integer> intConstants;

    public AsmGenVisitor() {
        templates = new STGroupFile(
                Objects.requireNonNull(getClass().getResource("asmgen.stg"))
        );
        stringData = new ArrayList<>();
        intData = new ArrayList<>();
        methodData = new ArrayList<>();
        intConstants = new HashMap<>();
        populateInitialIntConstants();
    }

    private void populateInitialIntConstants() {
        intConstants.put(0, 0);
        intConstants.put(6, 1);
        intConstants.put(2, 2);
        intConstants.put(3, 3);
    }

    private static int getNextTag() {
        return tagCounter++;
    }

    /**
     * Combining all data sections into a single ST
     * using the "combineLines" template or some "sequence" approach.
     */
    private ST mergeST(List<ST> stList) {
        ST merged = templates.getInstanceOf("combineLines");
        for (ST st : stList) {
            merged.add("e", st.render());
        }
        return merged;
    }

    private void parseIntConstantsToIntData(Map<Integer, Integer> intConstants) {
        for (Map.Entry<Integer, Integer> entry : intConstants.entrySet()) {
            int val = entry.getKey();
            int tag = entry.getValue();

            ST intConstantST = templates.getInstanceOf("intConstantDefinition")
                    .add("tag", tag)
                    .add("val", val);

            intData.add(intConstantST);
        }
    }

    @Override
    public ST visit(Program program) {
        currentScope = SymbolTable.getGlobals();

        List<ST> classNamePointers = new ArrayList<>();
        List<ST> protoObjectDefs   = new ArrayList<>();
        List<ST> allDispatchTables = new ArrayList<>();
        List<ST> classProtoPointers = new ArrayList<>();
        List<ST> classInits = new ArrayList<>();

        for (CoolClass c : program.getClasses()) {
            ST classData = c.accept(this);

            ST nameLine = templates.getInstanceOf("putWord")
                    .add("item", "str_const" + c.getType().getText());
            classNamePointers.add(nameLine);

            ST dispatchTable = templates.getInstanceOf("createDispatchTable")
                    .add("thisClass", c.getType().getText())
                    .add("methodSet", "");
            allDispatchTables.add(dispatchTable);

            ST protObj = templates.getInstanceOf("putWord")
                    .add("item", c.getType().getText() + "_protObj");
            ST init = templates.getInstanceOf("putWord")
                    .add("item", c.getType().getText() + "_init");
            classProtoPointers.add(mergeST(List.of(protObj, init)));

            protoObjectDefs.add( (ST) classData.getAttribute("prot"));
            classInits.add( (ST) classData.getAttribute("init"));
        }

        ST finalProgram = templates.getInstanceOf("program");
        parseIntConstantsToIntData(intConstants);

        addMainMethod();

        finalProgram.add("integerData",        mergeST(intData));
        finalProgram.add("stringData",         mergeST(stringData));
        finalProgram.add("classNamePointers",  mergeST(classNamePointers));
        finalProgram.add("classProtoPointers", mergeST(classProtoPointers));
        finalProgram.add("protoObjectDefs",    mergeST(protoObjectDefs));
        finalProgram.add("allDispatchTables",  mergeST(allDispatchTables));
        finalProgram.add("classInits",         mergeST(classInits));
        finalProgram.add("methodData",         mergeST(methodData));

        return finalProgram;
    }

    @Override
    public ST visit(CoolClass coolClass) {
        String className = coolClass.getType().getText();
        ClassSymbol currentSym = (ClassSymbol) SymbolTable.getGlobals().lookup(className);
        ClassSymbol parentSym  = (ClassSymbol) currentSym.getParent();

        final Scope saveScope = currentScope;
        currentScope = currentSym;

        int tag = getNextTag();
        List<ST> fieldWords = new ArrayList<>();

        ST prototypeST = templates.getInstanceOf("createProt")
                .add("name",        className)
                .add("tag",         tag)
                .add("size",        3)
                .add("dispTabName", className)
                .add("fieldWords",  fieldWords);

        ST initST = templates.getInstanceOf("initClass")
                .add("thisClass",   className)
                .add("parentClass", parentSym.getName())
                .add("attrSetup",   "");

        for (Feature feature : coolClass.getFeatures()) {
            feature.accept(this);
        }

        currentScope = saveScope;

        final int intTagLength = intConstants.computeIfAbsent(className.length(), k -> intConstants.size());

        stringData.add(templates.getInstanceOf("strConstantDefinition")
                      .add("strTag",  className)
                      .add("size",    calculateWordSizeForStringConstant(coolClass))
                      .add("intTag",  intTagLength)
                      .add("textVal", className));

        return templates.getInstanceOf("initAndProt")
                .add("prot", prototypeST)
                .add("init", initST);
    }

    private int calculateWordSizeForStringConstant(CoolClass coolClass) {
        return (int) (4 + Math.ceil(coolClass.getType().getText().length() / 4.0));
    }

    private void addMainMethod() {
        ST mainMethod = templates.getInstanceOf("defineMipsMethod")
                .add("name",           "Main.main")
                .add("bodyInstr",      "la $a0 int_const0")
                .add("prepareLocals",   templates.getInstanceOf("allocLocals").add("bytes", 0).render())
                .add("freeLocalInstr", "")
                .add("freeParamInstr", "");

        methodData.add(mainMethod);
    }

    private int calculateLocalBytes(Method method) {
        // Example calculation: 4 bytes per local variable
        MethodSymbol methodScope = (MethodSymbol) currentScope;
        return methodScope.getSymbols().size() * 4;
    }

    private int calculateParamBytes(Method method) {
        // Example calculation: 4 bytes per parameter
        return method.getFormals().size() * 4;
    }

    @Override
    public ST visit(Method method) {
        String methodName = method.getIdentifier().getText();
        ClassSymbol enclosingClassSymbol = (ClassSymbol) currentScope;
        MethodSymbol methodSymbol = (MethodSymbol) enclosingClassSymbol.getMethods().get(methodName);

        Scope saveScope = currentScope;
        currentScope = methodSymbol;

        ST prepareLocals = templates.getInstanceOf("allocLocals").add("bytes", calculateLocalBytes(method));
        ST freeParamInstr = templates.getInstanceOf("freeParameters").add("bytes", calculateParamBytes(method))
                .add("paramAlias", methodName);

        ST methodST = templates.getInstanceOf("defineMipsMethod")
                .add("name",           enclosingClassSymbol.getName() + "." + methodName)
                .add("bodyInstr",      "")
                .add("prepareLocals",   prepareLocals.render())
                .add("freeLocalInstr", "")
                .add("freeParamInstr",  freeParamInstr.render());

        methodData.add(methodST);

        currentScope = saveScope;

        return methodST;
    }

    @Override
    public ST visit(Attribute attribute) {
        return null;
    }

    @Override
    public ST visit(Formal formal) {
        return null;
    }

    @Override
    public ST visit(CoolInt coolInt) {
        return null;
    }

    @Override
    public ST visit(CoolBool coolBool) {
        return null;
    }

    @Override
    public ST visit(CoolString coolString) {
        return null;
    }

    @Override
    public ST visit(Id id) {
        return null;
    }

    @Override
    public ST visit(BinaryExpression binaryExpression) {
        return null;
    }

    @Override
    public ST visit(Complement complement) {
        return null;
    }

    @Override
    public ST visit(Not not) {
        return null;
    }

    @Override
    public ST visit(Assignment assignment) {
        return null;
    }

    @Override
    public ST visit(CoolNew coolNew) {
        return null;
    }

    @Override
    public ST visit(IsVoid isVoid) {
        return null;
    }

    @Override
    public ST visit(ImplicitCall implicitCall) {
        return null;
    }

    @Override
    public ST visit(Call call) {
        return null;
    }

    @Override
    public ST visit(CoolIf coolIf) {
        return null;
    }

    @Override
    public ST visit(CoolWhile coolWhile) {
        return null;
    }

    @Override
    public ST visit(LocalDefinition localDefinition) {
        return null;
    }

    @Override
    public ST visit(CoolLet coolLet) {
        return null;
    }

    @Override
    public ST visit(CoolCase coolCase) {
        return null;
    }

    @Override
    public ST visit(CaseBranch caseBranch) {
        return null;
    }

    @Override
    public ST visit(BlockStatement blockStatement) {
        return null;
    }

    @Override
    public ST visit(BlockExpression blockExpression) {
        return null;
    }
}
