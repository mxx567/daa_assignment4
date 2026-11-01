package algorithms;

public class RubinKarp {
    public static java.util.List<Integer> searchAll(String text, String pattern) {
        java.util.List<Integer> matches = new java.util.ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0) {
            for (int i = 0; i <= n; i++) matches.add(i);
            return matches;
        }
        if (m > n) return matches;
        long base = 256, mod = 1_000_000_007L;
        long power = 1, target = 0, hash = 0;
        for (int i = 1; i < m; i++) power = power * base % mod;
        for (int i = 0; i < m; i++) {
            target = (target * base + pattern.charAt(i)) % mod;
            hash = (hash * base + text.charAt(i)) % mod;
        }
        for (int start = 0; start <= n - m; start++) {
            if (hash == target) {
                int j = 0;
                while (j < m && text.charAt(start + j) == pattern.charAt(j)) j++;
                if (j == m) matches.add(start);
            }
            if (start < n - m) {
                hash = (hash - text.charAt(start) * power % mod + mod) % mod;
                hash = (hash * base + text.charAt(start + m)) % mod;
            }
        }
        return matches;
    }


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
