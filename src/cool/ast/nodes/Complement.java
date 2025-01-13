package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class Complement extends Expression {
    private final Expression expression;
    private final CoolParser.ComplementContext context;

    public Complement(Token token, Expression expression, CoolParser.ComplementContext context) {
        super(token);
        this.expression = expression;
        this.context = context;
    }

    public Expression getExpression() {
        return expression;
    }

    public CoolParser.ComplementContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
