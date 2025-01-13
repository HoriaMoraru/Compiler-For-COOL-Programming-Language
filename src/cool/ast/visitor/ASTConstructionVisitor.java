package cool.ast.visitor;

import cool.ast.abstracts.ASTNode;
import cool.ast.abstracts.Definition;
import cool.ast.abstracts.Expression;
import cool.ast.abstracts.Feature;
import cool.ast.nodes.*;
import cool.parser.*;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class ASTConstructionVisitor extends CoolParserBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitProgram(CoolParser.ProgramContext ctx) {
        final List<CoolClass> classes = new ArrayList<>();

        ctx.class_().forEach(classContext -> classes.add( (CoolClass) visit(classContext)));

        return new Program(ctx.start, classes);
    }

    @Override
    public ASTNode visitClass(CoolParser.ClassContext ctx) {
        final List<Feature> features = new ArrayList<>();

        ctx.feature().forEach(featureContext -> features.add( (Feature) visit(featureContext)));

        return new CoolClass(ctx, ctx.start, ctx.main, ctx.inherited, features);
    }

    @Override
    public ASTNode visitFeature(CoolParser.FeatureContext ctx) {
        final CoolParser.AttributeContext attributeContext = ctx.attribute();
        final CoolParser.MethodContext methodContext = ctx.method();

        if (attributeContext != null) {
            return visitAttribute(attributeContext);
        } else {
            return visitMethod(methodContext);
        }
    }

    @Override
    public ASTNode visitAttribute(CoolParser.AttributeContext ctx) {
        final Expression expression = ctx.expr() != null ? (Expression) visit(ctx.expr()) : null;
        final Token typeToken = ctx.TYPE() != null
                ? ctx.TYPE().getSymbol()
                : ctx.SELF_TYPE().getSymbol();

        return new Attribute(ctx,
                ctx.start,
                ctx.IDENTIFIER().getSymbol(),
                typeToken,
                expression);
    }

    @Override
    public ASTNode visitMethod(CoolParser.MethodContext ctx) {
        final Expression expression = (Expression) visit(ctx.expr());
        final List<Formal> formals = new ArrayList<>();

        ctx.formals.forEach(formalContext -> formals.add( (Formal) visit(formalContext)));

        return new Method(ctx,
                          ctx.start,
                          ctx.IDENTIFIER().getSymbol(),
                          formals,
                          ctx.type,
                          expression);
    }

    @Override
    public ASTNode visitFormal(CoolParser.FormalContext ctx) {
        return new Formal(ctx, ctx.start, ctx.IDENTIFIER().getSymbol(), ctx.type);
    }

    @Override
    public ASTNode visitBool(CoolParser.BoolContext ctx) {
        return new CoolBool(ctx.start);
    }

    @Override
    public ASTNode visitInt(CoolParser.IntContext ctx) {
        return new CoolInt(ctx.start);
    }

    @Override
    public ASTNode visitString(CoolParser.StringContext ctx) {
        return new CoolString(ctx.start);
    }

    @Override
    public ASTNode visitId(CoolParser.IdContext ctx) {
        return new Id(ctx.start, ctx);
    }

    @Override
    public ASTNode visitArithmetic(CoolParser.ArithmeticContext ctx) {
        final Expression left = (Expression) visit(ctx.left);
        final Expression right = (Expression) visit(ctx.right);

        return new Arithmetic(ctx.start, left, right, ctx.op, ctx);
    }

    @Override
    public ASTNode visitRelational(CoolParser.RelationalContext ctx) {
        final Expression left = (Expression) visit(ctx.left);
        final Expression right = (Expression) visit(ctx.right);

        return new Relational(ctx.start, left, right, ctx.op, ctx);
    }

    @Override
    public ASTNode visitComplement(CoolParser.ComplementContext ctx) {
        final Expression expression = (Expression) visit(ctx.expr());

        return new Complement(ctx.start, expression, ctx);
    }

    @Override
    public ASTNode visitParan_expression(CoolParser.Paran_expressionContext ctx) {
        return ctx.expr().accept(this);
    }

    @Override
    public ASTNode visitNegation(CoolParser.NegationContext ctx) {
        final Expression expression = (Expression) visit(ctx.expr());

        return new Not(ctx.start, expression, ctx);
    }

    @Override
    public ASTNode visitAssignment(CoolParser.AssignmentContext ctx) {
        final Expression expression = (Expression) visit(ctx.expr());

        return new Assignment(ctx.ASSIGN().getSymbol(), ctx.IDENTIFIER().getSymbol(), expression, ctx);
    }

    @Override
    public ASTNode visitNew(CoolParser.NewContext ctx) {
        return new CoolNew(ctx.start, ctx.type, ctx);
    }

    @Override
    public ASTNode visitIsvoid(CoolParser.IsvoidContext ctx) {
        final Expression expression = (Expression) visit(ctx.expr());

        return new IsVoid(ctx.start, expression);
    }

    @Override
    public ASTNode visitImplicit_call(CoolParser.Implicit_callContext ctx) {
        final List<Expression> args = new ArrayList<>();
        ctx.args.forEach(arg -> args.add( (Expression) visit(arg)));

        return new ImplicitCall(ctx.start, ctx.IDENTIFIER().getSymbol(), args, ctx);
    }

    @Override
    public ASTNode visitCall(CoolParser.CallContext ctx) {
        final Expression caller = (Expression) visit(ctx.caller);
        final Token callerType = ctx.type != null ? ctx.type : null;
        final List<Expression> args = new ArrayList<>();
        ctx.args.forEach(arg -> args.add( (Expression) visit(arg)));

        return new Call(ctx.start,
                        caller,
                        callerType,
                        ctx.IDENTIFIER().getSymbol(),
                        args,
                        ctx);
    }

    @Override
    public ASTNode visitIf(CoolParser.IfContext ctx) {
        final Expression condition = (Expression) visit(ctx.condition);
        final Expression thenBranch = (Expression) visit(ctx.thenBranch);
        final Expression elseBranch = (Expression) visit(ctx.elseBranch);

        return new CoolIf(ctx.start, condition, thenBranch, elseBranch, ctx);
    }

    @Override
    public ASTNode visitWhile(CoolParser.WhileContext ctx) {
        final Expression condition = (Expression) visit(ctx.condition);
        final Expression body = (Expression) visit(ctx.body);

        return new CoolWhile(ctx.start, condition, body, ctx);
    }

    @Override
    public ASTNode visitDefinition(CoolParser.DefinitionContext ctx) {
        final Expression init = ctx.expr() != null ? (Expression) visit(ctx.expr()) : null;

        return new LocalDefinition(ctx.start, ctx.IDENTIFIER().getSymbol(), ctx.type, ctx, init);
    }

    @Override
    public ASTNode visitLet(CoolParser.LetContext ctx) {
        final Expression body = (Expression) visit(ctx.expr());
        final List<Definition> definitions = new ArrayList<>();
        ctx.definitions.forEach(definitionContext -> definitions.add( (Definition) visit(definitionContext)));

        return new CoolLet(ctx.start, definitions, body);
    }

    @Override
    public ASTNode visitCase_branch(CoolParser.Case_branchContext ctx) {
        final Expression body = (Expression) visit(ctx.expr());

        return new CaseBranch(ctx.start, ctx.IDENTIFIER().getSymbol(), ctx.type, body, ctx);
    }

    @Override
    public ASTNode visitCase(CoolParser.CaseContext ctx) {
        final Expression body = (Expression) visit(ctx.expr());
        final List<CaseBranch> caseBranches = new ArrayList<>();

        ctx.case_branch().forEach(caseBranchContext -> caseBranches.add( (CaseBranch) visit(caseBranchContext)));

        return new CoolCase(ctx.start, body, caseBranches);
    }

    @Override
    public ASTNode visitBlock_expr(CoolParser.Block_exprContext ctx) {
        final List<Expression> statements =  new ArrayList<>();
        final Expression finalExpression = (Expression) visit(ctx.final_);

        ctx.statements.forEach(statementContext -> statements.add((Expression) visit(statementContext)));

        return new BlockExpression(ctx.start, statements, finalExpression);
    }
}
