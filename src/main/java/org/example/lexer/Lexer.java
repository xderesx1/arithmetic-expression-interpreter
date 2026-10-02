package org.example.lexer;

import org.example.exception.SyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Лексический анализатор (лексер) языка арифметических выражений.
 *
 * <p>Преобразует исходную строку в последовательность токенов {@link Token} —
 * атомарных элементов языка: чисел, идентификаторов, символов операций,
 * скобок и запятых. Результат всегда завершается токеном {@link TokenType#EOF},
 * отмечающим конец входной строки.
 *
 * <p>Распознаваемые лексемы:
 * <ul>
 *   <li>числа: {@code 42}, {@code 3.14} (после десятичной точки обязательна
 *       хотя бы одна цифра);</li>
 *   <li>идентификаторы: {@code x}, {@code value_1} (первый символ — буква или
 *       {@code '_'}, далее буквы, цифры и {@code '_'});</li>
 *   <li>операции и разделители: {@code + - * / ^ ( ) ,};</li>
 *   <li>пробельные символы (пробел, таб, перевод строки) пропускаются и
 *       токенов не образуют.</li>
 * </ul>
 *
 * <p>Лексер не интерпретирует смысл выражения: он не знает о приоритетах
 * операций, переменных и функциях. Отличить переменную от вызова функции и
 * построить дерево выражения — задача парсера {@link org.example.parser.Parser}.
 *
 * <p>Экземпляр класса рассчитан на однократный вызов {@link #tokenize()}:
 * текущая позиция сохраняется между вызовами, поэтому повторный вызов
 * вернёт список, состоящий только из токена {@code EOF}.
 *
 * <p>Класс не является потокобезопасным.
 *
 * @see Token
 * @see TokenType
 * @see org.example.parser.Parser
 */
public class Lexer {
    private final String source;
    private int position;

    /**
     * Создаёт лексер для заданной исходной строки.
     *
     * <p>Разбор в конструкторе не выполняется: анализ происходит лениво,
     * при вызове {@link #tokenize()}.
     *
     * @param source исходная строка с выражением; не должна быть {@code null}
     * @throws NullPointerException если {@code source} равна {@code null}
     */
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

    /**
     * Выполняет лексический разбор всей исходной строки.
     *
     * <p>Возвращает неизменяемый список токенов в порядке их следования во
     * входной строке; последний элемент — токен {@link TokenType#EOF}. Список
     * защищён от изменения снаружи: возвращается копия через
     * {@link List#copyOf(java.util.Collection)}.
     *
     * <p>Пример: строка {@code "2 + x"} даёт последовательность
     * {@code [NUMBER("2", 0), PLUS("+", 2), IDENTIFIER("x", 4), EOF("", 5)]}.
     *
     * @return неизменяемый список распознанных токенов, завершающийся {@code EOF}
     * @throws SyntaxException если во входной строке встречен недопустимый
     *         символ (например, {@code '@'}) или некорректное число
     *         (например, {@code "2."} — после десятичной точки нет цифр).
     *         Исключение содержит позицию ошибки (нумерация с нуля) и
     *         исходную строку для формирования диагностики
     */
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
