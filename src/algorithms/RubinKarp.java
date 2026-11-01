package algorithms;

public class RubinKarp {

    public static int search(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        if (m == 0) return 0;
        if (m > n) return -1;

        long base = 256;
        long mod = 1_000_000_007L;

        long patternHash = 0;
        long windowHash = 0;
        long highestPower = 1;

        for (int i = 1; i < m; i++) {
            highestPower = (highestPower * base) % mod;
        }

        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * base + pattern.charAt(i)) % mod;

            windowHash = (windowHash * base + text.charAt(i)) % mod;
        }

        for (int start = 0; start <= n - m; start++) {
            if (patternHash == windowHash) {
                int j = 0;

                while (j < m && text.charAt(start + j) == pattern.charAt(j)) {
                    j++;
                }

                if (j == m) return start;
            }

            if (start < n - m) {
                long outgoing = (text.charAt(start) * highestPower) % mod;

                windowHash = (windowHash - outgoing + mod) % mod;

                windowHash = (windowHash * base + text.charAt(start + m)) % mod;
            }
        }

        return -1;
    }

}
