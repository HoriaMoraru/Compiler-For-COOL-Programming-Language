package cool.ast.nodes;

import cool.ast.abstracts.Definition;
import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class LocalDefinition extends Definition {
    private final Expression init;
    private final CoolParser.DefinitionContext context;

    public LocalDefinition(Token token, Token identifier, Token type, CoolParser.DefinitionContext context, Expression init) {
        super(token, identifier, type);
        this.context = context;
        this.init = init;
    }

    public Expression getInit() {
        return init;
    }

    public CoolParser.DefinitionContext getContext() {
        return context;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
