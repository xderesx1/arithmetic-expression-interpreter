package org.example.service;

import org.example.exception.UnknownVariableException;

import java.util.Map;
import java.util.Objects;

public class EvaluationContext {
    private final Map<String, Double> variables;

    public EvaluationContext(Map<String, Double> source) {
        Objects.requireNonNull(source, "Map with variables must not be null");
        this.variables = Map.copyOf(source);
    }

    public boolean containsVariable(String variableName) {
        Objects.requireNonNull(variableName, "Variable name must not be null");
        return variables.containsKey(variableName);
    }

    public Double getValue(String variableName) {
        Objects.requireNonNull(variableName, "Variable name must not be null");

        if (!variables.containsKey(variableName)) {
            throw new UnknownVariableException(variableName);
        }

        return variables.get(variableName);
    }
}
