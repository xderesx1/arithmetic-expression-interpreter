package org.example.operation;

import java.util.Optional;

/**
 * Перечисление встроенных функций языка выражений с их метаданными и поведением.
 *
 * <p>Каждая константа хранит:
 * <ul>
 *   <li>{@linkplain #getName() имя} функции для разбора вызовов в парсере и
 *       печати дерева в строку;</li>
 *   <li>{@linkplain #getArity() арность} — фиксированное количество аргументов,
 *       которое проверяется при построении узла {@link org.example.expression.FunctionCall};</li>
 *   <li>реализацию {@link #calculate(double...)} в теле константы — каждая функция
 *       сама знает, как вычисляться.</li>
 * </ul>
 *
 * <p>Таблица функций:
 * <table border="1">
 *   <tr><th>Константа</th><th>Имя</th><th>Арность</th><th>Семантика</th></tr>
 *   <tr><td>{@link #MIN}</td><td>{@code min}</td><td>2</td><td>минимум из двух аргументов</td></tr>
 *   <tr><td>{@link #MAX}</td><td>{@code max}</td><td>2</td><td>максимум из двух аргументов</td></tr>
 *   <tr><td>{@link #ABS}</td><td>{@code abs}</td><td>1</td><td>модуль числа</td></tr>
 * </table>
 *
 * <p>Набор функций закрыт: добавление новой функции требует изменения этого
 * перечисления. Поиск функции по имени выполняется через статический метод
 * {@link #fromName(String)}; неизвестное имя даёт {@link Optional#empty()},
 * что парсер интерпретирует как синтаксическую ошибку.
 *
 * @see org.example.expression.FunctionCall
 */
public enum FunctionOperation {

    /** Минимум из двух аргументов: {@code Math.min(values[0], values[1])}. */
    MIN("min", 2) {
        @Override
        public double calculate(double... values) {
            return Math.min(values[0], values[1]);
        }
    },

    /** Максимум из двух аргументов: {@code Math.max(values[0], values[1])}. */
    MAX("max", 2) {
        @Override
        public double calculate(double... values) {
            return Math.max(values[0], values[1]);
        }
    },

    /** Модуль числа: {@code Math.abs(values[0])}. */
    ABS("abs", 1) {
        @Override
        public double calculate(double... values) {
            return Math.abs(values[0]);
        }
    };

    private final String name;
    private final int arity;

    /**
     * Конструктор константы перечисления.
     *
     * @param name  имя функции, используемое при разборе и печати
     * @param arity фиксированное количество аргументов функции
     */
    FunctionOperation(String name, int arity) {
        this.name = name;
        this.arity = arity;
    }

    /**
     * Возвращает имя функции для разбора вызовов и печати дерева.
     *
     * @return имя функции ({@code "min"}, {@code "max"}, {@code "abs"})
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает арность функции — фиксированное количество аргументов.
     *
     * <p>Используется при построении узла {@link org.example.expression.FunctionCall}
     * для проверки соответствия количества переданных аргументов ожидаемому.
     *
     * @return количество аргументов: 2 для {@link #MIN}/{@link #MAX},
     *         1 для {@link #ABS}
     */
    public int getArity() {
        return arity;
    }

    /**
     * Применяет функцию к переданным числовым аргументам.
     *
     * <p>Каждая константа переопределяет этот метод в своём теле, реализуя
     * собственную семантику вычисления. Количество аргументов должно совпадать
     * с {@link #getArity()}; при несоответствии поведение не определено
     * (возможно {@link ArrayIndexOutOfBoundsException}). Проверка арности
     * выполняется на этапе построения узла, а не здесь.
     *
     * @param values числовые аргументы функции
     * @return результат применения функции
     */
    public abstract double calculate(double... values);

    /**
     * Ищет функцию по её имени.
     *
     * <p>Выполняет линейный поиск по всем константам перечисления, сравнивая
     * имя с учётом регистра. Используется парсером для распознавания вызовов
     * функций: если имя найдено, создаётся узел {@link org.example.expression.FunctionCall};
     * если нет — выбрасывается синтаксическая ошибка.
     *
     * @param name имя функции для поиска; сравнение регистрозависимое
     * @return {@link Optional} с найденной функцией, или {@link Optional#empty()},
     *         если функция с таким именем не существует
     */
    public static Optional<FunctionOperation> fromName(String name) {
        for (FunctionOperation operation : values()) {
            if (operation.getName().equals(name)) {
                return Optional.of(operation);
            }
        }

        return Optional.empty();
    }
}