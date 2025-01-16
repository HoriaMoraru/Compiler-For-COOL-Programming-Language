package cool.asmgen;

import cool.ast.abstracts.BinaryExpression;
import cool.ast.interfaces.ASTVisitor;
import cool.ast.nodes.*;
import cool.structures.symbol.ClassSymbol;
import cool.structures.symbol.SymbolTable;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.ArrayList;
import java.util.List;

public class AsmGenVisitor implements ASTVisitor<ST> {
    private static int tagCounter = 0;
    private final STGroupFile templates;
    private ST mainSection; // Contains the main program instructions
    private ST dataSection; // Contains global variable declarations (.data section)
    private ST textSection; // Contains function definitions (in .text section)

    public AsmGenVisitor() {
        templates = new STGroupFile("asmgen/asmgen.stg");
    }

    private static int getNextTag() {
        return tagCounter++;
    }

    private List<ST> generateConstants() {
        List<ST> constants = new ArrayList<>();
        constants.add(templates.getInstanceOf("int_const")
                 .add("value", 0)
                 .add("tag", getNextTag())
                 .add("size", 4));
        final int boolTag = getNextTag();
        constants.add(templates.getInstanceOf("bool_const")
                 .add("value", 0)
                 .add("tag", boolTag).add("size", 4));
        constants.add(templates.getInstanceOf("bool_const")
                 .add("value", 1)
                 .add("tag", boolTag)
                 .add("size", 4));
        constants.add(templates.getInstanceOf("str_const")
                .add("index", 1)
                .add("tag", getNextTag())
                .add("size", 5)
                .add("int_value", 0)
                .add("text", "Object"));
        return constants;
    }

    private ST generateTextSection(Program program) {
        ST combinedText = templates.getInstanceOf("sequence");
        for (CoolClass coolClass : program.getClasses()) {
            if ("Main".equals(coolClass.getType().getText())) {
                ST classText = coolClass.accept(this);
                combinedText.add("e", classText.render());
            }
        }
        return combinedText;
    }

    private ST mergeST(List<ST> stList) {
        ST merged = templates.getInstanceOf("sequence");
        for (ST st : stList) {
            merged.add("e", st.render());
        }
        return merged;
    }

    // WE need to change in this the feature
    private int calculateClassWordSize(CoolClass coolClass) {
        return 3;
    }

    @Override
    public ST visit(Program program) {
        final List<ST> constants = generateConstants();
        final List<ST> classData = new ArrayList<>();

        for (CoolClass coolClass : program.getClasses()) {
            classData.add(coolClass.accept(this));
        }

        List<ST> dataSections = new ArrayList<>();
        dataSections.addAll(constants);
        dataSections.addAll(classData);

        textSection = generateTextSection(program);
        dataSection = mergeST(dataSections);

        return templates.getInstanceOf("program")
                .add("data", dataSection.render())
                .add("text", textSection.render());
    }

    @Override
    public ST visit(CoolClass coolClass) {
        final ClassSymbol currentClass = (ClassSymbol) SymbolTable.getGlobals().lookup(coolClass.getType().getText());
        final ClassSymbol parentClass = (ClassSymbol) currentClass.getParent();

        ST protObj = templates.getInstanceOf("class_protObj")
                .add("name", coolClass.getType().getText())
                .add("tag", getNextTag())
                .add("size", calculateClassWordSize(coolClass))
                .add("dispTab", coolClass.getType().getText() + "_dispTab");

        ST initMethod = templates.getInstanceOf("class_init")
                .add("name", coolClass.getType().getText())
                .add("parent", parentClass.getName());

        return mergeST(List.of(protObj, initMethod));
    }

    @Override
    public ST visit(Method method) {
        return null;
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
