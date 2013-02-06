package com.vangercat.data;

import com.vangercat.data.helpers.CyclicListOfQuaternions;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 05.11.2009<p>
 * Time: 22:54:53<p>
 */
public class Quaternion {
    private double Q0; // a
    private double Qi; // b
    private double Qj; // c
    private double Qk; // d

    public static final Quaternion a = new Quaternion(1, 0, 0, 0);
    public static final Quaternion i = new Quaternion(0, 1, 0, 0);
    public static final Quaternion j = new Quaternion(0, 0, 1, 0);
    public static final Quaternion k = new Quaternion(0, 0, 0, 1);
    public static final Quaternion zero = new Quaternion(0, 0, 0, 0);

    private static final CyclicListOfQuaternions POWER_OF_I = new CyclicListOfQuaternions(4, new Quaternion(0, 1, 0, 0));
    private static final CyclicListOfQuaternions POWER_OF_J = new CyclicListOfQuaternions(4, new Quaternion(0, 0, 1, 0));
    public static final double pricesion = 100d;

    public Quaternion(double q0, double qi, double qj, double qk) {
        Q0 = q0;
        Qi = qi;
        Qj = qj;
        Qk = qk;
    }

    public double getQ0() {
        return Q0;
    }

    public double getQi() {
        return Qi;
    }

    public double getQj() {
        return Qj;
    }

    public double getQk() {
        return Qk;
    }

    // 1 * j = j
    // i * j = k
    // j * j = -1
    // k * j = -i
    public Quaternion mulOnJ() {
        return new Quaternion( - this.getQj(),  - this.getQk(), this.getQ0(), this.getQi());
    }

    // i * 1 = i
    // i * i = -1
    // i * j = k
    // i * k = - j   
    public Quaternion mulIOn() {
        return new Quaternion( - this.getQi(), this.getQ0(), - this.getQk(), this.getQj());
    }

    public Quaternion sum(Quaternion that) {
        return new Quaternion(
                this.getQ0() + that.getQ0(),
                this.getQi() + that.getQi(),
                this.getQj() + that.getQj(),
                this.getQk() + that.getQk()
        );
    }

    public Quaternion mul(double val) {
        return new Quaternion(                                       /**/
                this.getQ0() * val,
                this.getQi() * val,
                this.getQj() * val,
                this.getQk() * val

        );
    }

    public Quaternion mul(Quaternion that) {
        return new Quaternion(
                this.Q0*that.Q0 - this.Qi*that.Qi - this.Qj*that.Qj - this.Qk*that.Qk,
                this.Q0*that.Qi + this.Qi*that.Q0 + this.Qj*that.Qk - this.Qk*that.Qj,
                this.Q0*that.Qj + this.Qj*that.Q0 + this.Qk*that.Qi - this.Qi*that.Qk,
                this.Q0*that.Qk + this.Qk*that.Q0 + this.Qi*that.Qj - this.Qj*that.Qi
        );
    }

    public Quaternion mulPowOfIOn(Integer pow) {
        int t = pow % 4;
        switch (t) {
        case 0: return this;
        case 1: return this.mulIOn();
        case 2: return this.mul(-1);
        case 3: return this.mulIOn().mul(-1);
        default: return null;
        }
    }

    public Quaternion mulOnPowOfJ(Integer pow) {
        int t = pow % 4;
        switch (t) {
        case 0: return this;
        case 1: return this.mulOnJ();
        case 2: return this.mul(-1);
        case 3: return this.mulOnJ().mul(-1);
        default: return null;
        }
    }

    public Quaternion mulABOn(Quaternion w1) {
//        return this.mulIOn().mul(w1.getQi()).sum(this.mul(w1.Q0));
        return new Quaternion(
                Q0*w1.Q0 - Qi*w1.Qi,
                Qi*w1.Q0 + Q0*w1.Qi,
                Qj*w1.Q0 - Qk*w1.Qi,
                Qk*w1.Q0 + Qj*w1.Qi
        );
    }

    public Quaternion mulOnAC(Quaternion w2) {
//        return this.mulOnJ().mul(w2.getQj()).sum(this.mul(w2.Q0));
        return new Quaternion(
                Q0*w2.Q0 - Qi*w2.Qj,
                Qi*w2.Q0 - Qk*w2.Qj,
                Qj*w2.Q0 + Q0*w2.Qj,
                Qk*w2.Q0 + Qj*w2.Qj
        );
    }

    static public Quaternion iPow(int N) {
        return POWER_OF_I.get(N % 4);
    }

    static public Quaternion jPow(int N) {
        return POWER_OF_J.get(N % 4);
    }

    public Quaternion epsilonI() {
        return new Quaternion(Q0, Qi, -Qj, -Qk);
    }

    public Quaternion epsilonJ() {
        return new Quaternion(Q0, -Qi, Qj, -Qk);
    }

    public Quaternion epsilonK() {
        return new Quaternion(Q0, -Qi, -Qj, Qk);
    }

    @Override
    public String toString() {
//        return toFloatComplexRepresentation();
//        return toIntegerComplexRepresentation();/**/
//        return "(" + Math.round(Q0*pricesion)/pricesion + ", "+ Math.round(Qi*pricesion)/pricesion + ", " + Math.round(Qj*pricesion)/pricesion + ", " + Math.round(Qk*pricesion)/pricesion + ")";
//        return "(" + (Q0) + ", "+ (Qi) + ", " + (Qj) + ", " + (Qk) + ")";
//        return "" + Math.round(Q0);

        return getComplexForm().toString();
    }

    public ComplexNumber getComplexForm() {
        return new ComplexNumber((Q0 - Qk), (Qi + Qj));
    }

    public String complexPart() {
        double b = Math.round((Qi + Qj) * pricesion) / (pricesion);
        return "" + b;
    }

    public String realPart() {
        double a = Math.round((Q0 - Qk) * pricesion) / (pricesion);
        return "" + a;
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

    private String toIntegerComplexRepresentation() {
        long a = Math.round(Q0 - Qk);
        long b = Math.round(Qi + Qj);

        String ans = "" + a + sign(b) + Math.abs(b) + "i";

        while (ans.length() < 13) {
            ans = " " + ans;
        }
        return  ans;
    }

    private String toFloatComplexRepresentation() {
        double a = Math.round((Q0 - Qk) * pricesion) / (pricesion);
        double b = Math.round((Qi + Qj) * pricesion) / (pricesion);

        String ans = "" + a + sign(b) + Math.abs(b) + "i";

        while (ans.length() < 13) {
            ans = " " + ans;
        }
        return  ans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quaternion)) return false;

        Quaternion that = (Quaternion) o;

        if (Double.compare(that.Q0, Q0) != 0) return false;
        if (Double.compare(that.Qi, Qi) != 0) return false;
        if (Double.compare(that.Qj, Qj) != 0) return false;
        if (Double.compare(that.Qk, Qk) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Q0 != +0.0d ? Double.doubleToLongBits(Q0) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = Qi != +0.0d ? Double.doubleToLongBits(Qi) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Qj != +0.0d ? Double.doubleToLongBits(Qj) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Qk != +0.0d ? Double.doubleToLongBits(Qk) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
