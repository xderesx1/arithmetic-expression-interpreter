package org.example.exception;

/**
 * Исключение, сигнализирующее о попытке деления на ноль при вычислении
 * выражения.
 *
 * <p>Выбрасывается на этапе <b>вычисления</b>, а не разбора: операция
 * деления применяется к уже вычисленным операндам через
 * {@link org.example.operation.BinaryOperation#DIVIDE}, и если правый
 * операнд равен нулю, вычисление прерывается этим исключением.
 *
 * <p>Примеры выражений, приводящих к исключению:
 * <ul>
 *   <li>{@code 1 / 0} — деление на нулевой литерал;</li>
 *   <li>{@code x / (y - y)} — делитель становится нулём только после
 *       вычисления подвыражения;</li>
 *   <li>{@code 5 / (2 - 2)} — аналогично.</li>
 * </ul>
 *
 * <p>Исключение является unchecked (наследуется от {@link RuntimeException}),
 * поэтому вызывающий код не обязан его обрабатывать, но демонстрационное
 * приложение и тесты перехватывают его для формирования читаемой диагностики.
 *
 * <p>В отличие от {@link SyntaxException}, это исключение не содержит
 * позиции в исходной строке: ошибка обнаруживается не в тексте выражения,
 * а в процессе вычисления дерева, где позиция уже не имеет смысла.
 *
 * @see org.example.operation.BinaryOperation#DIVIDE
 * @see org.example.expression.Binary
 */
public class DivisionByZeroException extends RuntimeException {

    /**
     * Создаёт исключение с заданным сообщением.
     *
     * @param message человекочитаемое описание ошибки; передаётся в
     *                {@link RuntimeException#RuntimeException(String)}
     */
    public DivisionByZeroException(String message) {
        super(message);
    }
}