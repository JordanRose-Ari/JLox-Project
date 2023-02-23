package edu.sou.cs452.jlox;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

@DgsComponent
public class RunDatafetcher {
    @DgsQuery
    public String run(@InputArgument String code, @InputArgument String input) {
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);

        List<Stmt> statements = parser.parse();
        Interpreter interpreter = new Interpreter(input);

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        String output = interpreter.interpret(statements);
        return output;
    }
}