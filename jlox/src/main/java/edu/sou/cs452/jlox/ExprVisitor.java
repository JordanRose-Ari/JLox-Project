package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

public interface ExprVisitor<T> {

    T visitBinary(Binary expr);

    T visitGrouping(Grouping expr);

    T visitUnary(Unary expr);

    T visitLiteral(Literal expr);

    T visitVariableExpr(Variable expr);

    T visitAssignment(Assignment expr);

    T visitLogicalExpr(Logical expr);

    T visitCallExpr(Call expr);

    T visitGetExpr(Get expr);

    T visitSetExpr(Set expr);

    T visitThisExpr(This expr);

    T visitSuperExpr(Super expr);

    default T accept(Expr o) {
        if (o instanceof Binary) {
            return visitBinary((Binary) o);
        } else if (o instanceof Grouping) {
            return visitGrouping((Grouping) o);
        } else if (o instanceof Unary) {
            return visitUnary((Unary) o);
        } else if (o instanceof Literal) {
            return visitLiteral((Literal) o);
        } else if (o instanceof Variable) {
            return visitVariableExpr((Variable) o);
        } else if (o instanceof Assignment) {
            return visitAssignment((Assignment) o);
        } else if (o instanceof Call) {
            return visitCallExpr((Call) o);
        } else if (o instanceof Logical) {
            return visitLogicalExpr((Logical) o);
        } else if (o instanceof Get) {
            return visitGetExpr((Get) o);
        } else if (o instanceof Set) {
            return visitSetExpr((Set) o);
        } else if (o instanceof This) {
            return visitThisExpr((This) o);
        } else if (o instanceof Super) {
            return visitSuperExpr((Super) o);
        } else {
            throw new RuntimeException("Unhandled expression type: " + o.getClass().getSimpleName());
        }
    }
}