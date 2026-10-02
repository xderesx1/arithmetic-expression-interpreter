package org.example.expression;

import org.example.service.EvaluationContext;

/**
 * Закрытая иерархия арифметических выражений.
 *
 * <p>Интерфейс представляет собой корень алгебраического типа данных (ADT),
 * описывающего дерево математического выражения. Иерархия является закрытой
 * ({@code sealed}), что гарантирует исчерпывающий набор узлов и позволяет
 * использовать сопоставление с образцом (pattern matching) в {@code switch}
 * без ветки {@code default}.
 *
 * <p>Разрешённые реализации:
 * <ul>
 *   <li>{@link Number} — числовая константа;</li>
 *   <li>{@link Variable} — именованная переменная;</li>
 *   <li>{@link Unary} — унарная операция;</li>
 *   <li>{@link Binary} — бинарная операция;</li>
 *   <li>{@link FunctionCall} — вызов встроенной функции.</li>
 * </ul>
 *
 * <p>Все реализации являются неизменяемыми записями ({@code record}).
 * Единственная полиморфная операция над деревом — вычисление значения
 * в заданном контексте через метод {@link #evaluate(EvaluationContext)}.
 * Остальные операции над деревом (упрощение, печать, дифференцирование)
 * реализованы как внешние трансформации в пакете
 * {@code org.example.transform} с использованием сопоставления с образцом.
 *
 * @see EvaluationContext
 * @see Binary
 * @see Unary
 * @see FunctionCall
 * @see Number
 * @see Variable
 */
public sealed interface Expression
        permits Binary, FunctionCall, Number, Unary, Variable {

    /**
     * Вычисляет значение выражения в заданном контексте переменных.
     *
     * <p>Вычисление выполняется рекурсивно по структуре дерева:
     * числовые константы возвращают своё значение, переменные разрешаются
     * через контекст, операции применяются к результатам вычисления
     * дочерних узлов, функции применяются к вычисленным аргументам.
     *
     * @param context контекст вычисления, содержащий значения переменных;
     *                не должен быть {@code null}
     * @return числовой результат вычисления выражения
     * @throws org.example.exception.UnknownVariableException
     *         если выражение содержит переменную, отсутствующую в контексте
     * @throws org.example.exception.DivisionByZeroException
     *         если в процессе вычисления происходит деление на ноль
     */
    double evaluate(EvaluationContext context);
}