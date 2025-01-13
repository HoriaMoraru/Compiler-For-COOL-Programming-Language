package cool.ast.visitor;

import cool.ast.abstracts.BinaryExpression;
import cool.ast.interfaces.ASTVisitor;
import cool.ast.nodes.*;
import cool.structures.scope.DefaultScope;
import cool.structures.scope.Scope;
import cool.structures.symbol.*;

import java.util.HashSet;
import java.util.Set;

public class CycleDetectionAndDefinitionPassVisitor implements ASTVisitor<Void> {
    private static final Set<String> ILLEGAL_ATTRIBUTE_NAMES = Set.of("self");
    private static final Set<String> ILLEGAL_FORMAL_TYPES = Set.of("SELF_TYPE");
    private Scope currentScope = null;

    @Override
    public Void visit(Program program) {
        currentScope = SymbolTable.getGlobals();
        program.getClasses().forEach(coolClass -> coolClass.accept(this));
        return null;
    }

    @Override
    public Void visit(CoolClass coolClass) {
        final String resolvedClassName = coolClass.getContext().main.getText();

        final ClassSymbol resolvedClass = (ClassSymbol) SymbolTable.getGlobals().lookup(resolvedClassName);
        if (resolvedClass == null) {
            System.out.println("This should have already been resolved");
            return null;
        }
        final Set<Scope> visited = new HashSet<>();
        if (hasCycle(resolvedClass, visited)) {
            SymbolTable.error(coolClass.getContext(), coolClass.getContext().main,
                    String.format("Inheritance cycle for class %s", resolvedClassName));
        }
        final Scope saveScope = currentScope;
        currentScope = resolvedClass;
        coolClass.getFeatures().forEach(feature -> feature.accept(this));
        currentScope = saveScope;
        return null;
    }

    private boolean hasCycle(Scope classSymbol, Set<Scope> visited) {
        Scope parent = classSymbol.getParent();
        if (parent == null) {
            return false;
        }
        if (visited.contains(parent)) {
            return true;
        }
        visited.add(classSymbol);
        return hasCycle(parent, visited);
    }

    @Override
    public Void visit(Method method) {
        final String methodName = method.getIdentifier().getText();
        final ClassSymbol currentClass = (ClassSymbol) currentScope;
        final MethodSymbol methodSymbol = new MethodSymbol(methodName,
                new TypeSymbol(method.getContext().type.getText()),
                currentScope, method);

        if (!currentScope.add(methodSymbol)) {
            SymbolTable.error(method.getContext(), method.getIdentifier(),
                    String.format("Class %s redefines method %s", currentClass.getName(), methodName));
            return null;
        }
        final Scope saveScope = currentScope;
        currentScope = methodSymbol;
        for (Formal formal : method.getFormals()) {
            formal.accept(this);
        }
        method.getBody().accept(this);
        currentScope = saveScope;
        return null;
    }

    @Override
    public Void visit(Attribute attribute) {
        final String attributeName = attribute.getIdentifier().getText();
        final ClassSymbol currentClass = (ClassSymbol) currentScope;
        if (ILLEGAL_ATTRIBUTE_NAMES.contains(attributeName)) {
            SymbolTable.error(attribute.getContext(), attribute.getIdentifier(),
                    String.format("Class %s has attribute with illegal name %s", currentClass.getName(), attributeName));
            return null;
        }
        final IdSymbol attributeSymbol = new IdSymbol(attributeName, new TypeSymbol(attribute.getType().getText()));
        if (!currentScope.add(attributeSymbol)) {
            SymbolTable.error(attribute.getContext(), attribute.getIdentifier(),
                    String.format("Class %s redefines attribute %s", currentClass.getName(), attributeName));
            return null;
        }
        if(SymbolTable.getGlobals().lookup(attribute.getType().getText()) == null) {
            SymbolTable.error(attribute.getContext(), attribute.getType(),
                    String.format("Class %s has attribute %s with undefined type %s",
                            currentClass.getName(), attributeName, attribute.getType().getText()));
        }
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        final String formalName = formal.getIdentifier().getText();
        final ClassSymbol currentClass = (ClassSymbol) currentScope.getParent();
        final MethodSymbol currentMethod = (MethodSymbol) currentScope;
        if (ILLEGAL_ATTRIBUTE_NAMES.contains(formalName)) {
            SymbolTable.error(formal.getContext(), formal.getIdentifier(),
                    String.format("Method %s of class %s has formal parameter with illegal name %s",
                            currentMethod.getName(), currentClass.getName(), formalName));
            return null;
        }
        if (ILLEGAL_FORMAL_TYPES.contains(formal.getType().getText())) {
            SymbolTable.error(formal.getContext(), formal.getType(),
                    String.format("Method %s of class %s has formal parameter %s with illegal type %s",
                            currentMethod.getName(), currentClass.getName(), formalName, formal.getType().getText()));
            return null;
        }
        final IdSymbol formalSymbol = new IdSymbol(formalName, new TypeSymbol(formal.getType().getText()));
        if (!currentScope.add(formalSymbol)) {
            SymbolTable.error(formal.getContext(), formal.getIdentifier(),
                    String.format("Method %s of class %s redefines formal parameter %s",
                            currentMethod.getName(), currentClass.getName(), formalName));
        }
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
        if (ILLEGAL_ATTRIBUTE_NAMES.contains(assignment.getIdentifier().getText())) {
            SymbolTable.error(assignment.getContext(), assignment.getIdentifier(),
                    String.format("Cannot assign to %s", assignment.getIdentifier().getText()));
        }
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
        final String localDefinitionName = localDefinition.getIdentifier().getText();
        if (ILLEGAL_ATTRIBUTE_NAMES.contains(localDefinitionName)) {
            SymbolTable.error(localDefinition.getContext(), localDefinition.getIdentifier(),
                    String.format("Let variable has illegal name %s", localDefinitionName));
            return null;
        }
        return null;
    }

    @Override
    public Void visit(CoolLet coolLet) {
        final Scope saveScope = currentScope;
        currentScope = new DefaultScope(currentScope);
        coolLet.getDefinitions().forEach(definition -> definition.accept(this));
        currentScope = saveScope;
        return null;
    }

    @Override
    public Void visit(CoolCase coolCase) {
        final Scope saveScope = currentScope;
        currentScope = new DefaultScope(currentScope);
        coolCase.getCaseBranches().forEach(caseBranch -> caseBranch.accept(this));
        currentScope = saveScope;
        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        final String caseBranchName = caseBranch.getIdentifier().getText();
        final String caseBranchType = caseBranch.getType().getText();
        if (ILLEGAL_ATTRIBUTE_NAMES.contains(caseBranchName)) {
            SymbolTable.error(caseBranch.getContext(), caseBranch.getIdentifier(),
                    String.format("Case variable has illegal name %s", caseBranchName));
            return null;
        }
        if (ILLEGAL_FORMAL_TYPES.contains(caseBranchType)) {
            SymbolTable.error(caseBranch.getContext(), caseBranch.getType(),
                    String.format("Case variable %s has illegal type %s", caseBranchName, caseBranchType));
            return null;
        }
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
