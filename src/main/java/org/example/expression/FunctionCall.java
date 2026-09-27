package org.example.expression;

import org.example.operation.FunctionOperation;
import org.example.service.EvaluationContext;

import java.util.List;
import java.util.Objects;

public record FunctionCall(
        FunctionOperation operation,
        List<Expression> arguments

) implements Expression {
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
