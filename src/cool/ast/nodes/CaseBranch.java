package cool.ast.nodes;

import cool.ast.abstracts.ASTNode;
import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class CaseBranch extends ASTNode {

    private final Token identifier;
    private final Token type;
    private final Expression expression;
    private final CoolParser.Case_branchContext context;

    public CaseBranch(Token token, Token identifier, Token type, Expression expression, CoolParser.Case_branchContext context) {
        super(token);
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
        this.context = context;
    }

    public Expression getExpression() {
        return expression;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public Token getType() {
        return type;
    }

    public CoolParser.Case_branchContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
