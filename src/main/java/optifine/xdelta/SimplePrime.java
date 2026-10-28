package optifine.xdelta;

public class SimplePrime {
    public static long belowOrEqual(long l) {
        if (l < 2L) {
            return 0L;
        }

        if (l == 2L) {
            return 2L;
        }

        if ((l & 1L) == 0L) {
            l--;
        }

        while (!testPrime(l)) {
            l -= 2L;
            if (l <= 2L) {
                return 2L;
            }
        }

        return l;
    }

    public static long aboveOrEqual(long l) {
        if (l <= 2L) {
            return 2L;
        }

        if ((l & 1L) == 0L) {
            l++;
        }

        while (!testPrime(l)) {
            l += 2L;
            if (l < 0L) {
                return 0L;
            }
        }

        return l;
    }

    public static boolean testPrime(long l) {
        if (l == 2L) {
            return true;
        }

        if (l < 2L) {
            return false;
        }

        if ((l & 1L) == 0L) {
            return false;
        }

        long floor = (long)Math.floor(Math.sqrt(l));

        for (long i = 3L; i <= floor; i += 2L) {
            if (l % i == 0L) {
                return false;
            }
        }

        return true;
    }
}
