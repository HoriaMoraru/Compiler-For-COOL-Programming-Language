package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class CoolIf extends Expression {
    private final Expression condition;
    private final Expression thenBranch;
    private final Expression elseBranch;
    private final CoolParser.IfContext context;

    public CoolIf(Token token, Expression condition, Expression thenBranch, Expression elseBranch, CoolParser.IfContext context) {
        super(token);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.context = context;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getThenBranch() {
        return thenBranch;
    }

    public Expression getElseBranch() {
        return elseBranch;
    }

    public CoolParser.IfContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
