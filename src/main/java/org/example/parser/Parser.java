package org.example.parser;

import org.example.exception.SyntaxException;
import org.example.expression.Binary;
import org.example.expression.FunctionCall;
import org.example.expression.Expression;
import org.example.expression.Number;
import org.example.expression.Unary;
import org.example.expression.Variable;
import org.example.lexer.Lexer;
import org.example.lexer.Token;
import org.example.lexer.TokenType;
import org.example.operation.BinaryOperation;
import org.example.operation.FunctionOperation;
import org.example.operation.UnaryOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parser {
    private final String source;
    private final List<Token> tokens;
    private int current;

    public Parser(String source) {
        this.source = Objects.requireNonNull(source, "Source must not be null");
        this.tokens = new Lexer(source).tokenize();
        this.current = 0;
    }

    public Expression parse() {
        Expression expression = parseExpression();

        expect(TokenType.EOF, "Expect end expression");

        return expression;
    }

    private Expression parseExpression() {
        return parseAdditive();
    }

    private Expression parseAdditive() {
        Expression left = parseMultiplicative();

        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            Token operationToken = advance();

            BinaryOperation operation = toBinaryOperation(operationToken);
            Expression right = parseMultiplicative();

            left = new Binary(left, operation, right);
        }

        return left;
    }

    private Expression parseMultiplicative() {
        Expression left = parseUnary();

        while (check(TokenType.STAR) || check(TokenType.SLASH)) {
            Token operationToken = advance();

            BinaryOperation operation = toBinaryOperation(operationToken);
            Expression right = parseUnary();

            left = new Binary(left, operation, right);
        }

        return left;
    }

    private Expression parseUnary() {
        if (match(TokenType.PLUS)) {
            return new Unary(UnaryOperation.POSITIVE, parseUnary());
        }

        if (match(TokenType.MINUS)) {
            return new Unary(UnaryOperation.NEGATIVE, parseUnary());
        }

        return parsePower();
    }

    private Expression parsePower() {
        Expression base = parsePrimary();

        if (match(TokenType.CARET)) {
            Expression exponent = parseUnary();

            return new Binary(base, BinaryOperation.POWER, exponent);
        }

        return base;
    }

    private Expression parsePrimary() {
        if (check(TokenType.NUMBER)) {
            Token token = advance();

            try {
                double value = Double.parseDouble(token.text());
                return new Number(value);
            } catch (NumberFormatException exception) {
                throw error(token, "Incorrect digit");
            }
        }

        if (check(TokenType.IDENTIFIER)) {
            Token token = advance();

            if (check(TokenType.LEFT_PAREN)) {
                return parseFunctionCall(token);
            }

            return new Variable(token.text());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expression expression = parseExpression();

            expect(TokenType.RIGHT_PAREN, "Expect ')'");

            return expression;
        }

        throw error(
                peek(),
                "Expect digit, variable, function or '('"
        );
    }

    private Expression parseFunctionCall(Token nameToken) {
        String name = nameToken.text();

        FunctionOperation function = FunctionOperation.fromName(name)
                .orElseThrow(() -> error(
                        nameToken,
                        "Unknown function '" + name + "'"
                ));

        expect(TokenType.LEFT_PAREN, "Expect '(' after name function");

        List<Expression> arguments = new ArrayList<>();

        arguments.add(parseExpression());

        while (match(TokenType.COMMA)) {
            arguments.add(parseExpression());
        }

        expect(TokenType.RIGHT_PAREN, "Expect '(' after arguments function");

        if (arguments.size() != function.getArity()) {
            throw error(
                    nameToken,
                    "Function '" + name + "' expect "
                            + function.getArity()
                            + " arguments, but get "
                            + arguments.size()
            );
        }

        return new FunctionCall(function, List.copyOf(arguments));
    }

    private BinaryOperation toBinaryOperation(Token token) {
        return switch (token.type()) {
            case PLUS -> BinaryOperation.PLUS;
            case MINUS -> BinaryOperation.MINUS;
            case STAR -> BinaryOperation.MULTIPLY;
            case SLASH -> BinaryOperation.DIVIDE;
            case CARET -> BinaryOperation.POWER;

            default -> throw new IllegalStateException(
                    "Token " + token.type() + " didn't binary operation"
            );
        };
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean check(TokenType type) {
        return peek().type() == type;
    }

    private Token advance() {
        Token token = peek();

        if (token.type() != TokenType.EOF) {
            current++;
        }

        return token;
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }

        return false;
    }

    private Token expect(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), message);
    }

    private SyntaxException error(Token token, String message) {
        int errorPosition = token.type() == TokenType.EOF
                ? source.length()
                : token.position();

        return new SyntaxException(errorPosition, message, source);
    }
}