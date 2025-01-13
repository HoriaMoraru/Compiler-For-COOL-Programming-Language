package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import org.antlr.v4.runtime.Token;

public class IsVoid extends Expression {
    private final Expression expression;

    public IsVoid(Token token, Expression expression) {
        super(token);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
