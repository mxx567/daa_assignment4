package algorithms;

public class Kasai {
    public static int[] solve(String text, int[] sa) {
        int n = text.length();

        int[] rank = new int[n];
        int[] lcp = new int[n];

        for (int r = 0; r < n; r++) {
            rank[sa[r]] = r;
        }

        int h = 0;

        for (int i = 0; i < n; i++) {
            int r = rank[i];

            if (r == 0) {
                h = 0;
                continue;
            }

            int j = sa[r - 1];

            while (i + h < n
                    && j + h < n
                    && text.charAt(i + h) == text.charAt(j + h)) {
                h++;
            }

            lcp[r] = h;

            if (h > 0) {
                h--;
            }
        }

        return lcp;
    }
}
