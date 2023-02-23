package edu.sou.cs452.jlox;

import static edu.sou.cs452.jlox.generated.types.AbstractValue.*;
import edu.sou.cs452.jlox.generated.types.*;

import java.util.HashMap;
import java.util.Map;

public class AbstractEnvironment extends Environment < AbstractValue > {

    @Override
    AbstractValue get(Token name) {
        if (values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme());
        }

        return BOTTOM;
    }

    AbstractEnvironment copy() {
        Map < String, AbstractValue > newValues = new HashMap < > ();
        newValues.putAll(values);

        AbstractEnvironment newEnvironment = new AbstractEnvironment();
        newEnvironment.values = newValues;

        return newEnvironment;
    }

    void join(Environment < AbstractValue > other) {
        for (String key: values.keySet()) {
            values.put(key, AbstractMap.join(values.get(key), other.values.get(key)));
        }
    }
}