package org.example.expression;

import org.example.operation.UnaryOperation;
import org.example.service.EvaluationContext;

import java.util.Objects;

/**
 * Узел унарной операции в дереве выражения.
 *
 * <p>Представляет применение унарной операции (унарный минус или унарный
 * плюс) к одному дочернему выражению — операнду. Например, выражение
 * {@code -x} представляется как:
 * <pre>
 * Unary(operation = NEGATIVE, operand = Variable("x"))
 * </pre>
 * а выражение {@code -(2 + 3)} — как:
 * <pre>
 * Unary(operation = NEGATIVE, operand = Binary(Number(2), PLUS, Number(3)))
 * </pre>
 *
 * <p>Унарные операции имеют приоритет выше умножения и деления, но ниже
 * возведения в степень. Это означает, что {@code -2 ^ 2} интерпретируется
 * как {@code -(2 ^ 2) = -4}, а не как {@code (-2) ^ 2 = 4}. Данное
 * соглашение зафиксировано в грамматике парсера и учитывается при печати
 * дерева обратно в строку.
 *
 * <p>Узел неизменяем: оба компонента финальны, а операнд является
 * неизменяемой записью, поэтому дополнительного защитного копирования
 * не требуется.
 *
 * @param operation унарная операция, не {@code null}
 * @param operand   выражение-операнд, к которому применяется операция;
 *                  не {@code null}
 * @see UnaryOperation
 * @see Expression
 */
public record Unary(
        UnaryOperation operation,
        Expression operand
) implements Expression {

    /**
     * Компактный конструктор, проверяющий корректность компонентов.
     *
     * <p>Гарантирует, что ни операция, ни операнд не равны {@code null}.
     * Поскольку операнд является неизменяемой записью, дополнительного
     * защитного копирования не требуется.
     *
     * @throws NullPointerException если {@code operation} или
     *                              {@code operand} равны {@code null}
     */
    public Unary {
        Objects.requireNonNull(operation, "Operation must not be null");
        Objects.requireNonNull(operand, "Operand must not be null");
    }

    /**
     * Вычисляет значение унарной операции в заданном контексте.
     *
     * <p>Сначала рекурсивно вычисляется значение операнда в переданном
     * контексте, затем к нему применяется унарная операция через
     * {@link UnaryOperation#calculate(double)}.
     *
     * @param context контекст вычисления со значениями переменных;
     *                не должен быть {@code null}
     * @return числовой результат применения операции к операнду
     * @throws NullPointerException
     *         если {@code context} равен {@code null}
     * @throws org.example.exception.UnknownVariableException
     *         если при вычислении операнда встречается переменная,
     *         отсутствующая в контексте
     * @throws org.example.exception.DivisionByZeroException
     *         если при вычислении операнда происходит деление на ноль
     */
    @Override
    public double evaluate(EvaluationContext context) {
        Objects.requireNonNull(context, "EvaluationContext must not be null");
        double value = operand.evaluate(context);
        return operation.calculate(value);
    }
}