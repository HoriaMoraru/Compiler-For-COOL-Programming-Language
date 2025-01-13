package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class Not extends Expression {
    private final Expression expression;
    private final CoolParser.NegationContext context;

    public Not(Token token, Expression expression, CoolParser.NegationContext context) {
        super(token);
        this.expression = expression;
        this.context = context;
    }

    public Expression getExpression() {
        return expression;
    }

    public CoolParser.NegationContext getContext() {
        return context;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
