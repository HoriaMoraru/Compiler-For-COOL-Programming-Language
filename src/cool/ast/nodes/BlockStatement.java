package cool.ast.nodes;

import cool.ast.abstracts.ASTNode;
import cool.ast.interfaces.ASTVisitor;
import org.antlr.v4.runtime.Token;

public class BlockStatement extends ASTNode {
    private final ASTNode statement;

    public BlockStatement(Token token, ASTNode statement) {
        super(token);
        this.statement = statement;
    }

    public ASTNode getStatement() {
        return statement;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
