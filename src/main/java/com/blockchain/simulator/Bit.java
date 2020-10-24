package com.blockchain.simulator;

public enum Bit {
    ZERO("0"),
    ONE("1"),
    FLOOR("F");
    private final String name;
    Bit(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
