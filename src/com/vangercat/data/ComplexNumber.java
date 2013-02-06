package com.vangercat.data;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 17.12.2009<p>
 * Time: 18:37:46<p>
 */
public class ComplexNumber {
    public static final double pricesion = 100;

    public static final ComplexNumber a0 = new ComplexNumber(1, 0);
    public static final ComplexNumber i = new ComplexNumber(0, 1);

    private double a;
    private double b;

    public ComplexNumber(double a, double b) {
        this.a = a;
        this.b = b;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public ComplexNumber sum(ComplexNumber that) {
        return
                new ComplexNumber(
                        getA() + that.getA(),
                        getB() + that.getB()
                );
    }

    public ComplexNumber mul(ComplexNumber that) {
        return
                new ComplexNumber(
                        getA()*that.getA() - getB()*that.getB(),
                        getA()*that.getB() + getB()*that.getA()
                );
    }

    @Override
    public String toString() {
//        return "(" + Math.round(a*pricesion)/pricesion + ", "+ Math.round(b*pricesion)/pricesion + ")";
        return toFloatComplexRepresentation();
    }

    private char sign(double a) {
        char c;

        if (a >= 0) {
            c = '+';
        } else {
            c = '-';
        }

        return c;
    }

    private String toFloatComplexRepresentation() {
        String ans = "" + Math.round(a*pricesion)/pricesion + sign(Math.abs(Math.round(b*pricesion)/pricesion)) + Math.abs(Math.round(b*pricesion)/pricesion) + "i";

        while (ans.length() < 13) {
            ans = " " + ans;
        }
        return  ans;
    }
}
