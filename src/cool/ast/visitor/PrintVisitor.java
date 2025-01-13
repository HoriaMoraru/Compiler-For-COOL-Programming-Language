package cool.ast.visitor;

import cool.ast.abstracts.BinaryExpression;
import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.ast.nodes.*;

public class PrintVisitor implements ASTVisitor<Void> {
    private int indent = 0;

    void printIndent(String str) {
        for (int i = 0; i < indent; ++i) {
            System.out.print("  ");
        }
        System.out.println(str);
    }

    @Override
    public Void visit(Program program) {
        printIndent("program");
        ++indent;
        program.getClasses().forEach(c -> c.accept(this));
        --indent;
        return null;
    }

    @Override
    public Void visit(CoolClass coolClass) {
        printIndent(coolClass.getToken().getText());
        ++indent;
        printIndent(coolClass.getType().getText());
        if (coolClass.getInheritance() != null) {
            printIndent(coolClass.getInheritance().getText());
        }
        coolClass.getFeatures().forEach(feat -> feat.accept(this));
        --indent;
        return null;
    }

    @Override
    public Void visit(Method method) {
        printIndent("method");
        ++indent;
        printIndent(method.getIdentifier().getText());
        method.getFormals().forEach(formal -> formal.accept(this));
        printIndent(method.getType().getText());
        method.getBody().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Attribute attribute) {
        printIndent("attribute");
        ++indent;
        printIndent(attribute.getIdentifier().getText());
        printIndent(attribute.getType().getText());
        if (attribute.getAssign() != null) {
            attribute.getAssign().accept(this);
        }
        --indent;
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        printIndent("formal");
        ++indent;
        printIndent(formal.getIdentifier().getText());
        printIndent(formal.getType().getText());
        --indent;
        return null;
    }

    @Override
    public Void visit(CoolInt coolInt) {
        printIndent(coolInt.getToken().getText());
        return null;
    }

    @Override
    public Void visit(CoolBool coolBool) {
        printIndent(coolBool.getToken().getText());
        return null;
    }

    @Override
    public Void visit(CoolString coolString) {
        final String sanitizedString = sanitizeString(coolString.getToken().getText());
        printIndent(sanitizedString);
        return null;
    }

    private String sanitizeString(String str) {
        str = str.replace("\"", "");

        if (str.contains("\\n")) {
            str = str.replace("\\n", "\n");
        }
        if (str.contains("\\t")) {
            str = str.replace("\\t", "\t");
        }
        if (str.contains("\\b")) {
            str = str.replace("\\b", "\b");
        }
        if (str.contains("\\f")) {
            str = str.replace("\\f", "\f");
        }
        if (str.contains("\\\\")) {
            str = str.replace("\\\\", "\\");
        }
        else if (str.contains("\\") && !str.contains("\\0")) {
            str = str.replace("\\", "");
        }
        return str;
    }

    @Override
    public Void visit(Id id) {
        printIndent(id.getToken().getText());
        return null;
    }

    @Override
    public Void visit(BinaryExpression binaryExpression) {
        printIndent(binaryExpression.getOperation().getText());
        ++indent;
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Complement complement) {
        printIndent(complement.getToken().getText());
        ++indent;
        complement.getExpression().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Not not) {
        printIndent(not.getToken().getText());
        ++indent;
        not.getExpression().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Assignment assignment) {
        printIndent(assignment.getToken().getText());
        ++indent;
        printIndent(assignment.getIdentifier().getText());
        assignment.getExpression().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(CoolNew coolNew) {
        printIndent(coolNew.getToken().getText());
        ++indent;
        printIndent(coolNew.getType().getText());
        --indent;
        return null;
    }

    @Override
    public Void visit(IsVoid isVoid) {
        printIndent(isVoid.getToken().getText());
        ++indent;
        isVoid.getExpression().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(ImplicitCall implicitCall) {
        printIndent("implicit dispatch");
        ++indent;
        printIndent(implicitCall.getIdentifier().getText());
        implicitCall.getArgs().forEach(arg -> arg.accept(this));
        --indent;
        return null;
    }

    @Override
    public Void visit(Call call) {
        printIndent(".");
        ++indent;
        call.getCaller().accept(this);
        if (call.getCallerType() != null) {
            printIndent(call.getCallerType().getText());
        }
        printIndent(call.getIdentifier().getText());
        call.getArgs().forEach(arg -> arg.accept(this));
        --indent;
        return null;
    }

    @Override
    public Void visit(CoolIf coolIf) {
        printIndent(coolIf.getToken().getText());
        ++indent;
        coolIf.getCondition().accept(this);
        coolIf.getThenBranch().accept(this);
        coolIf.getElseBranch().accept(this);
        --indent;
        return null;
    }

    public Void visit(CoolWhile coolWhile) {
        printIndent(coolWhile.getToken().getText());
        ++indent;
        coolWhile.getCondition().accept(this);
        coolWhile.getBody().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(LocalDefinition localDefinition) {
        printIndent("local");
        ++indent;
        printIndent(localDefinition.getIdentifier().getText());
        printIndent(localDefinition.getType().getText());

        if (localDefinition.getInit() != null) {
            localDefinition.getInit().accept(this);
        }
        --indent;
        return null;
    }

    @Override
    public Void visit(CoolLet coolLet) {
        printIndent(coolLet.getToken().getText());
        ++indent;
        coolLet.getDefinitions().forEach(definition -> definition.accept(this));
        coolLet.getBody().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        printIndent("case branch");
        ++indent;
        printIndent(caseBranch.getIdentifier().getText());
        printIndent(caseBranch.getType().getText());
        caseBranch.getExpression().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(CoolCase coolCase) {
        printIndent(coolCase.getToken().getText());
        ++indent;
        coolCase.getBody().accept(this);
        coolCase.getCaseBranches().forEach(caseBranch -> caseBranch.accept(this));
        --indent;
        return null;
    }

    @Override
    public Void visit(BlockExpression blockExpression) {
        printIndent("block");
        ++indent;
        if (blockExpression.getStatements() != null) {
            blockExpression.getStatements().forEach(statement -> statement.accept(this));
        }
        blockExpression.getFinalExpression().accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(BlockStatement blockStatement) {
        if (blockStatement.getStatement() instanceof Attribute) {
            Attribute attribute = (Attribute) blockStatement.getStatement();
            attribute.accept(this);
        } else {
            Expression expression = (Expression) blockStatement.getStatement();
            expression.accept(this);
        }
        return null;
    }
}
