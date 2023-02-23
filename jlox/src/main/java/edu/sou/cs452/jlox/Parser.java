package edu.sou.cs452.jlox;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import static edu.sou.cs452.jlox.generated.types.TokenType.*;
import edu.sou.cs452.jlox.generated.types.*;

class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private int nextId = 1;

    private static class ParseError extends RuntimeException {
        public ParseError(String message) {
            super(message);
        }
    }

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Variable) {
                Token name = ((Variable) expr).getName();
                return new Assignment(nextId++, name, value);
            } else if (expr instanceof Get) {
                Get get = (Get) expr;
                return new Set(nextId++, get.getObject(), get.getName(), value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Logical(nextId++, expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Logical(nextId++, expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Binary(nextId++, expr, operator, right);
        }

        return expr;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Binary(nextId++, expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Binary(nextId++, expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Binary(nextId++, expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Unary(nextId++, operator, right);
        }

        return call();
    }

    private Expr call() {
        Expr expr = primary();

        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(DOT)) {
                Token name;
                if (check(IDENTIFIER)) {
                    name = consume(IDENTIFIER, "Expect property name after '.'.");
                    System.out.println("The Lexeme is: " + name.getLexeme() + " AND the type is: " + name.getType());
                } else if (check(PROTO)) {
                    name = consume(PROTO, "Expect proto after '.'.");
                    System.out.println("The Lexeme is: " + name.getLexeme() + " AND the type is: " + name.getType());
                } else {
                    throw error(peek(), "Expect property or proto after dot.");
                }
                expr = new Get(nextId++, expr, name);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();

        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "Can't have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }

        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

        return new Call(nextId++, callee, paren, arguments);
    }

    private Expr primary() {

        if (match(FALSE))
            return new Literal(nextId++, new LiteralBoolean(false));
        if (match(TRUE))
            return new Literal(nextId++, new LiteralBoolean(true));
        if (match(NIL))
            return new Literal(nextId++, new LiteralNull());

        if (match(NUMBER, STRING)) {
            return new Literal(nextId++, previous().getLiteral());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            if (match(RIGHT_PAREN))
                return new Grouping(nextId++, expr);
            error(peek(), "Expected RIGHT_PAREN");
        }

        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token method = consume(IDENTIFIER, "Expect superclass method name.");
            return new Super(nextId++, keyword, method);
        }

        if (match(THIS)) {
            return new This(nextId++, previous());
        }

        if (match(IDENTIFIER)) {
            return new Variable(nextId++, previous());
        }

        throw error(peek(), "Expect expression.");
    }

    private Stmt declaration() {
        try {
            if (match(CLASS))
                return classDeclaration();
            if (match(FUN))
                return function("function");
            if (match(VAR))
                return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt classDeclaration() {
        Token name = consume(IDENTIFIER, "Expect class name.");

        Variable superclass = null;
        if (match(LESS)) {
            consume(IDENTIFIER, "Expect superclass name.");
            superclass = new Variable(nextId++, previous());
        }

        consume(LEFT_BRACE, "Expect '{' before class body.");
        List<Function> methods = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add((Function) function("method"));
        }

        consume(RIGHT_BRACE, "Expect '}' after class body.");

        return new ClassDecl(nextId++, name, superclass, methods);
    }

    private Stmt function() {
        Token name = consume(IDENTIFIER, "Expect function name name.");
        consume(LEFT_PAREN, "Expect '(' after function name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(
                        consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(LEFT_BRACE, "Expect '{' before function body.");
        List<Stmt> body = block();
        return new Function(nextId++, name, parameters, body);
    }

    private Stmt function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(
                        consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Function(nextId++, name, parameters, body);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Var(nextId++, name, initializer);
    }

    private Stmt statement() {
        if (match(PRINT))
            return printStatement();
        if (match(WHILE))
            return whileStatement();
        if (match(RETURN))
            return returnStatement();
        if (match(LEFT_BRACE))
            return new Block(nextId++, block());
        if (match(FOR))
            return forStatement();
        if (match(IF))
            return ifStatement();

        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Print(nextId++, value);
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();

        return new While(nextId++, condition, body);
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        Stmt body = statement();

        if (increment != null) {
            body = new Block(nextId++,
                    Arrays.asList(
                            body,
                            new Expression(nextId++, increment)));
        }

        if (condition == null)
            condition = new Literal(nextId++, new LiteralBoolean(true));
        body = new While(nextId++, condition, body);

        if (initializer != null) {
            body = new Block(nextId++, Arrays.asList(initializer, body));
        }

        return body;
    }

    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value;
        if (!check(SEMICOLON)) {
            value = expression();
        } else {
            value = new Literal(nextId++, new LiteralNull());
        }

        consume(SEMICOLON, "Expect ';' after return value.");
        return new Return(nextId++, keyword, value);
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new If(nextId++, condition, thenBranch, elseBranch);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Expression(nextId++, expr);
    }

    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        throw new ParseError(message);
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().getType() == SEMICOLON)
                return;

            switch (peek().getType()) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }
}