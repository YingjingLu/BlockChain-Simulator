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

    public Bit negateBit() {
        if (this.name.equals("0")) {
            return Bit.ONE;
        } else if (this.name.equals("1")) {
            return Bit.ZERO;
        } else {
            return Bit.FLOOR;
        }
    }

    public static Bit stringToBit(String s) {
        if (s.equals("0")) {
            return Bit.ZERO;
        } else if (s.equals("1")) {
            return Bit.ONE;
        } else {
            return Bit.FLOOR;
        }
    }
}
