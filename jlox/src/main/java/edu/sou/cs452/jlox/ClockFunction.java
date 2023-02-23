
package edu.sou.cs452.jlox;

import java.util.List;
import edu.sou.cs452.jlox.generated.types.*;

public class ClockFunction extends Function implements LoxCallable {
    public int arity() {
        return 0;
    }

    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        return new LiteralFloat(System.currentTimeMillis() / 1000.0);
    }
}