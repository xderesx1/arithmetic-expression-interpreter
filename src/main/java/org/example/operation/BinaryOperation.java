package org.example.operation;

import org.example.exception.DivisionByZeroException;

/**
 * Перечисление бинарных арифметических операций с их метаданными и поведением.
 *
 * <p>Каждая константа хранит:
 * <ul>
 *   <li>{@linkplain #getSymbol() символ} операции для печати дерева в строку;</li>
 *   <li>{@linkplain #getPrecedence() приоритет} для определения порядка вычислений
 *       и расстановки минимальных скобок при печати;</li>
 *   <li>{@linkplain #getAssociativity() ассоциативность} для корректной группировки
 *       операций одного приоритета;</li>
 *   <li>реализацию {@link #calculate(double, double)} в теле константы — каждая
 *       операция сама знает, как вычисляться.</li>
 * </ul>
 *
 * <p>Таблица операций:
 * <table border="1">
 *   <tr><th>Константа</th><th>Символ</th><th>Приоритет</th><th>Ассоциативность</th></tr>
 *   <tr><td>{@link #PLUS}</td><td>{@code +}</td><td>1</td><td>левая</td></tr>
 *   <tr><td>{@link #MINUS}</td><td>{@code -}</td><td>1</td><td>левая</td></tr>
 *   <tr><td>{@link #MULTIPLY}</td><td>{@code *}</td><td>2</td><td>левая</td></tr>
 *   <tr><td>{@link #DIVIDE}</td><td>{@code /}</td><td>2</td><td>левая</td></tr>
 *   <tr><td>{@link #POWER}</td><td>{@code ^}</td><td>3</td><td>правая</td></tr>
 * </table>
 *
 * <p>Чем выше числовой приоритет, тем сильнее связывается операция. Степень имеет
 * наивысший приоритет среди бинарных операций и правую ассоциативность, поэтому
 * {@code 2 ^ 3 ^ 2} интерпретируется как {@code 2 ^ (3 ^ 2) = 512}.
 *
 * <p>Замечание о приоритетах: унарные операции имеют промежуточный приоритет
 * между умножением (2) и степенью (3). Поэтому {@code -2 ^ 2} интерпретируется
 * как {@code -(2 ^ 2) = -4}, а не как {@code (-2) ^ 2 = 4}. Данное соглашение
 * зафиксировано в грамматике парсера.
 *
 * @see Associativity
 * @see org.example.expression.Binary
 */
public enum BinaryOperation {

    /** Сложение: {@code left + right}. */
    PLUS('+', 1, Associativity.LEFT) {
        @Override
        public double calculate(double left, double right) {
            return left + right;
        }
    },

    /** Вычитание: {@code left - right}. */
    MINUS('-', 1, Associativity.LEFT) {
        @Override
        public double calculate(double left, double right) {
            return left - right;
        }
    },

    /** Умножение: {@code left * right}. */
    MULTIPLY('*', 2, Associativity.LEFT) {
        @Override
        public double calculate(double left, double right) {
            return left * right;
        }
    },

    /**
     * Деление: {@code left / right}.
     *
     * @throws DivisionByZeroException если {@code right} равен нулю
     */
    DIVIDE('/', 2, Associativity.LEFT) {
        @Override
        public double calculate(double left, double right) {
            if (right == 0) {
                throw new DivisionByZeroException("Cannot divide by zero");
            }
            return left / right;
        }
    },

    /**
     * Возведение в степень: {@code Math.pow(left, right)}.
     *
     * <p>Имеет правую ассоциативность: {@code a ^ b ^ c = a ^ (b ^ c)}.
     */
    POWER('^', 3, Associativity.RIGHT) {
        @Override
        public double calculate(double left, double right) {
            return Math.pow(left, right);
        }
    };

    private final char symbol;
    private final int precedence;
    private final Associativity associativity;

    /**
     * Конструктор константы перечисления.
     *
     * @param symbol        символьное представление операции
     * @param precedence    числовой приоритет (чем больше, тем сильнее связь)
     * @param associativity ассоциативность операции
     */
    BinaryOperation(char symbol, int precedence, Associativity associativity) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.associativity = associativity;
    }

    /**
     * Возвращает символ операции для печати дерева в строку.
     *
     * @return символ операции ({@code '+'}, {@code '-'}, {@code '*'},
     *         {@code '/'}, {@code '^'})
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Возвращает числовой приоритет операции.
     *
     * <p>Используется при печати дерева для определения необходимости скобок:
     * если приоритет дочерней операции ниже приоритета родительской, вокруг
     * дочернего выражения ставятся скобки.
     *
     * @return приоритет: 1 для {@code +}/{@code -}, 2 для {@code *}/{@code /},
     *         3 для {@code ^}
     */
    public int getPrecedence() {
        return precedence;
    }

    /**
     * Возвращает ассоциативность операции.
     *
     * <p>Определяет группировку операций одного приоритета. Левая ассоциативность
     * означает {@code (a op b) op c}, правая — {@code a op (b op c)}.
     *
     * @return {@link Associativity#LEFT} для арифметических операций,
     *         {@link Associativity#RIGHT} для степени
     */
    public Associativity getAssociativity() {
        return associativity;
    }

    /**
     * Применяет операцию к двум числовым операндам.
     *
     * <p>Каждая константа переопределяет этот метод в своём теле, реализуя
     * собственную семантику вычисления.
     *
     * @param left  левый операнд
     * @param right правый операнд
     * @return результат применения операции
     * @throws DivisionByZeroException только для {@link #DIVIDE}, если
     *                                 {@code right} равен нулю
     */
    public abstract double calculate(double left, double right);
}