package edu.sou.cs452.jlox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sou.cs452.jlox.generated.types.*;

public class LoxClass extends ClassDecl implements LoxCallable {
    final String name;
    private LoxClass superklass;
    private final Map<String, LoxFunction> methods;
    private final Map<String, LiteralValue> map = new HashMap<>();

    LoxClass(LoxClass klass) {
        this.name = klass.name;
        this.superklass = klass.superklass;
        this.methods = klass.methods;
    }

    LoxClass(String name, LoxClass superklass, Map<String, LoxFunction> methods) {
        this.name = name;
        this.methods = methods;
        this.superklass = superklass;
    }

    protected LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superklass != null) {
            return superklass.findMethod(name);
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setSuperklass(LoxClass superklass) {
        this.superklass = superklass;
    }

    void set(Token name, LiteralValue value) {
        map.put(name.getLexeme(), value);
    }

    LiteralValue get(Token name) {
        if (map.containsKey(name.getLexeme())) {
            return map.get(name.getLexeme());
        }

        LoxFunction method = findMethod(name.getLexeme());
        if (method != null) {
            return method.bind(this);
        }

        if (superklass != null && superklass.get(name) != null) {
            return superklass.get(name);
        }

        throw new RuntimeError(name, "Undefined property '" + name.getLexeme() + "'.");
    }

    @Override
    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        LoxClass instance = new LoxClass("", this, new HashMap<>());
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) {
            return 0;
        }
        return initializer.arity();
    }
}
