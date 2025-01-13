package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class ImplicitCall extends Expression {
    private final Token identifier;
    private final List<Expression> args;
    private final CoolParser.Implicit_callContext context;

    public ImplicitCall(Token token, Token identifier, List<Expression> args, CoolParser.Implicit_callContext context) {
        super(token);
        this.identifier = identifier;
        this.args = args;
        this.context = context;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public List<Expression> getArgs() {
        return args;
    }

    public CoolParser.Implicit_callContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
