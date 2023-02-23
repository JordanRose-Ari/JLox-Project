package edu.sou.cs452.jlox;

import java.util.List;
import edu.sou.cs452.jlox.generated.types.*;

interface LoxCallable {
    int arity();

    LiteralValue call(Interpreter interpreter, List < LiteralValue > objects);
}