package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Call extends Expression {
    private final Expression caller;
    private final Token callerType;
    private final Token identifier;
    private final List<Expression> args;
    private final CoolParser.CallContext context;

    public Call(Token token, Expression caller, Token callerType, Token identifier, List<Expression> args, CoolParser.CallContext context) {
        super(token);
        this.caller = caller;
        this.callerType = callerType;
        this.identifier = identifier;
        this.args = args;
        this.context = context;
    }

    public Expression getCaller() {
        return caller;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public List<Expression> getArgs() {
        return args;
    }

    public Token getCallerType() {
        return callerType;
    }

    public CoolParser.CallContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
