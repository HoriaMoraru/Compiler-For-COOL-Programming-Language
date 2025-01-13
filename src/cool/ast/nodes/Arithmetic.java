package cool.ast.nodes;

import cool.ast.abstracts.BinaryExpression;
import cool.ast.abstracts.Expression;
import cool.ast.interfaces.ASTVisitor;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.Token;

public class Arithmetic extends BinaryExpression {

    public Arithmetic(Token token, Expression left, Expression right, Token operation, CoolParser.ArithmeticContext context) {
        super(token, left, right, operation, context);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
