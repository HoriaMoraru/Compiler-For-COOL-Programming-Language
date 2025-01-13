package cool.ast.nodes;

import cool.ast.abstracts.Definition;
import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class CoolLet extends Expression {
    private final List<Definition> definitions;
    private final Expression body;

    public CoolLet(Token token, List<Definition> definitions, Expression body) {
        super(token);
        this.definitions = definitions;
        this.body = body;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public Expression getBody() {
        return body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
