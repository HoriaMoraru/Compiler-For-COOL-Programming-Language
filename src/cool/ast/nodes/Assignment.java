package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class Assignment extends Expression {
    private final Token identifier;
    private final Expression expression;
    private final CoolParser.AssignmentContext context;

    public Assignment(Token token, Token identifier, Expression expression, CoolParser.AssignmentContext context) {
        super(token);
        this.identifier = identifier;
        this.expression = expression;
        this.context = context;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public CoolParser.AssignmentContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
