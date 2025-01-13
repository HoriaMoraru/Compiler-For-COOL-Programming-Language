package cool.ast.abstracts;

import cool.ast.interfaces.ASTVisitor;
import org.antlr.v4.runtime.Token;

public abstract class ASTNode {
    private final Token token;

    protected ASTNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        throw new UnsupportedOperationException("This method should not be called");
    }
}
