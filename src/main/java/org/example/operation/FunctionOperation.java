package org.example.operation;

public enum FunctionOperation {
    MIN("min", 2) {
        @Override
        public double calculate(double... values) {
            return Math.min(values[0], values[1]);
        }
    },
    MAX("max", 2) {
        @Override
        public double calculate(double... values) {
            return Math.max(values[0], values[1]);
        }
    },
    ABS("abs", 1) {
        @Override
        public double calculate(double... values) {
            return Math.abs(values[0]);
        }
    };

    private final String name;
    private final int arity;

    FunctionOperation(String name, int arity) {
        this.name = name;
        this.arity = arity;
    }

    public String getName() {
        return name;
    }
    public int getArity() {
        return arity;
    }

    public abstract double calculate(double... values);
}
