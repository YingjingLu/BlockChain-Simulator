package com.blockchain.simulator;

/**
 * class representation of Bit
 */
public enum Bit {
    ZERO("0"),
    ONE("1"),
    FLOOR("F");
    private final String name;

    /**
     * Construct bit from string name
     * @param name
     */
    Bit(String name) {
        this.name = name;
    }

    /**
     * Convert the bot to string representation
     * @return
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Negate the bit of current value. If 0 return 1, if 1 return 0, floor does not have negation
     * @return
     */
    public Bit negateBit() {
        if (this.name.equals("0")) {
            return Bit.ONE;
        } else if (this.name.equals("1")) {
            return Bit.ZERO;
        } else {
            return Bit.FLOOR;
        }
    }

    /**
     * Convert string representation to Bit value
     * @param s
     * @return
     */
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
