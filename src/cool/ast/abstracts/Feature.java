package cool.ast.abstracts;

import org.antlr.v4.runtime.Token;

public abstract class Feature extends ASTNode{
    protected Feature(Token token) {
        super(token);
    }
}
