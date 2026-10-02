package org.example.service;

import org.example.exception.UnknownVariableException;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Неизменяемый контекст вычисления выражений: отображение имён переменных
 * на их числовые значения.
 *
 * <p>Контекст передаётся в метод {@link org.example.expression.Expression#evaluate(EvaluationContext)}
 * и используется узлами {@link org.example.expression.Variable} для разрешения
 * имён переменных в значения.
 *
 * <p>Инкапсуляция: переданная в конструктор карта защищённо копируется через
 * {@link Map#copyOf(Map)}, поэтому последующие изменения исходной карты не
 * влияют на контекст, а сам контекст не предоставляет методов изменения.
 * Метод {@link #getVariableNames()} возвращает неизменяемое множество имён,
 * полученное от неизменяемой внутренней карты.
 *
 * <p>Пример создания:
 * <pre>
 * EvaluationContext context = new EvaluationContext(Map.of(
 *         "x", 4.0,
 *         "y", -2.0
 * ));
 * </pre>
 *
 * <p>Класс неизменяем и потокобезопасен.
 *
 * @see org.example.expression.Expression
 * @see org.example.expression.Variable
 */
public class EvaluationContext {
    private final Map<String, Double> variables;

    /**
     * Создаёт контекст вычисления с заданными значениями переменных.
     *
     * <p>Переданная карта защищённо копируется через {@link Map#copyOf(Map)},
     * поэтому контекст не зависит от дальнейших изменений исходной карты.
     *
     * @param source отображение имён переменных на их значения;
     *               не должно быть {@code null}
     * @throws NullPointerException если {@code source} равно {@code null}
     */
    public EvaluationContext(Map<String, Double> source) {
        Objects.requireNonNull(source, "Map with variables must not be null");
        this.variables = Map.copyOf(source);
    }

    /**
     * Проверяет, содержится ли переменная с заданным именем в контексте.
     *
     * @param variableName имя переменной для проверки; не должно быть {@code null}
     * @return {@code true}, если контекст содержит переменную с таким именем,
     *         иначе {@code false}
     * @throws NullPointerException если {@code variableName} равно {@code null}
     */
    public boolean containsVariable(String variableName) {
        Objects.requireNonNull(variableName, "Variable name must not be null");
        return variables.containsKey(variableName);
    }

    /**
     * Возвращает значение переменной с заданным именем.
     *
     * @param variableName имя переменной; не должно быть {@code null}
     * @return числовое значение переменной
     * @throws NullPointerException                     если {@code variableName}
     *                                                  равно {@code null}
     * @throws org.example.exception.UnknownVariableException
     *         если контекст не содержит переменной с таким именем
     */
    public Double getValue(String variableName) {
        Objects.requireNonNull(variableName, "Variable name must not be null");

        if (!variables.containsKey(variableName)) {
            throw new UnknownVariableException(variableName);
        }

        return variables.get(variableName);
    }

    /**
     * Возвращает множество имён всех переменных контекста.
     *
     * <p>Возвращаемое множество неизменяемо (получено от неизменяемой
     * внутренней карты), поэтому попытки изменения приведут к
     * {@link UnsupportedOperationException}.
     *
     * @return неизменяемое множество имён переменных
     */
    public Set<String> getVariableNames() {
        return variables.keySet();
    }
}