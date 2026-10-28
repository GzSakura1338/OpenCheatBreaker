package optifine.xdelta;

public class EratosthenesPrimes {
    public static BitArray sieve;
    public static int lastInit = -1;

    public static synchronized void reset() {
        sieve = null;
        lastInit = -1;
    }

    public static synchronized void init(int n) {
        if (n > lastInit) {
            int ceil = (int)Math.ceil(Math.sqrt(n));
            lastInit = n;
            n >>= 1;
            n++;
            ceil >>= 1;
            ceil++;
            sieve = new BitArray(n + 1);
            sieve.set(0, true);

            for (int i = 1; i <= ceil; i++) {
                if (!sieve.get(i)) {
                    int n2 = (i << 1) + 1;

                    for (int j = i * ((i << 1) + 2); j <= n; j += n2) {
                        sieve.set(j, true);
                    }
                }
            }
        }
    }

    public static synchronized int[] getPrimes(int n) {
        int primesCount2 = primesCount(n);
        if (primesCount2 <= 0) {
            return new int[0];
        }

        if (n == 2) {
            return new int[]{2};
        }

        init(n);
        int[] ints = new int[primesCount2];
        int n2 = n - 1 >> 1;
        byte b = 0;
        int n3 = b + 1;
        ints[b] = 2;

        for (int i = 1; i <= n2; i++) {
            if (!sieve.get(i)) {
                ints[n3++] = (i << 1) + 1;
            }
        }

        return ints;
    }

    public static synchronized int primesCount(int n) {
        if (n < 2) {
            return 0;
        }

        init(n);
        int n2 = n - 1 >> 1;
        int n3 = 1;

        for (int i = 1; i <= n2; i++) {
            if (!sieve.get(i)) {
                n3++;
            }
        }

        return n3;
    }

    public static synchronized int belowOrEqual(int n) {
        if (n < 2) {
            return -1;
        }

        if (n == 2) {
            return 2;
        }

        init(n);
        int n2 = n - 1 >> 1;

        for (int i = n2; i > 0; i--) {
            if (!sieve.get(i)) {
                return (i << 1) + 1;
            }
        }

        return -1;
    }

    public static int below(int n) {
        return belowOrEqual(n - 1);
    }
}
