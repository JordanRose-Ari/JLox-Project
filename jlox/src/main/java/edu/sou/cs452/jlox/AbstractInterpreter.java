package edu.sou.cs452.jlox;

import java.util.List;
import edu.sou.cs452.jlox.generated.types.*;

import static edu.sou.cs452.jlox.generated.types.AbstractValue.*;

public class AbstractInterpreter implements ExprVisitor<AbstractValue>, StmtVisitor<Void> {

    String output;
    private AbstractEnvironment environment = new AbstractEnvironment();

    public AbstractInterpreter() {
        output = "";
    }

    @Override
    public AbstractValue visitBinary(Binary expr) {
        AbstractValue left = accept(expr.getLeft());
        AbstractValue right = accept(expr.getRight());

        switch (expr.getOperator().getType()) {
            case PLUS:
                return AbstractMap.plus(left, right);
            case MINUS:
                return AbstractMap.minus(left, right);
            case STAR:
                return AbstractMap.multiply(left, right);
            case SLASH:
                return AbstractMap.divide(left, right);
        }

        throw new RuntimeException("Unsupported binary operator: " + expr.getOperator().getLexeme());
    }

    @Override
    public AbstractValue visitGrouping(Grouping expr) {
        return accept(expr.getExpression());
    }

    @Override
    public AbstractValue visitUnary(Unary expr) {
        AbstractValue right = accept(expr.getRight());

        switch (expr.getOperator().getType()) {
            case MINUS:
                return AbstractMap.invert(right);
        }

        throw new RuntimeException("Unsupported unary operator.");
    }

    @Override
    public AbstractValue visitLiteral(Literal expr) {
        if (expr.getValue() instanceof LiteralFloat) {
            Double v = ((LiteralFloat) expr.getValue()).getValue();
            if (v > 0) {
                return POSITIVE;
            } else if (v < 0) {
                return NEGATIVE;
            } else {
                return ZERO;
            }
        } else if (expr.getValue() instanceof LiteralString) {
            return TOP;
        } else {
            throw new RuntimeException("Unsupported literal type: " + expr.getValue().getClass().getName());
        }
    }

    @Override
    public Void visitPrint(Print stmt) {
        System.out.println(output);
        AbstractValue v = accept(stmt.getExpression());

        output += v;
        output += "\n";

        return null;
    }

    @Override
    public Void visitExpression(Expression stmt) {
        accept(stmt.getExpression());
        return null;
    }

    public String interpret(List<Stmt> statements) {
        for (Stmt s : statements) {
            accept(s);
        }

        String result = output;
        output = "";

        return result;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        AbstractValue value = null;
        if (stmt.getInitializer() != null) {
            value = accept(stmt.getInitializer());
        }

        environment.define(stmt.getName().getLexeme(), value);
        return null;
    }

    @Override
    public AbstractValue visitVariableExpr(Variable expr) {
        return environment.get(expr.getName());
    }

    @Override
    public Void visitBlock(Block stmt) {
        for (Stmt s : stmt.getStatements()) {
            accept(s);
        }

        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        AbstractEnvironment startEnvironment = environment.copy();

        accept(stmt.getThen());

        AbstractEnvironment thenEnvironment = environment.copy();
        environment = startEnvironment.copy();

        if (stmt.getElse() != null)
            accept(stmt.getElse());
        AbstractEnvironment elseEnvironment = environment.copy();

        thenEnvironment.join(elseEnvironment);

        environment = thenEnvironment;

        return null;
    }

    @Override
    public AbstractValue visitAssignment(Assignment expr) {
        AbstractValue value = accept(expr.getValue());
        environment.assign(expr.getName(), value);
        return value;
    }

    @Override
    public AbstractValue visitLogicalExpr(Logical expr) {
        accept(expr.getLeft());
        accept(expr.getRight());

        return TOP;
    }

    @Override
    public AbstractValue visitCallExpr(Call expr) {
        // TODO: Turn everything to top for function calls.
        throw new RuntimeException("Abstract interpreter can't handle function calls.");
    }

    @Override
    public Void visitFunction(Function stmt) {
        throw new RuntimeException("Abstract interpreter can't handle function definitions.");
    }

    @Override
    public Void visitReturn(Return stmt) {
        throw new RuntimeException("Abstract interpreter can't handle return statements.");
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        throw new RuntimeException("Abstract interpreter can't handle while statements.");
    }

    @Override
    public AbstractValue visitGetExpr(Get expr) {
        throw new RuntimeException("Abstract interpreter can't handle get statements.");
    }

    @Override
    public AbstractValue visitSetExpr(Set expr) {
        throw new RuntimeException("Abstract interpreter can't handle break statements.");
    }

    @Override
    public AbstractValue visitThisExpr(This expr) {
        throw new RuntimeException("Abstract interpreter can't handle this statements.");
    }

    @Override
    public AbstractValue visitSuperExpr(Super expr) {
        throw new RuntimeException("Abstract interpreter can't handle super statements.");
    }

    @Override
    public Void visitClassStmt(ClassDecl stmt) {
        throw new RuntimeException("Abstract interpreter can't handle class statements.");
    }

}