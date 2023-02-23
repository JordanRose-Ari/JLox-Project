package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

import java.util.List;
import java.util.Stack;

import java.util.HashMap;
import java.util.Map;

public class Resolver implements ExprVisitor<Void>, StmtVisitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack();
    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD
    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }

    private ClassType currentClass = ClassType.NONE;

    @Override
    public Void visitBlock(Block stmt) {
        beginScope();
        resolve(stmt.getStatements());
        endScope();
        return null;
    }

    @Override
    public Void visitClassStmt(ClassDecl stmt) {

        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;

        declare(stmt.getName());
        define(stmt.getName());

        if (stmt.getSuperclass() != null
                && stmt.getName().getLexeme().equals(stmt.getSuperclass().getName().getLexeme())) {
            Lox.error(stmt.getSuperclass().getName(), "A class cannot inherit from itself.");
        }

        if (stmt.getSuperclass() != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.getSuperclass());
        }

        if (stmt.getSuperclass() != null) {
            beginScope();
            scopes.peek().put("super", true);
        }

        beginScope();
        scopes.peek().put("this", true);

        for (Function method : stmt.getMethods()) {
            FunctionType type = FunctionType.METHOD;
            if (method.getName().getLexeme().equals("init")) {
                type = FunctionType.INITIALIZER;
            }
            resolveFunction(method, type);
        }

        endScope();

        if (stmt.getSuperclass() != null) {
            endScope();
        }

        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        declare(stmt.getName());

        if (stmt.getInitializer() != null) {
            resolve(stmt.getInitializer());
        }

        define(stmt.getName());
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.getName().getLexeme()) == Boolean.FALSE) {
            Lox.error(expr.getName(), "Can't read local variable in its own initializer");
        }

        resolveLocal(expr, expr.getName());

        return null;
    }

    @Override
    public Void visitAssignment(Assignment expr) {
        resolve(expr.getValue());
        resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitFunction(Function stmt) {
        declare(stmt.getName());
        define(stmt.getName());
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunction(Function fn, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : fn.getParams()) {
            declare(param);
            define(param);
        }
        resolve(fn.getBody());
        endScope();
        currentFunction = enclosingFunction;
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.getLexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    @Override
    public Void visitExpression(Expression stmt) {
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getThen());

        if (stmt.getElse() != null)
            resolve(stmt.getElse());

        return null;
    }

    @Override
    public Void visitPrint(Print stmt) {
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitReturn(Return stmt) {

        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.getKeyword(), "Cannot return from top-level code.");
        }

        if (stmt.getValue() != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Lox.error(stmt.getKeyword(), "Cannot return a value from an initializer.");
            }
            resolve(stmt.getValue());
        }

        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getBody());
        return null;
    }

    @Override
    public Void visitBinary(Binary expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitCallExpr(Call expr) {
        resolve(expr.getCallee());

        for (Expr argument : expr.getArguments()) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitGetExpr(Get expr) {
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitGrouping(Grouping expr) {
        resolve(expr.getExpression());
        return null;
    }

    @Override
    public Void visitLiteral(Literal expr) {
        return null;
    }

    @Override
    public Void visitUnary(Unary expr) {
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitSetExpr(Set expr) {
        resolve(expr.getValue());
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitSuperExpr(Super expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.getKeyword(), "Cannot use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            Lox.error(expr.getKeyword(), "Cannot use 'super' in a class with no superclass.");
        }

        resolveLocal(expr, expr.getKeyword());
        return null;
    }

    @Override
    public Void visitThisExpr(This expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.getKeyword(), "Cannot use 'this' outside of a class");
            return null;
        }

        resolveLocal(expr, expr.getKeyword());
        return null;
    }

    private void declare(Token name) {
        if (scopes.isEmpty())
            return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.getLexeme())) {
            Lox.error(name, "Variable with this name already declared in this scope.");
        }
        scope.put(name.getLexeme(), false);
    }

    private void define(Token name) {
        if (scopes.isEmpty())
            return;
        scopes.peek().put(name.getLexeme(), true);
    }

    void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt s) {
        accept(s);
    }

    private void resolve(Expr e) {
        accept(e);
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }
}