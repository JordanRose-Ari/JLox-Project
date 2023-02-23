package edu.sou.cs452.jlox;

import java.util.List;
import edu.sou.cs452.jlox.generated.types.*;

public class PutcFunction extends Function implements LoxCallable {
    public int arity() {
        return 1;
    }

    public LiteralValue call(Interpreter interpreter, List < LiteralValue > arguments) {
        LiteralFloat arg = (LiteralFloat) arguments.get(0);

        String s = Character.toString((int) arg.getValue());
        interpreter.output.append(s);

        return new LiteralString(s);
    }
}