package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

public interface StmtVisitor<T> {

    T visitPrint(Print stmt);

    T visitExpression(Expression stmt);

    T visitVarStmt(Var stmt);

    T visitIfStmt(If stmt);

    T visitWhileStmt(While stmt);

    T visitBlock(Block stmt);

    T visitFunction(Function stmt);

    T visitReturn(Return stmt);

    T visitClassStmt(ClassDecl stmt);

    default T accept(Stmt o) {
        if (o instanceof Expression) {
            return visitExpression((Expression) o);
        } else if (o instanceof Print) {
            return visitPrint((Print) o);
        } else if (o instanceof Var) {
            return visitVarStmt((Var) o);
        } else if (o instanceof If) {
            return visitIfStmt((If) o);
        } else if (o instanceof Block) {
            return visitBlock((Block) o);
        } else if (o instanceof Function) {
            return visitFunction((Function) o);
        } else if (o instanceof Return) {
            return visitReturn((Return) o);
        } else if (o instanceof While) {
            return visitWhileStmt((While) o);
        } else if (o instanceof ClassDecl) {
            return visitClassStmt((ClassDecl) o);
        } else {
            throw new RuntimeException("Unhandled statement type: " + o.getClass().getName());
        }
    }
}