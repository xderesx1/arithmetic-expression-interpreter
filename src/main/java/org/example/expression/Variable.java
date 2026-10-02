package org.example.expression;

import org.example.service.EvaluationContext;

import java.util.Objects;

/**
 * Узел именованной переменной в дереве выражения.
 *
 * <p>Представляет ссылку на переменную, значение которой берётся из
 * контекста вычисления при вызове {@link #evaluate(EvaluationContext)}.
 * Является листовым узлом дерева — не содержит дочерних выражений.
 *
 * <p>Имя переменной чувствительно к регистру: {@code x} и {@code X} — это
 * разные переменные. Допустимые имена определяются лексером (обычно
 * буква или подчёркивание, далее буквы, цифры и подчёркивания).
 *
 * <p>Примеры:
 * <ul>
 *   <li>{@code x} → {@code Variable("x")}</li>
 *   <li>{@code value_1} → {@code Variable("value_1")}</li>
 * </ul>
 *
 * <p>Узел неизменяем: поле {@code name} является финальной ссылкой на
 * неизменяемую строку.
 *
 * @param name имя переменной, не {@code null}
 * @see Expression
 * @see EvaluationContext
 */
public record Variable(String name) implements Expression {

    /**
     * Компактный конструктор, проверяющий корректность имени.
     *
     * <p>Гарантирует, что имя переменной не равно {@code null}.
     * Дополнительная проверка на пустоту или допустимые символы
     * выполняется на этапе лексического анализа, поэтому здесь
     * достаточно проверки на {@code null}.
     *
     * @throws NullPointerException если {@code name} равно {@code null}
     */
    public Variable {
        Objects.requireNonNull(name, "Variable name must not be null");
    }

    /**
     * Вычисляет значение переменной в заданном контексте.
     *
     * <p>Значение извлекается из контекста через
     * {@link EvaluationContext#getValue(String)}. Контекст обязан
     * содержать данную переменную, иначе выбрасывается исключение.
     *
     * @param context контекст вычисления со значениями переменных;
     *                не должен быть {@code null} и должен содержать
     *                переменную с именем {@link #name()}
     * @return числовое значение переменной из контекста
     * @throws NullPointerException
     *         если {@code context} равен {@code null}
     * @throws org.example.exception.UnknownVariableException
     *         если контекст не содержит переменной с именем {@link #name()}
     */
    @Override
    public double evaluate(EvaluationContext context) {
        return context.getValue(name);
    }
}