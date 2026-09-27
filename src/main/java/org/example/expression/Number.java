package org.example.expression;

import org.example.service.EvaluationContext;

public record Number(double value) implements Expression{
    @Override
    public double evaluate(EvaluationContext context) {
        return value;
    }
}
