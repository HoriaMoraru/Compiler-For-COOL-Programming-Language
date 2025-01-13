package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class CoolNew extends Expression {
    private final Token type;
    private final CoolParser.NewContext context;

    public CoolNew(Token token, Token type, CoolParser.NewContext context) {
        super(token);
        this.type = type;
        this.context = context;
    }

    public Token getType() {
        return type;
    }

    public CoolParser.NewContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
