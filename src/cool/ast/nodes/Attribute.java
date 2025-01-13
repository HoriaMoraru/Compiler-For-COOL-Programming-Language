package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.abstracts.Feature;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class Attribute extends Feature {
    private final Token identifier;
    private final Token type;
    private final Expression assign;
    private final CoolParser.AttributeContext context;
    public Attribute(CoolParser.AttributeContext context, Token token, Token identifier, Token type, Expression assign) {
        super(token);
        this.context = context;
        this.identifier = identifier;
        this.type = type;
        this.assign = assign;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public Token getType() {
        return type;
    }

    public Expression getAssign() {
        return assign;
    }

    public CoolParser.AttributeContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
