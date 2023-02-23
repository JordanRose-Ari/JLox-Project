/* package edu.sou.cs452.jlox;

import java.util.HashMap;
import java.util.Map;

import edu.sou.cs452.jlox.generated.types.*;

class LoxInstance extends LoxClass {
    private final Map<String, LiteralValue> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        super(klass);
    }

    LiteralValue get(Token name) {
        if (fields.containsKey(name.getLexeme())) {
            return fields.get(name.getLexeme());
        }

        LoxFunction method = findMethod(name.getLexeme());
        if (method != null) {
            return method.bind(this);
        }

        throw new RuntimeError(name, "Undefined property '" + name.getLexeme() + "'.");
    }

    void set(Token name, LiteralValue value) {
        fields.put(name.getLexeme(), value);
    }

    @Override
    public String toString() {
        return name + " instance";
    }
} */
