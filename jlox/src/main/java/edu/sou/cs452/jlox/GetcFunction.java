package edu.sou.cs452.jlox;

import java.util.List;
import edu.sou.cs452.jlox.generated.types.*;

public class GetcFunction extends Function implements LoxCallable {
    public int arity() {
        return 0;
    }

    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        if (interpreter.input.length() > interpreter.inputIdx) {
            return new LiteralString(Character.toString(interpreter.input.charAt(interpreter.inputIdx++)));
        } else {
            return new LiteralNull();
        }
    }
}