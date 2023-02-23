package edu.sou.cs452.jlox;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import java.util.List;

import edu.sou.cs452.jlox.generated.types.Token;

@DgsComponent
public class TokensDatafetcher {
    @DgsQuery
    public List < Token > tokens(@InputArgument String code) {
        Scanner scanner = new Scanner(code);
        List < Token > tokens = scanner.scanTokens();
        return tokens;
    }
}