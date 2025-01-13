package cool.ast.visitor;

import cool.ast.abstracts.BinaryExpression;
import cool.ast.interfaces.ASTVisitor;
import cool.ast.nodes.*;
import cool.structures.symbol.ClassSymbol;
import cool.structures.symbol.SymbolTable;

import java.util.Set;

public class ClassResolutionPassVisitor implements ASTVisitor<Void> {
    private static final Set<String> ILLEGAL_PARENT_CLASS_NAMES =
            Set.of("Int", "Bool", "String", "SELF_TYPE");
    @Override
    public Void visit(Program program) {
        program.getClasses().forEach(coolClass -> coolClass.accept(this));
        return null;
    }

        @Override
        public Void visit(CoolClass coolClass) {
            final String definedClassName = coolClass.getContext().main.getText();
            final String parentName = (coolClass.getInheritance() == null)
                    ? SymbolTable.getBaseInheritanceClass().getName()
                    : coolClass.getInheritance().getText();

            final ClassSymbol definedClass = (ClassSymbol) SymbolTable.getGlobals().lookup(definedClassName);
            if (definedClass == null) {
                System.out.println("This should have already been defined");
                return null;
            }

            if (ILLEGAL_PARENT_CLASS_NAMES.contains(parentName)) {
                SymbolTable.error(coolClass.getContext(), coolClass.getContext().inherited,
                        String.format("Class %s has illegal parent %s", definedClassName, parentName));
                return null;
            }
            final ClassSymbol parent = (ClassSymbol) SymbolTable.getGlobals().lookup(parentName);
            if (parent == null) {
                SymbolTable.error(coolClass.getContext(), coolClass.getContext().inherited,
                        String.format("Class %s has undefined parent %s", definedClassName, parentName));
                return null;
            }

            definedClass.setParentScope(parent);
            return null;
        }

    @Override
    public Void visit(Method method) {
        return null;
    }

    @Override
    public Void visit(Attribute attribute) {
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        return null;
    }

    @Override
    public Void visit(CoolInt coolInt) {
        return null;
    }

    @Override
    public Void visit(CoolBool coolBool) {
        return null;
    }

    @Override
    public Void visit(CoolString coolString) {
        return null;
    }

    @Override
    public Void visit(Id id) {
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        return null;
    }

    @Override
    public Void visit(Complement complement) {
        return null;
    }

    @Override
    public Void visit(Not not) {
        return null;
    }

    @Override
    public Void visit(Assignment assignment) {
        return null;
    }

    @Override
    public Void visit(CoolNew coolNew) {
        return null;
    }

    @Override
    public Void visit(IsVoid isVoid) {
        return null;
    }

    @Override
    public Void visit(ImplicitCall implicitCall) {
        return null;
    }

    @Override
    public Void visit(Call call) {
        return null;
    }

    @Override
    public Void visit(CoolIf coolIf) {
        return null;
    }

    @Override
    public Void visit(CoolWhile coolWhile) {
        return null;
    }

    @Override
    public Void visit(LocalDefinition localDefinition) {
        return null;
    }

    @Override
    public Void visit(CoolLet coolLet) {
        return null;
    }

    @Override
    public Void visit(CoolCase coolCase) {
        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        return null;
    }

    @Override
    public Void visit(BlockStatement blockStatement) {
        return null;
    }

    @Override
    public Void visit(BlockExpression blockExpression) {
        return null;
    }
}
