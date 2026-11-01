import algorithms.*;
import java.util.*;

/** Dependency-free tests; failures throw even without Java's -ea option. */
public class Tests {
    private static int checks;

    public static void run() {
        checks = 0;
        equal(3, LCS.solve("abc", "abc"), "LCS identical");
        equal(0, LCS.solve("abc", "xyz"), "LCS disjoint");
        equal(0, LCS.solve("", "abc"), "LCS empty first");
        equal(0, LCS.solve("abc", ""), "LCS empty second");
        equal(0, LCS.solve("", ""), "LCS both empty");
        equal(3, LCS.solve("abcde", "ace"), "LCS subsequence");
        equal(3, LCS.solve("ace", "abcde"), "LCS unequal lengths reversed");
        equal(0, EditDistance.minDistance("abc", "abc"), "Edit identical");
        equal(3, EditDistance.minDistance("", "abc"), "Edit empty first");
        equal(3, EditDistance.minDistance("abc", ""), "Edit empty second");
        equal(0, EditDistance.minDistance("", ""), "Edit both empty");
        equal(1, EditDistance.minDistance("cat", "cut"), "Edit substitution");
        equal(1, EditDistance.minDistance("cat", "cart"), "Edit insertion");
        equal(1, EditDistance.minDistance("cart", "cat"), "Edit deletion");
        equal(3, EditDistance.minDistance("kitten", "sitting"), "Edit combined");

        matching("abcdef", "abc", Arrays.asList(0), "beginning");
        matching("xxabczz", "abc", Arrays.asList(2), "middle");
        matching("xyzabc", "abc", Arrays.asList(3), "end");
        matching("abcxabc", "abc", Arrays.asList(0, 4), "multiple");
        matching("aaaaa", "aaa", Arrays.asList(0, 1, 2), "overlapping");
        matching("abc", "xyz", Collections.emptyList(), "absent");
        matching("ab", "abc", Collections.emptyList(), "longer pattern");
        matching("abc", "", Arrays.asList(0, 1, 2, 3), "empty pattern");
        matching("", "", Arrays.asList(0), "both empty");
        matching("", "a", Collections.emptyList(), "empty text");
        matching("abc", "abc", Arrays.asList(0), "whole text");
        // With base 256 these two different UTF-16 pairs have identical hashes.
        matching("\u0001\u0000", "\u0000\u0100", Collections.emptyList(), "hash collision");
        lcp("banana", new int[]{5, 3, 1, 0, 4, 2}, new int[]{0, 1, 3, 0, 0, 2});
        lcp("aaaa", new int[]{3, 2, 1, 0}, new int[]{0, 1, 2, 3});
        lcp("abc", new int[]{0, 1, 2}, new int[]{0, 0, 0});
        lcp("x", new int[]{0}, new int[]{0});
        lcp("", new int[]{}, new int[]{});

        Random random = new Random(67);
        for (int t = 0; t < 200; t++) {
            String text = randomString(random, random.nextInt(31));
            String pattern = randomString(random, random.nextInt(9));
            matching(text, pattern, expectedMatches(text, pattern), "random " + t);
        }
        System.out.println("PASS: " + checks + " correctness checks");
    }

    private static void matching(String text, String pattern, List<Integer> expected, String label) {
        equal(expected, StringMatching.searchAll(text, pattern), "Naive " + label);
        equal(expected, KMP.searchAll(text, pattern), "KMP " + label);
        equal(expected, RubinKarp.searchAll(text, pattern), "Rabin-Karp " + label);
        int first = expected.isEmpty() ? -1 : expected.get(0);
        equal(first, StringMatching.naive(text, pattern), "Naive first " + label);
        equal(first, KMP.search(text, pattern), "KMP first " + label);
        equal(first, RubinKarp.search(text, pattern), "Rabin-Karp first " + label);
    }

    public static List<Integer> expectedMatches(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        for (int from = 0; from <= text.length(); ) {
            int found = text.indexOf(pattern, from);
            if (found < 0) break;
            result.add(found);
            from = found + 1;
        }
        return result;
    }

    private static void lcp(String text, int[] sa, int[] expected) {
        equal(Arrays.toString(expected), Arrays.toString(Kasai.solve(text, sa)), "Kasai " + text);
    }

    private static String randomString(Random random, int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) result.append("abc".charAt(random.nextInt(3)));
        return result.toString();
    }

    private static void equal(Object expected, Object actual, String label) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
        checks++;
    }
}
