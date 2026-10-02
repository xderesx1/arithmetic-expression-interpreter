package org.example.operation;

/**
 * Перечисление унарных операций языка выражений с их поведением.
 *
 * <p>В отличие от {@link BinaryOperation}, унарные операции не хранят
 * метаданных о приоритете и ассоциативности: эти характеристики
 * зафиксированы в грамматике парсера и учитываются при печати дерева
 * как единое правило для всех унарных операций (приоритет выше
 * умножения, но ниже возведения в степень).
 *
 * <p>Каждая константа реализует {@link #calculate(double)} в своём теле,
 * определяя собственную семантику вычисления.
 *
 * <p>Таблица операций:
 * <table border="1">
 *   <tr><th>Константа</th><th>Семантика</th></tr>
 *   <tr><td>{@link #NEGATIVE}</td><td>арифметическое отрицание: {@code -value}</td></tr>
 *   <tr><td>{@link #POSITIVE}</td><td>унарный плюс (тождество): {@code value}</td></tr>
 * </table>
 *
 * @see org.example.expression.Unary
 */
public enum UnaryOperation {

    /** Арифметическое отрицание: возвращает {@code -value}. */
    NEGATIVE {
        @Override
        public double calculate(double value) {
            return -value;
        }
    },

    /** Унарный плюс (тождественная операция): возвращает {@code value} без изменений. */
    POSITIVE {
        @Override
        public double calculate(double value) {
            return value;
        }
    };

    /**
     * Применяет унарную операцию к числовому операнду.
     *
     * <p>Каждая константа переопределяет этот метод в своём теле, реализуя
     * собственную семантику вычисления.
     *
     * @param value числовой операнд
     * @return результат применения операции ({@code -value} для {@link #NEGATIVE},
     *         {@code value} для {@link #POSITIVE})
     */
    public abstract double calculate(double value);
}