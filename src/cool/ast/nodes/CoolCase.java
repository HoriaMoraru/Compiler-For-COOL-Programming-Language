package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class CoolCase extends Expression {
    private final Expression body;
    private final List<CaseBranch> caseBranches;

    public CoolCase(Token token, Expression body, List<CaseBranch> caseBranches) {
        super(token);
        this.body = body;
        this.caseBranches = caseBranches;
    }

    public Expression getBody() {
        return body;
    }

    public List<CaseBranch> getCaseBranches() {
        return caseBranches;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
