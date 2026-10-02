package org.example.exception;

/**
 * Исключение, сигнализирующее о том, что в выражении встретилась переменная,
 * отсутствующая в контексте вычисления.
 *
 * <p>Выбрасывается на этапе <b>вычисления</b>, когда узел
 * {@link org.example.expression.Variable} пытается разрешить своё имя через
 * {@link org.example.service.EvaluationContext#getValue(String)}, а контекст
 * не содержит переменной с таким именем.
 *
 * <p>Пример: при контексте {@code {x = 4, y = -2}} вычисление выражения
 * {@code q + 1} приведёт к этому исключению, поскольку переменная {@code q}
 * не определена.
 *
 * <p>Исключение является unchecked (наследуется от {@link RuntimeException}),
 * поэтому вызывающий код не обязан его обрабатывать, но демонстрационное
 * приложение и тесты перехватывают его для формирования читаемой диагностики.
 *
 * <p>Имя проблемной переменной сохраняется в поле {@link #variableName} и
 * доступно через {@link #getVariableName()}, что позволяет вызывающему коду
 * программно определить, какая именно переменная не была найдена, а не
 * разбирать текстовое сообщение.
 *
 * <p>В отличие от {@link SyntaxException}, это исключение не содержит позиции
 * в исходной строке: ошибка обнаруживается не в тексте выражения, а в
 * процессе вычисления дерева, где позиция уже не имеет смысла.
 *
 * @see org.example.expression.Variable
 * @see org.example.service.EvaluationContext
 */
public class UnknownVariableException extends RuntimeException {
    private final String variableName;

    /**
     * Создаёт исключение для переменной с заданным именем.
     *
     * <p>Сообщение исключения формируется автоматически в виде
     * {@code "Unknown variable: <имя>"}.
     *
     * @param variableName имя переменной, отсутствующей в контексте;
     *                     сохраняется и доступно через
     *                     {@link #getVariableName()}
     */
    public UnknownVariableException(String variableName) {
        super("Unknown variable: " + variableName);
        this.variableName = variableName;
    }

    /**
     * Возвращает имя переменной, отсутствующей в контексте вычисления.
     *
     * <p>Позволяет вызывающему коду программно получить проблемное имя,
     * не разбирая текстовое сообщение исключения.
     *
     * @return имя переменной, переданное в конструктор
     */
    public String getVariableName() {
        return variableName;
    }
}