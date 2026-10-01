package org.example.exception;

import java.util.Objects;

public class SyntaxException extends RuntimeException {
    private final int position;
    private final String originalMessage;
    private final String sourceText;

    /**
     * Исключение, сигнализирующее о синтаксической ошибке в выражении.
     * Содержит позицию ошибки и исходный текст для формирования
     * понятного диагностического сообщения.
     */
    public SyntaxException(int position, String message, String sourceText) {
        super.getMessage();
        if (position < 0) {
            throw new IllegalArgumentException("Position must be non-negative");
        }
        this.position = position;
        this.originalMessage = Objects.requireNonNull(message, "Message must not be null");
        this.sourceText = sourceText;
    }

    public SyntaxException(int position, String message) {
        this(position, message, null);
    }

    public int getPosition() { return position; }
    public String getOriginalMessage() { return originalMessage; }
    public String getSourceText() { return sourceText; }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();

        if (sourceText != null) {
            sb.append(sourceText).append('\n');
            sb.append(" ".repeat(Math.max(0, position))).append('^').append('\n');
        }

        sb.append(String.format("Syntax error in position %d: %s", position + 1, originalMessage));
        return sb.toString();
    }
}

