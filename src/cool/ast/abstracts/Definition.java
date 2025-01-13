package cool.ast.abstracts;

import org.antlr.v4.runtime.Token;

public class Definition extends ASTNode {
    private final Token identifier;
    private final Token type;

    protected Definition(Token token, Token identifier, Token type) {
        super(token);
        this.identifier = identifier;
        this.type = type;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public Token getType() {
        return type;
    }
}
