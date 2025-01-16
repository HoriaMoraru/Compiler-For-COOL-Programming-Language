package cool.ast.visitor;

import cool.ast.abstracts.BinaryExpression;
import cool.ast.interfaces.ASTVisitor;
import cool.ast.nodes.*;
import cool.parser.CoolParser;
import cool.structures.scope.DefaultScope;
import cool.structures.scope.Scope;
import cool.structures.symbol.*;
import cool.structures.symbol.abstracts.Symbol;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TypeCheckingPassVisitor implements ASTVisitor<TypeSymbol> {
    private Scope currentScope = null;
    private ClassSymbol currentClass = null;
    @Override
    public TypeSymbol visit(Program program) {
        currentScope = SymbolTable.getGlobals();
        program.getClasses().forEach(coolClass -> coolClass.accept(this));
        return null;
    }

    @Override
    public TypeSymbol visit(CoolClass coolClass) {
        final Scope saveScope = currentScope;
        final ClassSymbol saveClass = currentClass;
        final ClassSymbol classSymbol = (ClassSymbol) SymbolTable.getGlobals().lookup(coolClass.getContext().main.getText());
        currentScope = classSymbol;
        currentClass = classSymbol;
        coolClass.getFeatures().forEach(feature -> feature.accept(this));
        currentScope = saveScope;
        currentClass = saveClass;
        return new TypeSymbol(coolClass.getType().getText());
    }

    @Override
    public TypeSymbol visit(Method method) {
        final String methodName = method.getIdentifier().getText();
        final ClassSymbol currentClass = (ClassSymbol) currentScope;
        Symbol sym = currentClass.getMethods().get(methodName);
        if (!(sym instanceof MethodSymbol methodSymbol)) {
            return null;
        }
        final Scope saveScope = currentScope;
        currentScope = methodSymbol;
        for (Formal formal : method.getFormals()) {
            formal.accept(this);
        }
        final TypeSymbol methodBodyType = method.getBody().accept(this);
        if (methodBodyType == null) {
            currentScope = saveScope;
            return null;
        }
        ClassSymbol methodBodyClass = (ClassSymbol) SymbolTable.getGlobals().lookup(methodBodyType.getName());
        if (methodBodyClass == null) {
            currentScope = saveScope;
            return null;
        }
        ClassSymbol methodReturnClass = (ClassSymbol) SymbolTable.getGlobals().lookup(method.getType().getText());
        if (methodReturnClass == null) {
            currentScope = saveScope;
            return null;
        }
        if (methodBodyClass.getName().equals("SELF_TYPE")) {
            methodBodyClass = currentClass;
        }
        if (methodReturnClass.getName().equals("SELF_TYPE")) {
            methodReturnClass = currentClass;
        }
        if (!methodReturnClass.isSubtype(methodBodyClass)) {
            SymbolTable.error(method.getContext(), method.getBody().getToken(),
                    String.format("Type %s of the body of method %s is incompatible with declared return type %s",
                            methodBodyType.getName(), method.getIdentifier().getText(), methodReturnClass.getName()));
            currentScope = saveScope;
            return null;
        }
        methodSymbol.setType(methodBodyType);
        currentScope = saveScope;
        return new TypeSymbol(methodBodyClass.getName());
    }

    @Override
    public TypeSymbol visit(Attribute attribute) {
        if (attribute.getAssign() != null) {
            final TypeSymbol assignType = attribute.getAssign().accept(this);
            if (assignType == null) {
                return null;
            }
            final ClassSymbol assignClass = (ClassSymbol) SymbolTable.getGlobals().lookup(assignType.getName());
            if (assignClass == null) {
                return null;
            }
            final ClassSymbol definitionClass = (ClassSymbol) SymbolTable.getGlobals().lookup(attribute.getType().getText());
            if (definitionClass == null) {
                return null;
            }
            if (!definitionClass.isSubtype(assignClass)) {
                SymbolTable.error(attribute.getContext(), attribute.getAssign().getToken(),
                        String.format("Type %s of initialization expression of attribute %s is incompatible with declared type %s",
                        assignType.getName(), attribute.getIdentifier().getText(), definitionClass.getName()));
                return null;
            }
        }
        return new TypeSymbol(attribute.getType().getText());
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        return new TypeSymbol(formal.getType().getText());
    }

    @Override
    public TypeSymbol visit(CoolInt coolInt) {
        return new TypeSymbol("Int");
    }

    @Override
    public TypeSymbol visit(CoolBool coolBool) {
        return new TypeSymbol("Bool");
    }

    @Override
    public TypeSymbol visit(CoolString coolString) {
        return new TypeSymbol("String");
    }

    @Override
    public TypeSymbol visit(Id id) {
        if (id.getToken().getText().equals("self")) {
            return new TypeSymbol("SELF_TYPE");
        }
        final IdSymbol idSymbol = (IdSymbol) currentScope.lookup(id.getToken().getText());
        return idSymbol != null ? idSymbol.getType() : null;
    }

    @Override
    public TypeSymbol visit(BinaryExpression binaryExpression) {
        final TypeSymbol leftType = binaryExpression.getLeft().accept(this);
        final TypeSymbol rightType = binaryExpression.getRight().accept(this);

        final String operation = binaryExpression.getOperation().getText();

        boolean isArithmeticOperation = operation.equals("+") || operation.equals("-") ||
                operation.equals("*") || operation.equals("/");
        boolean isLessThanOrEqualThan = operation.equals("<") || operation.equals("<=");

        if (isArithmeticOperation) {
            if (leftType != null && !leftType.getName().equals("Int")) {
                CoolParser.ArithmeticContext context = (CoolParser.ArithmeticContext) binaryExpression.getContext();
                SymbolTable.error(binaryExpression.getContext(), context.left.start,
                        String.format("Operand of %s has type %s instead of Int",
                                operation, leftType.getName()));
                return null;
            }
            if (rightType != null && !rightType.getName().equals("Int")) {
                CoolParser.ArithmeticContext context = (CoolParser.ArithmeticContext) binaryExpression.getContext();
                SymbolTable.error(binaryExpression.getContext(), context.right.start,
                        String.format("Operand of %s has type %s instead of Int",
                                operation, rightType.getName()));
                return null;
            }
            return new TypeSymbol("Int");
        } else {
            if (leftType != null && rightType != null &&
                operation.equals("=") && !leftType.equals(rightType) &&
                anyTypePrimordialType(leftType.getName(), rightType.getName())) {

                CoolParser.RelationalContext context = (CoolParser.RelationalContext) binaryExpression.getContext();
                SymbolTable.error(binaryExpression.getContext(), context.op,
                        String.format("Cannot compare %s with %s",
                                leftType.getName(), rightType.getName()));
                return null;
            }
            if (isLessThanOrEqualThan) {
                if (leftType != null && !leftType.getName().equals("Int")) {
                    CoolParser.RelationalContext context = (CoolParser.RelationalContext) binaryExpression.getContext();
                    SymbolTable.error(binaryExpression.getContext(), context.left.start,
                            String.format("Operand of %s has type %s instead of Int",
                                    operation, leftType.getName()));
                    return null;
                }
                if (rightType != null && !rightType.getName().equals("Int")) {
                    CoolParser.RelationalContext context = (CoolParser.RelationalContext) binaryExpression.getContext();
                    SymbolTable.error(binaryExpression.getContext(), context.right.start,
                            String.format("Operand of %s has type %s instead of Int",
                                    operation, rightType.getName()));
                    return null;
                }
            }
            return new TypeSymbol("Bool");
        }
    }

    private boolean anyTypePrimordialType(String t1, String t2) {
        return Objects.equals(t1, "Int") || Objects.equals(t1, "Bool") || Objects.equals(t1, "String") ||
               Objects.equals(t2, "Int") || Objects.equals(t2, "Bool") || Objects.equals(t2, "String");
    }

    @Override
    public TypeSymbol visit(Complement complement) {
        final TypeSymbol complementExprReturnType = complement.getExpression().accept(this);
        if (complementExprReturnType != null && !complementExprReturnType.getName().equals("Int")) {
            SymbolTable.error(complement.getContext(), complement.getExpression().getToken(),
                    String.format("Operand of ~ has type %s instead of Int", complementExprReturnType.getName()));
            return null;
        }
        return new TypeSymbol("Int");
    }

    @Override
    public TypeSymbol visit(Not not) {
        final TypeSymbol notExprReturnType = not.getExpression().accept(this);
        if (notExprReturnType != null && !notExprReturnType.getName().equals("Bool")) {
            SymbolTable.error(not.getContext(), not.getExpression().getToken(),
                    String.format("Operand of not has type %s instead of Bool", notExprReturnType.getName()));
            return null;
        }
        return new TypeSymbol("Bool");
    }

    @Override
    public TypeSymbol visit(Assignment assignment) {
        final IdSymbol idSymbol = (IdSymbol) currentScope.lookup(assignment.getIdentifier().getText());
        if (idSymbol == null) {
            return null;
        }
        final TypeSymbol assignType = idSymbol.getType();
        final TypeSymbol expressionType = assignment.getExpression().accept(this);

        if (assignType == null || expressionType == null) {
            return null;
        }

        final ClassSymbol assignClass = (ClassSymbol) SymbolTable.getGlobals().lookup(assignType.getName());
        final ClassSymbol expressionClass = (ClassSymbol) SymbolTable.getGlobals().lookup(expressionType.getName());

        if (assignClass == null || expressionClass == null) {
            System.out.println("Assign or expression class is null for assignment");
            return null;
        }

        if (!assignClass.isSubtype(expressionClass)) {
            SymbolTable.error(assignment.getContext(), assignment.getExpression().getToken(),
                String.format("Type %s of assigned expression is incompatible with declared type %s of identifier %s",
                expressionType.getName(), assignType.getName(), assignment.getIdentifier().getText()));
        }
        idSymbol.setType(assignType);
        return null;
    }

    @Override
    public TypeSymbol visit(CoolNew coolNew) {
        final String newType = coolNew.getType().getText();
        if (SymbolTable.getGlobals().lookup(newType) == null) {
            SymbolTable.error(coolNew.getContext(), coolNew.getType(),
                    String.format("new is used with undefined type %s", newType));
            return null;
        }
        return new TypeSymbol(newType);
    }

    @Override
    public TypeSymbol visit(IsVoid isVoid) {
        return new TypeSymbol("Bool");
    }

    @Override
    public TypeSymbol visit(ImplicitCall implicitCall) {
        final String methodName = implicitCall.getIdentifier().getText();
        final MethodSymbol method = (MethodSymbol) currentScope.lookup(methodName);
        if (method == null) {
            return null;
        }
        if (method.getMethod() == null) {
            return null;
        }
        List<Formal> formals = method.getMethod().getFormals();
        for (int i = 0; i < implicitCall.getArgs().size(); i++) {
            TypeSymbol argType = implicitCall.getArgs().get(i).accept(this);
            TypeSymbol paramType = new TypeSymbol(formals.get(i).getType().getText());

            if (argType == null) {
                continue;
            }
            ClassSymbol argClassSymbol = (ClassSymbol) SymbolTable.getGlobals().lookup(argType.getName());
            ClassSymbol paramClassSymbol = (ClassSymbol) SymbolTable.getGlobals().lookup(paramType.getName());

            if (argClassSymbol == null || paramClassSymbol == null) {
                return null;
            }

            if (!paramClassSymbol.isSubtype(argClassSymbol)) {
                SymbolTable.error(implicitCall.getContext(), implicitCall.getArgs().get(i).getToken(),
                        String.format("In call to method %s of class %s, actual type %s of formal parameter %s is incompatible with declared type %s",
                                method.getName(), currentClass.getName(), argClassSymbol.getName(),
                                formals.get(i).getIdentifier().getText(), paramClassSymbol.getName()));
            }
        }
        return method.getType();
    }

    @Override
    public TypeSymbol visit(Call call) {
        TypeSymbol callerType;
        if (Objects.equals(call.getCaller().getToken().getText(), "self")) {
            callerType = new TypeSymbol(currentClass.getName());
        } else {
            callerType = call.getCaller().accept(this);
        }
        if (callerType == null) {
            return null;
        }
        ClassSymbol callerClassSymbol = (ClassSymbol) SymbolTable.getGlobals().lookup(callerType.getName());
        if (callerClassSymbol == null) {
            SymbolTable.error(call.getContext(), call.getCallerType(),
                    String.format("Type %s of caller is undefined", callerType.getName()));
            return null;
        }

        ClassSymbol staticDispatchClassSymbol = null;
        if (call.getCallerType() != null) {
            staticDispatchClassSymbol = (ClassSymbol) SymbolTable.getGlobals().lookup(call.getCallerType().getText());
            if (staticDispatchClassSymbol == null) {
                SymbolTable.error(call.getContext(), call.getCallerType(),
                        String.format("Type %s of static dispatch is undefined", call.getCallerType().getText()));
                return null;
            }
            if (staticDispatchClassSymbol.getName().equals("SELF_TYPE")) {
                SymbolTable.error(call.getContext(), call.getCallerType(),
                        "Type of static dispatch cannot be SELF_TYPE");
                return null;
            }
            if (!staticDispatchClassSymbol.isSubtype(callerClassSymbol)) {

                SymbolTable.error(call.getContext(), call.getCallerType(),
                        String.format("Type %s of static dispatch is not a superclass of type %s",
                                staticDispatchClassSymbol.getName(), callerClassSymbol.getName()));
                return null;
            }
        }

        ClassSymbol methodClassSymbol = staticDispatchClassSymbol != null ? staticDispatchClassSymbol : callerClassSymbol;

        MethodSymbol method = (MethodSymbol) methodClassSymbol.lookup(call.getIdentifier().getText());
        if (method == null) {
            SymbolTable.error(call.getContext(), call.getIdentifier(),
                    String.format("Undefined method %s in class %s", call.getIdentifier().getText(), methodClassSymbol.getName()));
            return null;
        }
        if (method.getMethod() == null) {
            return null;
        }
        if (call.getArgs().size() != method.getMethod().getFormals().size()) {
            SymbolTable.error(call.getContext(), call.getIdentifier(),
                    String.format("Method %s of class %s is applied to wrong number of arguments",
                            method.getName(), methodClassSymbol.getName()));
            return null;
        }
        List<Formal> formals = method.getMethod().getFormals();
        for (int i = 0; i < call.getArgs().size(); i++) {
            TypeSymbol argType = call.getArgs().get(i).accept(this);
            TypeSymbol paramType = new TypeSymbol(formals.get(i).getType().getText());

            if (argType == null) {
                continue;
            }
            ClassSymbol argClassSymbol = (ClassSymbol) SymbolTable.getGlobals().lookup(argType.getName());
            ClassSymbol paramClassSymbol = (ClassSymbol) SymbolTable.getGlobals().lookup(paramType.getName());

            if (argClassSymbol == null || paramClassSymbol == null) {
                return null;
            }

            if (!paramClassSymbol.isSubtype(argClassSymbol)) {
                SymbolTable.error(call.getContext(), call.getArgs().get(i).getToken(),
                        String.format("In call to method %s of class %s, actual type %s of formal parameter %s is incompatible with declared type %s",
                                method.getName(), methodClassSymbol.getName(), argClassSymbol.getName(),
                                formals.get(i).getIdentifier().getText(), paramClassSymbol.getName()));
                return null;
            }
        }
        return method.getType();
    }


    @Override
    public TypeSymbol visit(CoolIf coolIf) {
        final TypeSymbol ifConditionType = coolIf.getCondition().accept(this);
        final TypeSymbol thenType = coolIf.getThenBranch().accept(this);
        final TypeSymbol elseType = coolIf.getElseBranch().accept(this);
        if (!ifConditionType.getName().equals("Bool")) {
            SymbolTable.error(coolIf.getContext(), coolIf.getCondition().getToken(),
                    String.format("If condition has type %s instead of Bool", ifConditionType.getName()));
            return new TypeSymbol("Object");
        }
        if (thenType == null || elseType == null) {
            return null;
        }
        ClassSymbol thenClass = (ClassSymbol) SymbolTable.getGlobals().lookup(thenType.getName());
        ClassSymbol elseClass = (ClassSymbol) SymbolTable.getGlobals().lookup(elseType.getName());
        if (thenClass.getName().equals("SELF_TYPE") && elseClass.getName().equals("SELF_TYPE")) {
            return new TypeSymbol("SELF_TYPE");
        }
        if (thenClass.getName().equals("SELF_TYPE")) {
            thenClass = currentClass;
        }
        if (elseClass.getName().equals("SELF_TYPE")) {
            elseClass = currentClass;
        }
        return new TypeSymbol(thenClass.findLCA(elseClass).getName());
    }

    @Override
    public TypeSymbol visit(CoolWhile coolWhile) {
        final TypeSymbol whileConditionType = coolWhile.getCondition().accept(this);
        if (!whileConditionType.getName().equals("Bool")) {
            SymbolTable.error(coolWhile.getContext(), coolWhile.getCondition().getToken(),
                    String.format("While condition has type %s instead of Bool", whileConditionType.getName()));
        }
        final Scope saveScope = currentScope;
        currentScope = new DefaultScope(currentScope);
        currentScope = saveScope;
        return new TypeSymbol("Object");
    }

    @Override
    public TypeSymbol visit(LocalDefinition localDefinition) {
        currentScope.add(new IdSymbol(localDefinition.getIdentifier().getText(), new TypeSymbol(localDefinition.getType().getText())));
        if (localDefinition.getInit() != null) {
            final TypeSymbol initType = localDefinition.getInit().accept(this);
            if (initType == null) {
                return null;
            }
            final ClassSymbol initClass = (ClassSymbol) SymbolTable.getGlobals().lookup(initType.getName());
            if (initClass == null) {
                return null;
            }
            final ClassSymbol definitionClass = (ClassSymbol) SymbolTable.getGlobals().lookup(localDefinition.getType().getText());
            if (definitionClass == null) {
                return null;
            }
            if (!definitionClass.isSubtype(initClass)) {
                SymbolTable.error(localDefinition.getContext(), localDefinition.getInit().getToken(),
                        String.format("Type %s of initialization expression of identifier %s is incompatible with declared type %s",
                                initType.getName(), localDefinition.getIdentifier().getText(), localDefinition.getType().getText()));
                return null;
            }
        }
        return new TypeSymbol(localDefinition.getType().getText());
    }

    @Override
    public TypeSymbol visit(CoolLet coolLet) {
        final Scope saveScope = currentScope;
        currentScope = new DefaultScope(currentScope);
        coolLet.getDefinitions().forEach(definition -> definition.accept(this));
        final TypeSymbol letBodyType = coolLet.getBody().accept(this);
        currentScope = saveScope;
        return letBodyType;
    }

    @Override
    public TypeSymbol visit(CoolCase coolCase) {
        final Set<ClassSymbol> caseTypes = new HashSet<>();
        coolCase.getCaseBranches().forEach(caseBranch -> {
            final TypeSymbol caseBranchType = caseBranch.accept(this);
            if (caseBranchType == null) {
                return;
            }
            final ClassSymbol classSymbol = (ClassSymbol) SymbolTable.getGlobals().lookup(caseBranchType.getName());
            if (classSymbol == null) {
                return;
            }
            caseTypes.add(classSymbol);
        });
        final ClassSymbol lca = caseTypes.stream().reduce(ClassSymbol::findLCA).orElse(null);
        return lca != null ? new TypeSymbol(lca.getName()) : null;
    }

    @Override
    public TypeSymbol visit(CaseBranch caseBranch) {
        return caseBranch.getExpression().accept(this);
    }

    @Override
    public TypeSymbol visit(BlockStatement blockStatement) {
        return null;
    }

    @Override
    public TypeSymbol visit(BlockExpression blockExpression) {
        blockExpression.getStatements().forEach(statement -> statement.accept(this));
        return blockExpression.getFinalExpression().accept(this);
    }
}
