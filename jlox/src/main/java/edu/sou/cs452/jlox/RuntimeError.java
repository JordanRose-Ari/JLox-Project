package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}