package edu.sou.cs452.jlox;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import edu.sou.cs452.jlox.generated.types.*;

public class Interpreter implements ExprVisitor<LiteralValue>, StmtVisitor<Void> {

    StringBuilder output = new StringBuilder();
    String input;

    int inputIdx = 0;

    final Environment<LiteralValue> globals = new Environment<>();
    private Environment<LiteralValue> environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    public Interpreter() {
        this.input = "";
        globals.define("clock", new ClockFunction());
        globals.define("getc", new GetcFunction());
        globals.define("putc", new PutcFunction());
        globals.define("input", new InputFunction());
    }

    public Interpreter(String input) {
        this();
        this.input = input;

        globals.define("clock", new ClockFunction());
        globals.define("getc", new GetcFunction());
        globals.define("putc", new PutcFunction());
        globals.define("input", new InputFunction());
    }

    @Override
    public LiteralValue visitBinary(Binary expr) {
        LiteralValue left = accept(expr.getLeft());
        LiteralValue right = accept(expr.getRight());

        switch (expr.getOperator().getType()) {
            case PLUS:
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    Double leftValue = ((LiteralFloat) left).getValue();
                    Double rightValue = ((LiteralFloat) right).getValue();
                    return new LiteralFloat(leftValue + rightValue);
                } else if (left instanceof LiteralString && right instanceof LiteralString) {
                    String leftValue = ((LiteralString) left).getValue();
                    String rightValue = ((LiteralString) right).getValue();
                    return new LiteralString(leftValue + rightValue);
                }
            case MINUS: {
                var operands = checkNumberOperands(expr.getOperator(), left, right);
                Double leftValue = operands.getElement0().getValue();
                Double rightValue = operands.getElement1().getValue();
                return new LiteralFloat(leftValue - rightValue);
            }
            case SLASH: {
                var operands = checkNumberOperands(expr.getOperator(), left, right);
                Double leftValue = operands.getElement0().getValue();
                Double rightValue = operands.getElement1().getValue();
                return new LiteralFloat(leftValue / rightValue);
            }
            case STAR: {
                var operands = checkNumberOperands(expr.getOperator(), left, right);
                Double leftValue = operands.getElement0().getValue();
                Double rightValue = operands.getElement1().getValue();
                return new LiteralFloat(leftValue * rightValue);
            }
            case BANG_EQUAL:
                return new LiteralBoolean(!isEqual(left, right));
            case EQUAL_EQUAL:
                return new LiteralBoolean(isEqual(left, right));
            case GREATER: {
                checkNumberOperands(expr.getOperator(), left, right);
                var operands = checkNumberOperands(expr.getOperator(), left, right);
                Double leftValue = operands.getElement0().getValue();
                Double rightValue = operands.getElement1().getValue();
                return new LiteralBoolean(leftValue > rightValue);
            }
            case GREATER_EQUAL: {
                checkNumberOperands(expr.getOperator(), left, right);
                var operands = checkNumberOperands(expr.getOperator(), left, right);
                Double leftValue = operands.getElement0().getValue();
                Double rightValue = operands.getElement1().getValue();
                return new LiteralBoolean(leftValue >= rightValue);
            }
            case LESS: {
                checkNumberOperands(expr.getOperator(), left, right);
                var operands = checkNumberOperands(expr.getOperator(), left, right);
                Double leftValue = operands.getElement0().getValue();
                Double rightValue = operands.getElement1().getValue();
                return new LiteralBoolean(leftValue < rightValue);
            }
            case LESS_EQUAL: {
                var operands = checkNumberOperands(expr.getOperator(), left, right);
                Double leftValue = operands.getElement0().getValue();
                Double rightValue = operands.getElement1().getValue();
                return new LiteralBoolean(leftValue <= rightValue);
            }
        }

        throw new RuntimeException("Unsupported binary operator: " + expr.getOperator().getLexeme());
    }

    @Override
    public LiteralValue visitGrouping(Grouping expr) {
        return accept(expr.getExpression());
    }

    @Override
    public LiteralValue visitUnary(Unary expr) {
        LiteralValue right = accept(expr.getRight());

        switch (expr.getOperator().getType()) {
            case BANG:
                return new LiteralBoolean(!isTruthy(right));
            case MINUS:
                LiteralFloat num = checkNumberOperand(expr.getOperator(), right);
                return new LiteralFloat(-(num.getValue()));
        }

        // Unreachable.
        return null;
    }

    @Override
    public LiteralValue visitSetExpr(Set expr) {
        LiteralValue object = accept(expr.getObject());

        if (!(object instanceof LoxClass)) {
            throw new RuntimeError(expr.getName(), "Only instances have fields.");
        }

        LiteralValue value = accept(expr.getValue());

        if (expr.getName().getType() == TokenType.PROTO) {
            ((LoxClass) object).setSuperklass((LoxClass) value);
        } else {
            ((LoxClass) object).set(expr.getName(), value);
        }
        return value;
    }

    @Override
    public LiteralValue visitSuperExpr(Super expr) {
        int distance = locals.get(expr);
        LoxClass superclass = (LoxClass) environment.getAt(distance, "super");

        LoxClass object = (LoxClass) environment.getAt(distance - 1, "this");

        LoxFunction method = superclass.findMethod(expr.getMethod().getLexeme());

        if (method == null) {
            throw new RuntimeError(expr.getMethod(), "Undefined property '" + expr.getMethod().getLexeme() + "'.");
        }

        return method.bind(object);
    }

    @Override
    public LiteralValue visitThisExpr(This expr) {
        return lookupVariable(expr.getKeyword(), expr);
    }

    @Override
    public LiteralValue visitLiteral(Literal expr) {
        if (expr.getValue() instanceof LiteralFloat) {
            return ((LiteralFloat) expr.getValue());
        } else if (expr.getValue() instanceof LiteralString) {
            return ((LiteralString) expr.getValue());
        } else if (expr.getValue() instanceof LiteralBoolean) {
            return ((LiteralBoolean) expr.getValue());
        } else if (expr.getValue() instanceof LiteralNull) {
            return ((LiteralNull) expr.getValue());
        } else {
            throw new RuntimeException("Unsupported literal type: " + expr.getValue().getClass().getName());
        }
    }

    @Override
    public Void visitPrint(Print stmt) {
        LiteralValue v = accept(stmt.getExpression());

        if (v instanceof LiteralFloat) {
            output.append(((LiteralFloat) v).getValue());
        } else if (v instanceof LiteralString) {
            output.append(((LiteralString) v).getValue());
        } else if (v instanceof LiteralBoolean) {
            if (((LiteralBoolean) v).getValue()) {
                output.append("true");
            } else {
                output.append("false");
            }
        }
        output.append("\n");
        return null;
    }

    @Override
    public Void visitExpression(Expression stmt) {
        accept(stmt.getExpression());
        return null;
    }

    public String interpret(List<Stmt> statements) {
        output = new StringBuilder();
        try {
            for (Stmt s : statements) {
                accept(s);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }

        return output.toString();
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        LiteralValue value = null;
        if (stmt.getInitializer() != null) {
            value = accept(stmt.getInitializer());
        }

        environment.define(stmt.getName().getLexeme(), value);
        return null;
    }

    @Override
    public LiteralValue visitVariableExpr(Variable expr) {
        return lookupVariable(expr.getName(), expr);
    }

    private LiteralValue lookupVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.getLexeme());
        } else {
            return globals.get(name);
        }
    }

    @Override
    public Void visitBlock(Block stmt) {
        executeBlock(stmt.getStatements(), new Environment<>(environment));

        return null;
    }

    @Override
    public Void visitClassStmt(ClassDecl stmt) {
        LiteralValue superclass = null;
        if (stmt.getSuperclass() != null) {
            superclass = accept(stmt.getSuperclass());
            if (!(superclass instanceof LoxClass)) {
                throw new RuntimeError(stmt.getSuperclass().getName(), "Superclass must be a class.");
            }
        }

        environment.define(stmt.getName().getLexeme(), null);

        if (stmt.getSuperclass() != null) {
            environment = new Environment<>(environment);
            environment.define("super", superclass);
        }

        Map<String, LoxFunction> methods = new HashMap<>();
        for (Function method : stmt.getMethods()) {
            LoxFunction function = new LoxFunction(method, environment, method.getName().getLexeme().equals("init"));
            methods.put(method.getName().getLexeme(), function);
        }
        LoxClass klass = new LoxClass(stmt.getName().getLexeme(), (LoxClass) superclass, methods);

        if (superclass != null) {
            environment = environment.enclosing;
        }

        environment.assign(stmt.getName(), klass);
        return null;
    }

    public void executeBlock(List<Stmt> statements, Environment<LiteralValue> env) {
        Environment previous = this.environment;

        try {
            this.environment = env;

            for (Stmt s : statements) {
                accept(s);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitIfStmt(If stmt) {
        LiteralBoolean c = (LiteralBoolean) accept(stmt.getCondition());

        if (c.getValue()) {
            accept(stmt.getThen());
        } else if (stmt.getElse() != null) {
            accept(stmt.getElse());
        }

        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        while (isTruthy(accept(stmt.getCondition()))) {
            accept(stmt.getBody());
        }
        return null;
    }

    @Override
    public LiteralValue visitAssignment(Assignment expr) {
        LiteralValue value = accept(expr.getValue());

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.getName(), value);
        } else {
            globals.assign(expr.getName(), value);
        }

        environment.assign(expr.getName(), value);
        return value;
    }

    @Override
    public LiteralValue visitLogicalExpr(Logical expr) {
        LiteralValue left = accept(expr.getLeft());

        if (expr.getOperator().getType() == TokenType.OR) {
            if (isTruthy(left))
                return left;
        } else {
            if (!isTruthy(left))
                return left;
        }

        return accept(expr.getRight());
    }

    @Override
    public LiteralValue visitCallExpr(Call expr) {

        LiteralValue callee = accept(expr.getCallee());

        List<LiteralValue> arguments = new ArrayList<>();
        for (Expr argument : expr.getArguments()) {
            arguments.add(accept(argument));
        }

        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeException("Can only call functions and classes.");
        }
        LoxCallable

        function = (LoxCallable) callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeException("Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
        }

        LoxCallable fn = (LoxCallable)

        function;

        return fn.call(this, arguments);

    }

    @Override
    public LiteralValue visitGetExpr(Get expr) {
        LiteralValue object = accept(expr.getObject());

        if (object instanceof LoxClass) {
            LoxClass instance = (LoxClass) object;
            LiteralValue result = instance.get(expr.getName());
            return result;
        }

        throw new RuntimeException("Only instances have properties.");
    }

    @Override
    public Void visitFunction(Function stmt) {
        LoxFunction fn = new LoxFunction(stmt, environment, false);
        environment.define(stmt.getName().getLexeme(), fn);
        return null;
    }

    @Override
    public Void visitReturn(Return stmt) {
        LiteralValue value = null;
        if (stmt.getValue() != null) {
            value = accept(stmt.getValue());
        }

        throw new ReturnException(value);
    }

    private boolean isTruthy(LiteralValue object) {
        if (object == null)
            return false;
        if (object instanceof LiteralNull)
            return false;
        if (object instanceof LiteralBoolean)
            return ((LiteralBoolean) object).getValue();
        if (object instanceof LiteralFloat) {
            LiteralFloat f = (LiteralFloat) object;
            if (f.getValue() == 0.0) {
                return false;
            } else {
                return true;
            }
        }
        if (object instanceof LiteralString) {
            LiteralString s = (LiteralString) object;
            if (s.getValue().length() == 0) {
                return false;
            } else {
                return true;
            }
        }

        throw new RuntimeException("unknown truthiness.");
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private LiteralFloat checkNumberOperand(Token operator, LiteralValue operand) {
        if (operand instanceof LiteralFloat)
            return (LiteralFloat) operand;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private Pair<LiteralFloat, LiteralFloat> checkNumberOperands(Token operator,
            LiteralValue left, LiteralValue right) {
        if (left instanceof LiteralFloat && right instanceof LiteralFloat)
            return Pair.createPair((LiteralFloat) left, (LiteralFloat) right);
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isEqual(LiteralValue a, LiteralValue b) {
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;

        return a.equals(b);
    }
}