package cool.ast.visitor;

import cool.ast.abstracts.BinaryExpression;
import cool.ast.interfaces.ASTVisitor;
import cool.ast.nodes.*;
import cool.structures.scope.DefaultScope;
import cool.structures.scope.Scope;
import cool.structures.symbol.*;
import cool.structures.symbol.abstracts.Symbol;

import java.util.Objects;

public class ResolutionPassVisitor implements ASTVisitor<Void> {
    private Scope currentScope = null;
    @Override
    public Void visit(Program program) {
        currentScope = SymbolTable.getGlobals();
        program.getClasses().forEach(coolClass -> coolClass.accept(this));
        return null;
    }

    @Override
    public Void visit(CoolClass coolClass) {
        final Scope saveScope = currentScope;
        currentScope = (ClassSymbol) SymbolTable.getGlobals().lookup(coolClass.getContext().main.getText());
        coolClass.getFeatures().forEach(feature -> feature.accept(this));
        currentScope = saveScope;
        return null;
    }

    @Override
    public Void visit(Method method) {
        final String methodName = method.getIdentifier().getText();
        final ClassSymbol classSymbol = (ClassSymbol) currentScope;
        Symbol sym = classSymbol.getMethods().get(methodName);
        if (!(sym instanceof MethodSymbol methodSymbol)) {
            return null;
        }
        final Symbol parentSym = currentScope.getParent().lookup(methodName);
        if ((parentSym instanceof MethodSymbol parentMethodSymbol) && checkOverride(method, methodName, classSymbol, methodSymbol, parentMethodSymbol))
            return null;

        final Scope saveScope = currentScope;
        currentScope = methodSymbol;
        for (Formal formal : method.getFormals()) {
            formal.accept(this);
        }
        method.getBody().accept(this);
        currentScope = saveScope;
        return null;
    }

    private boolean checkOverride(Method method, String methodName, ClassSymbol currentClass, MethodSymbol methodSymbol, MethodSymbol parentMethodSymbol) {
        if (methodSymbol == null || methodSymbol.getMethod() == null) {
            return false;
        }

        if (parentMethodSymbol == null || parentMethodSymbol.getMethod() == null) {
            return false;
        }
        if (method.getFormals() != null && method.getFormals().size() != parentMethodSymbol.getMethod().getFormals().size()) {
            SymbolTable.error(method.getContext(), method.getIdentifier(),
                    String.format("Class %s overrides method %s with different number of formal parameters",
                            currentClass.getName(), methodName));
            return true;
        }

        for (int i = 0; i < method.getFormals().size(); i++) {
            Formal currentFormal = method.getFormals().get(i);
            Formal parentFormal = parentMethodSymbol.getMethod().getFormals().get(i);

            String currentFormalType = currentFormal.getType().getText();
            String parentFormalType = parentFormal.getType().getText();

            if (!currentFormalType.equals(parentFormalType)) {
                SymbolTable.error(currentFormal.getContext(), currentFormal.getType(),
                        String.format("Class %s overrides method %s but changes type of formal parameter %s from %s to %s",
                                currentClass.getName(), methodName, currentFormal.getIdentifier().getText(),
                                parentFormalType, currentFormalType));
                return true;
            }
        }

        if (!Objects.equals(methodSymbol.getType().getName(), parentMethodSymbol.getType().getName())) {
            SymbolTable.error(method.getContext(), method.getContext().type,
                    String.format("Class %s overrides method %s but changes return type from %s to %s",
                            currentClass.getName(), methodName,
                            parentMethodSymbol.getType().getName(), methodSymbol.getType().getName()));
            return true;
        }
        return false;
    }

    @Override
    public Void visit(Attribute attribute) {
        final String attributeName = attribute.getIdentifier().getText();
        final ClassSymbol currentClass = (ClassSymbol) currentScope;
        if (currentScope.getParent().lookup(attributeName) != null) {
            SymbolTable.error(attribute.getContext(), attribute.getIdentifier(),
                    String.format("Class %s redefines inherited attribute %s", currentClass.getName(), attributeName));
            return null;
        }
        if (attribute.getAssign() != null) {
            attribute.getAssign().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        final String formalName = formal.getIdentifier().getText();
        final String formalType = formal.getType().getText();
        final ClassSymbol currentClass = (ClassSymbol) currentScope.getParent();
        final MethodSymbol currentMethod = (MethodSymbol) currentScope;
        if (SymbolTable.getGlobals().lookup(formalType) == null) {
            SymbolTable.error(formal.getContext(), formal.getType(),
                    String.format("Method %s of class %s has formal parameter %s with undefined type %s",
                            currentMethod.getName(), currentClass.getName(), formalName, formal.getType().getText()));
            return null;
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
        final String idName = id.getToken().getText();
        Symbol idSymbol = currentScope.lookup(idName);
        if (idSymbol == null && !Objects.equals(idName, "self")) {
            SymbolTable.error(id.getContext(), id.getToken(),
                    String.format("Undefined identifier %s", id.getToken().getText()));
            return null;
        }
        id.setScope(currentScope);
        id.setSymbol((IdSymbol) idSymbol);
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
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
        final String localDefinitionName = localDefinition.getIdentifier().getText();
        if (SymbolTable.getGlobals().lookup(localDefinition.getType().getText()) == null) {
            SymbolTable.error(localDefinition.getContext(), localDefinition.getType(),
                    String.format("Let variable %s has undefined type %s",
                            localDefinitionName, localDefinition.getType().getText()));
            return null;
        }
        if (localDefinition.getInit() != null) {
            localDefinition.getInit().accept(this);
        }
        currentScope.add(new IdSymbol(localDefinitionName, new TypeSymbol(localDefinition.getType().getText())));
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
        coolCase.getCaseBranches().forEach(caseBranch -> caseBranch.accept(this));
        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        final String caseBranchName = caseBranch.getIdentifier().getText();
        final String caseBranchType = caseBranch.getType().getText();
        if (SymbolTable.getGlobals().lookup(caseBranchType) == null) {
            SymbolTable.error(caseBranch.getContext(), caseBranch.getType(),
                    String.format("Case variable %s has undefined type %s", caseBranchName, caseBranchType));
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
        blockExpression.getStatements().forEach(statement -> statement.accept(this));
        return null;
    }
}
