package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import org.antlr.v4.runtime.Token;

public class CoolInt extends Expression {
    public CoolInt(Token token) {
        super(token);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
