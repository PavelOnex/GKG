package math;

import java.util.Random;

public class ArithmeticModule {
    public ArithmeticModule() {
    }
    public static int gcdExtended(int a, int b) {
        if (a == 0) {
            return b;
        }
        int gcd = gcdExtended(b % a, a);
        return gcd;
    }

    /**
     * Returns modulo inverse of a with
     * respect to m using extended Euclid
     * Algorithm Assumption: a and m are
     * coprimes, i.e., gcd(a, m) = 1
     */
    public static int modInverse(int a, int m) {
        int m0 = m;
        int y = 0, x = 1;

        if (m == 1)
            return 0;

        while (a > 1)
        {
            // q is quotient
            int q = a / m;
            int t = m;

            m = a % m;
            a = t;
            t = y;

            y = x - q * y;
            x = t;
        }
        // Make x positive
        if (x < 0)
            x += m0;

        return x;
    }


    public static int generatePrimalNumber(int MAX_VALUE) {
        Random rand = new Random(); // generate a random number
        int num = rand.nextInt(MAX_VALUE) + 1;
        while (!isPrime(num)) {
            num = rand.nextInt(MAX_VALUE) + 1;
        }
        return num;
    }

    /**
     * Checks to see if the requested value is prime.
     */
    private static boolean isPrime(int inputNum) {
        if (inputNum <= 3 || inputNum % 2 == 0)
            return inputNum == 2 || inputNum == 3; //this returns false if number is <=1 & true if number = 2 or 3
        int divisor = 3;
        while ((divisor <= Math.sqrt(inputNum)) && (inputNum % divisor != 0))
            divisor += 2; //iterates through all possible divisors
        return inputNum % divisor != 0; //returns true/false
    }

}
