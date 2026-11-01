import algorithms.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

/** Run with: java -cp out Experiments */
public class Experiments {
    private static final long SEED = 64;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int RUNS = 5;
    private static final int WARMUPS = 5;
    private static final int PATTERN_LENGTH = 20;
    private static volatile Object sink;
    private static final List<String> rows = new ArrayList<>();

    public static void run() throws IOException {
        rows.clear();
        Random random = new Random(SEED);
        // Correctness checks are never timed.
        require(LCS.solve("abc", "xyz") == 0, "LCS disjoint strings");
        require(LCS.solve("abcde", "ace") == 3, "LCS unequal lengths");
        require(EditDistance.minDistance("kitten", "sitting") == 3, "Edit distance");
        require(Arrays.equals(Kasai.solve("banana", new int[]{5, 3, 1, 0, 4, 2}),
                new int[]{0, 1, 3, 0, 0, 2}), "Kasai banana");

        System.out.println("seed=" + SEED + ", alphabet=" + ALPHABET
                + ", runs=" + RUNS + ", warmups=" + WARMUPS);
        rows.add("algorithm,size,case,pattern_length,average_ns,average_ms,result,dp_cells");
        for (int n : new int[]{100, 500, 1000}) {
            String a = randomString(random, n);
            String b = randomString(random, n);
            measure("LCS", n, "random-pair", 0, (long) n * n, () -> LCS.solve(a, b));
            measure("EditDistance", n, "random-pair", 0, (long) n * n,
                    () -> EditDistance.minDistance(a, b));
        }

        for (int n : new int[]{10000, 50000, 100000}) {
            String text = randomString(random, n);
            // A late match exercises most of the text; indexOf handles any earlier occurrence.
            String present = text.substring(n - PATTERN_LENGTH);
            String absent;
            do {
                absent = randomString(random, PATTERN_LENGTH);
            } while (text.contains(absent));
            compareMatchers(text, present, "present");
            compareMatchers(text, absent, "absent");
        }

        for (int n : new int[]{1000, 5000, 10000}) {
            String text = randomString(random, n);
            int[] sa = suffixArray(text); // Setup excluded from timing.
            verifyLcp(text, sa, Kasai.solve(text, sa));
            measure("Kasai", n, "random", 0, -1, () -> Kasai.solve(text, sa));
        }
        Files.write(Paths.get("experiments.csv"), rows, StandardCharsets.UTF_8);
        System.out.println("Saved " + (rows.size() - 1) + " results to "
                + Paths.get("experiments.csv").toAbsolutePath());
    }

    private static void compareMatchers(String text, String pattern, String label) {
        List<Integer> expected = Tests.expectedMatches(text, pattern);
        require(StringMatching.searchAll(text, pattern).equals(expected), "Naive " + label);
        require(KMP.searchAll(text, pattern).equals(expected), "KMP " + label);
        require(RubinKarp.searchAll(text, pattern).equals(expected), "Rabin-Karp " + label);
        measure("Naive", text.length(), label, pattern.length(), -1,
                () -> StringMatching.searchAll(text, pattern));
        measure("KMP", text.length(), label, pattern.length(), -1,
                () -> KMP.searchAll(text, pattern));
        measure("Rabin-Karp", text.length(), label, pattern.length(), -1,
                () -> RubinKarp.searchAll(text, pattern));
    }

    private static void measure(String name, int n, String label, int m,
                                long cells, Supplier<?> operation) {
        for (int i = 0; i < WARMUPS; i++) sink = operation.get();
        long total = 0;
        Object result = null;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            result = operation.get();
            long elapsed = System.nanoTime() - start;
            sink = result; // Consume results outside the timed region.
            total += elapsed;
        }
        double average = total / (double) RUNS;
        // Summarize arrays only after timing ends.
        String summary = result instanceof int[]
                ? "checksum:" + Arrays.hashCode((int[]) result) : String.valueOf(result);
        rows.add(String.format(Locale.ROOT, "%s,%d,%s,%d,%.1f,%.6f,\"%s\",%s",
                name, n, label, m, average, average / 1_000_000.0,
                summary.replace("\"", "\"\""), cells < 0 ? "NA" : Long.toString(cells)));
    }

    private static String randomString(Random random, int length) {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
        }
        return new String(chars);
    }

    // Simple setup-only construction: sort suffix indices without allocating substrings.
    private static int[] suffixArray(String text) {
        Integer[] indices = new Integer[text.length()];
        for (int i = 0; i < indices.length; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> {
            int i = a, j = b;
            while (i < text.length() && j < text.length()) {
                int comparison = Character.compare(text.charAt(i), text.charAt(j));
                if (comparison != 0) return comparison;
                i++;
                j++;
            }
            return Integer.compare(text.length() - a, text.length() - b);
        });
        return Arrays.stream(indices).mapToInt(Integer::intValue).toArray();
    }

    private static void verifyLcp(String text, int[] sa, int[] actual) {
        require(actual.length == text.length() && actual[0] == 0, "LCP shape");
        for (int r = 1; r < sa.length; r++) {
            int h = 0, a = sa[r - 1], b = sa[r];
            while (a + h < text.length() && b + h < text.length()
                    && text.charAt(a + h) == text.charAt(b + h)) h++;
            require(actual[r] == h, "LCP at rank " + r);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
