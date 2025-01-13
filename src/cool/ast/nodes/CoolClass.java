package cool.ast.nodes;

import cool.ast.abstracts.ASTNode;
import cool.ast.abstracts.Feature;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class CoolClass extends ASTNode {
    private final Token type;
    private final Token inheritance;
    private final List<Feature> features;
    private final CoolParser.ClassContext context;

    public CoolClass(CoolParser.ClassContext context, Token token, Token type, Token inheritance, List<Feature> features) {
        super(token);
        this.context = context;
        this.type = type;
        this.inheritance = inheritance;
        this.features = features;
    }

    public Token getType() {
        return type;
    }

    public Token getInheritance() {
        return inheritance;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public CoolParser.ClassContext getContext() {
        return context;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
