package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class BlockExpression extends Expression {
    private final List<Expression> statements;
    private final Expression finalExpression;

    public BlockExpression(Token token, List<Expression> statements, Expression finalExpression) {
        super(token);
        this.statements = statements;
        this.finalExpression = finalExpression;
    }

    public List<Expression> getStatements() {
        return statements;
    }

    public Expression getFinalExpression() {
        return finalExpression;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
