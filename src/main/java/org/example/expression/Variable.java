package org.example.expression;

import org.example.service.EvaluationContext;

import java.util.Objects;

public record Variable(String name) implements Expression {
    public Variable {
        Objects.requireNonNull(name, "Variable name must not be null");
    }
    @Override
    public double evaluate(EvaluationContext context) {
        return context.getValue(name);
    }
}
