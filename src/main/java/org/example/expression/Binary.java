package org.example.expression;

import org.example.operation.BinaryOperation;
import org.example.service.EvaluationContext;

import java.util.Objects;

/**
 * Узел бинарной операции в дереве выражения.
 *
 * <p>Представляет применение бинарной операции (сложение, вычитание,
 * умножение, деление, возведение в степень) к двум дочерним выражениям —
 * левому и правому операндам. Например, выражение {@code 2 + 3 * x}
 * представляется как дерево:
 * <pre>
 * Binary(
 *     left  = Number(2),
 *     operation = PLUS,
 *     right = Binary(Number(3), MULTIPLY, Variable("x"))
 * )
 * </pre>
 *
 * <p>Приоритет и ассоциативность операции хранятся в самой операции
 * ({@link BinaryOperation}) и используются при печати дерева обратно в
 * строку с минимальным числом скобок.
 *
 * <p>Узел неизменяем: все компоненты финальны, а переданные операнды
 * не копируются, поскольку сами являются неизменяемыми записями.
 *
 * @param left      левый операнд, не {@code null}
 * @param operation бинарная операция, не {@code null}
 * @param right     правый операнд, не {@code null}
 * @see BinaryOperation
 * @see Expression
 */
public record Binary(
        Expression left,
        BinaryOperation operation,
        Expression right
) implements Expression {

    /**
     * Компактный конструктор, проверяющий корректность компонентов.
     *
     * <p>Гарантирует, что ни один из компонентов узла не является
     * {@code null}. Поскольку дочерние выражения и операция неизменяемы,
     * дополнительного защитного копирования не требуется.
     *
     * @throws NullPointerException если {@code left}, {@code operation}
     *                              или {@code right} равны {@code null}
     */
    public Binary {
        Objects.requireNonNull(left, "Left expression must not be null");
        Objects.requireNonNull(right, "Right expression must not be null");
        Objects.requireNonNull(operation, "Operation must not be null");
    }

    /**
     * Вычисляет значение бинарной операции в заданном контексте.
     *
     * <p>Сначала рекурсивно вычисляются значения левого и правого
     * операндов, затем к ним применяется операция через
     * {@link BinaryOperation#calculate(double, double)}.
     *
     * @param context контекст вычисления со значениями переменных;
     *                не должен быть {@code null}
     * @return числовой результат применения операции к операндам
     * @throws NullPointerException
     *         если {@code context} равен {@code null}
     * @throws org.example.exception.UnknownVariableException
     *         если при вычислении операндов встречается переменная,
     *         отсутствующая в контексте
     * @throws org.example.exception.DivisionByZeroException
     *         если операция — деление, а правый операнд вычисляется в ноль
     */
    @Override
    public double evaluate(EvaluationContext context) {
        Objects.requireNonNull(context, "EvaluationContext must not be null");
        double leftValue = left.evaluate(context);
        double rightValue = right.evaluate(context);

        return operation.calculate(leftValue, rightValue);
    }
}