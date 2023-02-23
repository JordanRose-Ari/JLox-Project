package edu.sou.cs452.jlox;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.DgsTypeResolver;
import com.netflix.graphql.dgs.InputArgument;
import java.util.List;

import edu.sou.cs452.jlox.generated.types.*;
import static edu.sou.cs452.jlox.generated.types.AbstractValue.*;

@DgsComponent
public class SignDatafetcher {
    @DgsQuery
    public String sign(@InputArgument String code) {
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);

        List<Stmt> statements = parser.parse();
        AbstractInterpreter interpreter = new AbstractInterpreter();
        return interpreter.interpret(statements);
    }
}