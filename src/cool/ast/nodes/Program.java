package cool.ast.nodes;

import cool.ast.abstracts.ASTNode;
import cool.ast.interfaces.ASTVisitor;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Program extends ASTNode {
    private final List<CoolClass> classes;

    public Program(Token token, List<CoolClass> classes) {
        super(token);
        this.classes = classes;
    }

    public List<CoolClass> getClasses() {
        return classes;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
