package org.example.lexer;

import org.example.exception.SyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lexer {
    private final String source;
    private int position;
    public Lexer(String source) {
        this.source = Objects.requireNonNull(source, "Source must not be null");
        this.position = 0;
    }

    private void skipWhiteSpace() {
        while (position < source.length() && Character.isWhitespace(source.charAt(position))) {
            position++;
        }
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private Token readNumber(int start) {
        while (position < source.length() && isDigit(source.charAt(position))) {
            position++;
        }

        if (position < source.length() && source.charAt(position) == '.') {
            position++;

            if (position >= source.length() || !isDigit(source.charAt(position))) {
                throw new SyntaxException(
                        position ,
                        "Digits are expected after the decimal point"
                );
            }

            while (position < source.length() && isDigit(source.charAt(position))) {
                position++;
            }
        }

        String text = source.substring(start, position);
        return new Token(TokenType.NUMBER, text, start);
    }

    private Token readIdentifier(int start) {
        while (position < source.length() && isIdentifierPart(source.charAt(position))) {
            position++;
        }

        String text = source.substring(start, position);
        return new Token(TokenType.IDENTIFIER, text, start);
    }

    private boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isIdentifierPart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9') || c == '_';
    }

    private Token singleCharToken(TokenType type, String text) {
        int start = position;
        position++;
        return new Token(type, text, start);
    }

    private Token nextToken(){
        skipWhiteSpace();

        if (position >= source.length()) {
            return new Token(TokenType.EOF, "", source.length());
        }

        char c = source.charAt(position);

        if(isDigit(c)){
            return readNumber(position);
        }

        if(isIdentifierStart(c)){
            return readIdentifier(position);
        }

        return switch (c) {
            case '+' -> singleCharToken(TokenType.PLUS, "+");
            case '-' -> singleCharToken(TokenType.MINUS, "-");
            case '*' -> singleCharToken(TokenType.STAR, "*");
            case '/' -> singleCharToken(TokenType.SLASH, "/");
            case '^' -> singleCharToken(TokenType.CARET, "^");
            case ',' -> singleCharToken(TokenType.COMMA, ",");
            case '(' -> singleCharToken(TokenType.LEFT_PAREN, "(");
            case ')' -> singleCharToken(TokenType.RIGHT_PAREN, ")");

            default -> throw new SyntaxException(position, "Unexpected character");
        };
    }

    public List<Token> tokenize(){
        var tokens = new ArrayList<Token>();

        while (true){
            Token token = nextToken();
            tokens.add(token);

            if (token.type() == TokenType.EOF) {
                break;
            }
        }

        return List.copyOf(tokens);
    }
}
