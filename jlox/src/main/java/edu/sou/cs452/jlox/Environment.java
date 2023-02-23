package edu.sou.cs452.jlox;

import java.util.HashMap;
import java.util.Map;

import edu.sou.cs452.jlox.generated.types.*;

class Environment<T> {
    protected Map<String, T> values;
    final Environment<T> enclosing;

    public Environment() {
        this.enclosing = null;
        this.values = new HashMap<>();
    }

    // Environment is new (inner scope).
    // No values here. Lookup values in enclosing scope if not found here.
    public Environment(Environment<T> enclosing) {
        this.enclosing = enclosing;
        this.values = new HashMap<>();
    }

    void define(String name, T value) {
        values.put(name, value);
    }

    void assign(Token name, T value) {

        if (values.containsKey(name.getLexeme())) {
            values.put(name.getLexeme(), value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeException("Undefined variable '" + name.getLexeme() + "'.");
    }

    T get(Token name) {

        if (values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme());
        }

        if (enclosing != null)
            return enclosing.get(name);

        System.out.println("Get problem with: " + name.getLexeme() + " Get problem with: " + name.getType());
        throw new RuntimeException("Undefined variable '" + name.getLexeme() + "'.");
    }

    T getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, T value) {
        ancestor(distance).values.put(name.getLexeme(), value);
    }

    Environment<T> ancestor(int distance) {
        Environment<T> e = this;
        for (int i = 0; i < distance; i++) {
            e = e.enclosing;
        }

        return e;
    }

}