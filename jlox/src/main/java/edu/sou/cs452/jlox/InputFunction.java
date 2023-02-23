package edu.sou.cs452.jlox;

import java.util.List;
import edu.sou.cs452.jlox.generated.types.*;

public class InputFunction extends Function implements LoxCallable {
    public int arity() {
        return 0;
    }

    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        if (interpreter.input.length() > interpreter.inputIdx) {
            String current = interpreter.input.substring(interpreter.inputIdx);
            int newlineIdx = current.indexOf('\n');
            if (newlineIdx == -1) {
                interpreter.inputIdx = interpreter.input.length();
                return new LiteralString(current);
            }
            current = interpreter.input.substring(interpreter.inputIdx, newlineIdx);
            interpreter.inputIdx = newlineIdx + 1;
            return new LiteralString(current);
        } else {
            return new LiteralNull();
        }
    }
}