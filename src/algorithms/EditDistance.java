package algorithms;

public class EditDistance {
    public static int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        int[][] cache = new int[m + 1][n + 1];

        for (int j = 0; j <= n; j++) {
            cache[m][j] = n - j;
        }

        for (int i = 0; i <= m; i++) {
            cache[i][n] = m - i;
        }

        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (word1.charAt(i) == word2.charAt(j)) {
                    cache[i][j] = cache[i + 1][j + 1];
                } else {
                    cache[i][j] = 1 + Math.min(cache[i + 1][j],
                            Math.min(cache[i][j + 1], cache[i + 1][j + 1]));
                }
            }
        }

        return cache[0][0];
    }

}
