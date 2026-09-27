package org.example.operation;

public enum UnaryOperation {
    NEGATIVE {
        @Override
        public double calculate(double value) {
            return -value;
        }
    },
    POSITIVE {
        @Override
        public double calculate(double value) {
            return value;
        }
    };

    public abstract double calculate(double value);
}
