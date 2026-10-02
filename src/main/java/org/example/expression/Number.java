package org.example.expression;

import org.example.service.EvaluationContext;

/**
 * Узел числовой константы в дереве выражения.
 *
 * <p>Представляет литерал вещественного числа ({@code double}). Является
 * листовым узлом дерева — не содержит дочерних выражений и при вычислении
 * всегда возвращает своё значение независимо от контекста переменных.
 *
 * <p>Примеры чисел в исходной строке и соответствующие узлы:
 * <ul>
 *   <li>{@code 42} → {@code Number(42.0)}</li>
 *   <li>{@code 3.14} → {@code Number(3.14)}</li>
 *   <li>{@code -5} → {@code Unary(NEGATIVE, Number(5.0))} (не {@code Number(-5.0)})</li>
 * </ul>
 * Обратите внимание: отрицательные числа в парсере представляются как
 * унарный минус над положительным числом. Узел {@code Number} с
 * отрицательным значением может появиться только после свёртки констант
 * в {@link org.example.transform.Simplifier}.
 *
 * <p>Узел неизменяем: поле {@code value} является финальным примитивом.
 *
 * @param value числовое значение константы
 * @see Expression
 */
public record Number(double value) implements Expression {

    /**
     * Возвращает значение константы.
     *
     * <p>Контекст игнорируется, поскольку числовая константа не зависит
     * от значений переменных. Метод никогда не выбрасывает исключений.
     *
     * @param context контекст вычисления (игнорируется);
     *                может быть {@code null}, но по контракту
     *                {@link Expression#evaluate(EvaluationContext)}
     *                рекомендуется передавать непустой контекст
     * @return значение {@link #value()}
     */
    @Override
    public double evaluate(EvaluationContext context) {
        return value;
    }
}