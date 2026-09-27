package org.example.exception;

public class UnknownVariableException extends RuntimeException {
    private final String variableName;
    public UnknownVariableException(String variableName) {
        super("Unknown variable: " + variableName);
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }
}
