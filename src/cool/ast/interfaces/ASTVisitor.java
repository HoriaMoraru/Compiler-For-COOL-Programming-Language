package cool.ast.interfaces;

import cool.ast.abstracts.BinaryExpression;
import cool.ast.nodes.*;

public interface ASTVisitor<T> {
    T visit(Program program);
    T visit(CoolClass coolClass);
    T visit(Method method);
    T visit(Attribute attribute);
    T visit(Formal formal);
    T visit(CoolInt coolInt);
    T visit(CoolBool coolBool);
    T visit(CoolString coolString);
    T visit(Id id);
    T visit(BinaryExpression binaryExpression);
    T visit(Complement complement);
    T visit(Not not);
    T visit(Assignment assignment);
    T visit(CoolNew coolNew);
    T visit(IsVoid isVoid);
    T visit(ImplicitCall implicitCall);
    T visit(Call call);
    T visit(CoolIf coolIf);
    T visit(CoolWhile coolWhile);
    T visit(LocalDefinition localDefinition);
    T visit(CoolLet coolLet);
    T visit(CoolCase coolCase);
    T visit(CaseBranch caseBranch);
    T visit(BlockStatement blockStatement);
    T visit(BlockExpression blockExpression);
}

