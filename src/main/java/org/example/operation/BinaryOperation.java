package org.example.operation;

import org.example.exception.DivisionByZeroException;

public enum BinaryOperation {
    PLUS('+', 1, Associativity.LEFT) {
        @Override
        public double calculate(double left, double right) {
            return left + right;
        }
    },
    MINUS('-', 1, Associativity.LEFT) {
        @Override
        public double calculate(double left, double right) {
            return left - right;
        }
    },
    MULTIPLY('*', 2, Associativity.LEFT) {
        @Override
        public double calculate(double left, double right) {
            return left * right;
        }
    },
    DIVIDE('/', 2, Associativity.LEFT) {
        @Override
        public double calculate(double left, double right) {
            if (right == 0) {
                throw new DivisionByZeroException("Cannot divide by zero");
            }
            return left / right;
        }
    },
    POWER('^', 3, Associativity.RIGHT) {
        @Override
        public double calculate(double left, double right) {
            return Math.pow(left, right);
        }
    };

    private final char symbol;
    private final int precedence;
    private final Associativity associativity;

    BinaryOperation(char symbol, int precedence,  Associativity associativity) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.associativity = associativity;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getPrecedence() {
        return precedence;
    }

    public Associativity getAssociativity() {
        return associativity;
    }

    public abstract double calculate(double left, double right);
}
