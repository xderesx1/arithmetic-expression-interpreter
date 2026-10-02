package org.example.expression;

import org.example.operation.FunctionOperation;
import org.example.service.EvaluationContext;

import java.util.List;
import java.util.Objects;

/**
 * Узел вызова встроенной функции в дереве выражения.
 *
 * <p>Представляет применение одной из встроенных функций ({@code min},
 * {@code max}, {@code abs}) к списку аргументов-выражений. Например,
 * выражение {@code min(x + 1, 2)} представляется как:
 * <pre>
 * FunctionCall(
 *     operation = MIN,
 *     arguments = [Binary(Variable("x"), PLUS, Number(1)), Number(2)]
 * )
 * </pre>
 *
 * <p>Количество аргументов должно в точности совпадать с арностью функции
 * ({@link FunctionOperation#getArity()}); несоответствие проверяется в
 * конструкторе и приводит к ошибке.
 *
 * <p>Узел неизменяем: переданный список аргументов защищённо копируется
 * в неизменяемый список через {@link List#copyOf(java.util.Collection)},
 * поэтому внешние изменения исходной коллекции не влияют на узел, а сам
 * узел не позволяет изменить свой список аргументов.
 *
 * @param operation вызываемая функция, не {@code null}
 * @param arguments список аргументов функции, не {@code null}; размер
 *                  должен совпадать с арностью операции
 * @see FunctionOperation
 * @see Expression
 */
public record FunctionCall(
        FunctionOperation operation,
        List<Expression> arguments
) implements Expression {

    /**
     * Компактный конструктор, проверяющий корректность компонентов.
     *
     * <p>Выполняет три проверки:
     * <ol>
     *   <li>операция и список аргументов не равны {@code null};</li>
     *   <li>список аргументов защищённо копируется в неизменяемый список,
     *       чтобы исключить утечку изменяемого состояния наружу;</li>
     *   <li>размер списка аргументов совпадает с арностью функции.</li>
     * </ol>
     *
     * @throws NullPointerException
     *         если {@code operation} или {@code arguments} равны {@code null}
     * @throws IllegalArgumentException
     *         если количество аргументов не соответствует арности функции
     */
    public FunctionCall {
        Objects.requireNonNull(operation, "Function operation must not be null");
        Objects.requireNonNull(arguments, "Function argument must not be null");

        arguments = List.copyOf(arguments);

        if (arguments.size() != operation.getArity()) {
            throw new IllegalArgumentException(
                    "Function " + operation.getName() +
                            " expects " + operation.getArity() +
                            " arguments, but got " + arguments.size()
            );
        }
    }

    /**
     * Вычисляет значение вызова функции в заданном контексте.
     *
     * <p>Сначала рекурсивно вычисляются значения всех аргументов в
     * переданном контексте, затем к полученным числовым значениям
     * применяется функция через
     * {@link FunctionOperation#calculate(double...)}.
     *
     * @param context контекст вычисления со значениями переменных;
     *                не должен быть {@code null}
     * @return числовой результат применения функции к аргументам
     * @throws NullPointerException
     *         если {@code context} равен {@code null}
     * @throws org.example.exception.UnknownVariableException
     *         если при вычислении аргументов встречается переменная,
     *         отсутствующая в контексте
     * @throws org.example.exception.DivisionByZeroException
     *         если при вычислении аргументов происходит деление на ноль
     */
    @Override
    public double evaluate(EvaluationContext context) {
        Objects.requireNonNull(context, "EvaluationContext must not be null");
        double[] values = new double[arguments.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = arguments.get(i).evaluate(context);
        }

        return operation.calculate(values);
    }
}