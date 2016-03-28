package tk.melnichuk.kommunalchik.Helpers;

/**
 * Created by al on 28.03.16.
 */

public class Fraction extends Number
        implements Comparable<Fraction> {

    private final int denominator;
    private final int numerator;

    public Fraction(double value) {
        this(value, 1.0e-5, 100);
    }

    public Fraction(double value, double epsilon, int maxIterations){
        this(value, epsilon, Integer.MAX_VALUE, maxIterations);
    }


    private Fraction(double value, double epsilon, int maxDenominator, int maxIterations){
        long overflow = Integer.MAX_VALUE;
        double r0 = value;
        long a0 = (long)Math.floor(r0);
        if (a0 > overflow) {
            System.out.println("ERROR");
        }

        // check for (almost) integer arguments, which should not go
        // to iterations.
        if (Math.abs(a0 - value) < epsilon) {
            this.numerator = (int) a0;
            this.denominator = 1;
            return;
        }

        long p0 = 1;
        long q0 = 0;
        long p1 = a0;
        long q1 = 1;

        long p2 = 0;
        long q2 = 1;

        int n = 0;
        boolean stop = false;
        do {
            ++n;
            double r1 = 1.0 / (r0 - a0);
            long a1 = (long)Math.floor(r1);
            p2 = (a1 * p1) + p0;
            q2 = (a1 * q1) + q0;
            if ((p2 > overflow) || (q2 > overflow)) {
                System.out.println("ERROR");
            }

            double convergent = (double)p2 / (double)q2;
            if (n < maxIterations && Math.abs(convergent - value) > epsilon && q2 < maxDenominator) {
                p0 = p1;
                p1 = p2;
                q0 = q1;
                q1 = q2;
                a0 = a1;
                r0 = r1;
            } else {
                stop = true;
            }
        } while (!stop);

        if (n >= maxIterations) {
            System.out.println("ERROR");
        }

        if (q2 < maxDenominator) {
            this.numerator = (int) p2;
            this.denominator = (int) q2;
        } else {
            this.numerator = (int) p1;
            this.denominator = (int) q1;
        }

    }

    public Fraction(int num) {
        this(num, 1);
    }

    public  int gcd(int a, int b) {
        if (b==0) return a;
        return gcd(b,a%b);
    }


    public Fraction(int num, int den) {
        if (den == 0) {
            System.out.println("ERROR");
        }
        if (den < 0) {
            if (num == Integer.MIN_VALUE || den == Integer.MIN_VALUE) {
                System.out.println("ERROR");
            }
            num = -num;
            den = -den;
        }

        // reduce numerator and denominator by greatest common denominator.
        final int d = this.gcd(num, den);
        if (d > 1) {
            num /= d;
            den /= d;
        }

        // move sign to numerator.
        if (den < 0) {
            num = -num;
            den = -den;
        }
        this.numerator   = num;
        this.denominator = den;
    }

    public Fraction abs() {
        Fraction ret;
        if (numerator >= 0) {
            ret = this;
        } else {
            ret = negate();
        }
        return ret;
    }

    public int compareTo(Fraction object) {
        long nOd = ((long) numerator) * object.denominator;
        long dOn = ((long) denominator) * object.numerator;
        return (nOd < dOn) ? -1 : ((nOd > dOn) ? +1 : 0);
    }


    @Override
    public double doubleValue() {
        return (double)numerator / (double)denominator;
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Fraction) {
            // since fractions are always in lowest terms, numerators and
            // denominators can be compared directly for equality.
            Fraction rhs = (Fraction)other;
            return (numerator == rhs.numerator) &&
                    (denominator == rhs.denominator);
        }
        return false;
    }

    @Override
    public float floatValue() {
        return (float)doubleValue();
    }

    public int getDenominator() {
        return denominator;
    }


    public int getNumerator() {
        return numerator;
    }

    @Override
    public int hashCode() {
        return 37 * (37 * 17 + numerator) + denominator;
    }

    @Override
    public int intValue() {
        return (int)doubleValue();
    }

    @Override
    public long longValue() {
        return (long)doubleValue();
    }


    public Fraction negate() {
        if (numerator==Integer.MIN_VALUE) {
            System.out.println("ERROR");
        }
        return new Fraction(-numerator, denominator);
    }


    @Override
    public String toString() {
        String str = null;
        if (denominator == 1) {
            str = Integer.toString(numerator);
        } else if (numerator == 0) {
            str = "0";
        } else {
            str = numerator + "/" + denominator;
        }
        return str;
    }


}