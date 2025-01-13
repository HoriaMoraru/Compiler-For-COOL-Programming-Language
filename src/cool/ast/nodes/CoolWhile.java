package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class CoolWhile extends Expression {
    private final Expression condition;
    private final Expression body;
    private final CoolParser.WhileContext context;

    public CoolWhile(Token token, Expression condition, Expression body, CoolParser.WhileContext context) {
        super(token);
        this.condition = condition;
        this.body = body;
        this.context = context;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getBody() {
        return body;
    }

    public CoolParser.WhileContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
