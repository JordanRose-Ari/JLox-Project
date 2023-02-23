package edu.sou.cs452.jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.sou.cs452.jlox.generated.types.*;

@SpringBootApplication
public class Lox {

    static boolean hadError = false;
    static boolean exceptionOnError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            if (args[0].equals("--service")) {
                exceptionOnError = true;
                runService();
            } else {
                runFile(args[0]);
            }
        } else {
            runPrompt();
        }
    }

    private static void runService() {
        SpringApplication.run(Lox.class);
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));

        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

        String line;
        String input = "";
        while ((line = systemIn.readLine()) != null) {
            input = input + line;
        }

        run(new String(bytes, Charset.defaultCharset()), input);

        if (hadError)
            System.exit(65);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null)
                break;
            run(line, "");
            hadError = false;
        }
    }

    public static void run(String source, String input) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);

        List<Stmt> statements = parser.parse();
        Interpreter interpreter = new Interpreter(input);

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // Stop if there was a resolution error.
        if (hadError)
            return;

        String result = interpreter.interpret(statements);

        System.out.println(result);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        String errMessage = "[line " + line + "] Error" + where + ": " + message;
        if (exceptionOnError) {
            throw new RuntimeException(errMessage);
        } else {
            System.err.println(errMessage);
        }
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.getType() == TokenType.EOF) {
            report(token.getLine(), " at end", message);
        } else {
            report(token.getLine(), " at '" + token.getLexeme() + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        String errMessage = error.getMessage() + " [line " + error.token.getLine() + "]";
        if (exceptionOnError) {
            throw new RuntimeException(errMessage);
        } else {
            System.err.println(errMessage);
        }
        hadError = true;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

}