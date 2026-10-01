package org.example.lexer;

public record Token(
        TokenType type,
        String text,
        int position
) {

}
