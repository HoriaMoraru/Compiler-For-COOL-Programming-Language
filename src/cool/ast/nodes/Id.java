package cool.ast.nodes;

import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import cool.structures.scope.Scope;
import cool.structures.symbol.IdSymbol;
import org.antlr.v4.runtime.Token;

public class Id extends Expression {
    private final CoolParser.IdContext context;
    private IdSymbol symbol;
    private Scope scope;
    public Id(Token token, CoolParser.IdContext context) {
        super(token);
        this.context = context;
    }

    public CoolParser.IdContext getContext() {
        return context;
    }

    public IdSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(IdSymbol symbol) {
        this.symbol = symbol;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
