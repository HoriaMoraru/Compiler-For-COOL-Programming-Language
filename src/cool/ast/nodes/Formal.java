package cool.ast.nodes;

import cool.ast.abstracts.Definition;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class Formal extends Definition {
    private final CoolParser.FormalContext context;
    public Formal(CoolParser.FormalContext context, Token token, Token identifier, Token type) {
        super(token, identifier, type);
        this.context = context;
    }

    public CoolParser.FormalContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
