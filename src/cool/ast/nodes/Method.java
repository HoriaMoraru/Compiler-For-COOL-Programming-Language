package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.abstracts.Feature;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Method extends Feature {
    private final Token identifier;
    private final List<Formal> formals;
    private final Token type;
    private final Expression body;
    private final CoolParser.MethodContext context;

    public Method(CoolParser.MethodContext context, Token token, Token identifier, List<Formal> formals, Token type, Expression body) {
        super(token);
        this.context = context;
        this.identifier = identifier;
        this.formals = formals;
        this.type = type;
        this.body = body;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public List<Formal> getFormals() {
        return formals;
    }

    public Token getType() {
        return type;
    }

    public Expression getBody() {
        return body;
    }

    public CoolParser.MethodContext getContext() {
        return context;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
