package edu.sou.cs452.jlox;

import java.util.List;
import edu.sou.cs452.jlox.generated.types.*;

public class LoxFunction extends Function implements LoxCallable {
    public final Environment<LiteralValue> closure;
    private final boolean isInitializer;

    public LoxFunction(Function declaration, Environment<LiteralValue> closure, boolean isInitializer) {
        super(declaration.getId(), declaration.getName(), declaration.getParams(), declaration.getBody());
        this.isInitializer = isInitializer;
        this.closure = closure;
    }

    LoxFunction bind(LoxClass instance) {
        Environment<LiteralValue> environment = new Environment<LiteralValue>(closure);

        environment.define("this", instance);

        return new LoxFunction(this, environment, isInitializer);
    }

    public int arity() {
        return getParams().size();
    }

    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        Environment<LiteralValue> callEnvironment = new Environment<LiteralValue>(closure);

        for (int i = 0; i < getParams().size(); i++) {
            callEnvironment.define(getParams().get(i).getLexeme(), arguments.get(i));
        }

        try {
            interpreter.executeBlock(getBody(), callEnvironment);
        } catch (ReturnException returnValue) {
            return returnValue.value;
        }

        if (isInitializer) {
            return closure.getAt(0, "this");
        }

        return new LiteralNull();
    }
}