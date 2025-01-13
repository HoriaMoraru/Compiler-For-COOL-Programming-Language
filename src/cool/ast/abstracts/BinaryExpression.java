package cool.ast.abstracts;

import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public abstract class BinaryExpression extends Expression {
    private final Expression left;
    private final Expression right;
    private final Token operation;
    private final CoolParser.ExprContext context;

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public Token getOperation() {
        return operation;
    }

    protected BinaryExpression(Token token, Expression left, Expression right, Token operation, CoolParser.ExprContext context) {
        super(token);
        this.left = left;
        this.right = right;
        this.operation = operation;
        this.context = context;
    }

    public CoolParser.ExprContext getContext() {
        return context;
    }
}
