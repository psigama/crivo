package io.github.rogeriocisi.airlab.structured;

import io.github.rogeriocisi.airlab.analysis.BinomialTest;
import io.github.rogeriocisi.airlab.analysis.PermutationResult;
import io.github.rogeriocisi.airlab.analysis.StratifiedPermutationTest;
import io.github.rogeriocisi.airlab.analysis.WilcoxonResult;
import io.github.rogeriocisi.airlab.analysis.WilcoxonSignedRank;
import io.github.rogeriocisi.airlab.catalog.Questionnaire;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * The P1 scoring and analysis of a whole study (SPEC E13, phase D), as one public pure function.
 * The unblinding runs it on the revealed data, and the verification package (SPEC E9) runs it again
 * outside the application, on the published data; both must get the same numbers.
 *
 * <p>Inputs: the active pairs in code order, the answers of each sitter and of the reading made for
 * each request, the pairing stratum of each request and the revealed seed, which drives the
 * permutation test.
 */
public final class StructuredAnalysis {

    private StructuredAnalysis() {
    }

    /** An active pair: its code and its two requests, in the order the pair stores them. */
    public record Pair(String code, String requestA, String requestB) {
    }

    public record PairOutcome(String pairCode, StructuredScoring.PairScore score) {
    }

    public record Outcome(List<PairOutcome> pairs,
                          WilcoxonResult primary,
                          int positive,
                          int nonZero,
                          BigDecimal signP,
                          PermutationResult permutation) {

        public Outcome {
            pairs = List.copyOf(pairs);
        }

        /** The three p-values as the report and the trail write them: six decimals, half-even. */
        public String primaryP() {
            return plain(primary.pValue());
        }

        public String signPlain() {
            return plain(signP);
        }

        public String permutationP() {
            return plain(permutation.pValue());
        }
    }

    /**
     * @param sitterAnswers  answers of each sitter, by request code
     * @param readingAnswers answers of the reading made for each request, by request code
     * @param strata         pairing stratum of each request, by request code
     */
    public static Outcome run(Questionnaire questionnaire, List<Pair> pairs,
                              Map<String, Map<String, String>> sitterAnswers,
                              Map<String, Map<String, String>> readingAnswers,
                              Map<String, String> strata, String seedHex) {
        List<PairOutcome> outcomes = new ArrayList<>();
        List<Double> differences = new ArrayList<>();
        for (Pair pair : pairs) {
            StructuredScoring.PairScore score = StructuredScoring.score(questionnaire,
                    answers(sitterAnswers, pair.requestA()), answers(sitterAnswers, pair.requestB()),
                    answers(readingAnswers, pair.requestA()), answers(readingAnswers, pair.requestB()));
            outcomes.add(new PairOutcome(pair.code(), score));
            differences.add((double) score.d());
        }
        WilcoxonResult primary = WilcoxonSignedRank.greaterThanZero(differences);
        int positive = (int) differences.stream().filter(d -> d > 0).count();
        int nonZero = (int) differences.stream().filter(d -> d != 0).count();
        BigDecimal signP = BinomialTest.pValueGreaterOrEqual(positive, nonZero);
        PermutationResult permutation = permutation(questionnaire, pairs, sitterAnswers, readingAnswers, strata,
                seedHex);
        return new Outcome(outcomes, primary, positive, nonZero, signP, permutation);
    }

    /**
     * The permutation matrix: one row per sitter, one column per reading, in pair order (A, B); the
     * reading on the same index is the one made for that sitter's target. Readings move only within
     * the stratum of the pairing.
     */
    private static PermutationResult permutation(Questionnaire questionnaire, List<Pair> pairs,
                                                 Map<String, Map<String, String>> sitterAnswers,
                                                 Map<String, Map<String, String>> readingAnswers,
                                                 Map<String, String> strata, String seedHex) {
        List<String> order = new ArrayList<>();
        pairs.forEach(pair -> {
            order.add(pair.requestA());
            order.add(pair.requestB());
        });
        List<String> strataIndex = new ArrayList<>(new TreeSet<>(order.stream().map(strata::get).toList()));

        int n = order.size();
        double[][] matrix = new double[n][n];
        int[] stratum = new int[n];
        for (int i = 0; i < n; i++) {
            stratum[i] = strataIndex.indexOf(strata.get(order.get(i)));
            Map<String, String> sitter = answers(sitterAnswers, order.get(i));
            for (int j = 0; j < n; j++) {
                matrix[i][j] = StructuredScoring.answeredMatches(questionnaire, answers(readingAnswers, order.get(j)),
                        sitter);
            }
        }
        return StratifiedPermutationTest.diagonalExceedsChance(matrix, stratum, seedHex);
    }

    private static Map<String, String> answers(Map<String, Map<String, String>> byRequest, String requestCode) {
        Map<String, String> answers = byRequest.get(requestCode);
        return answers == null ? Map.of() : answers;
    }

    public static String plain(BigDecimal value) {
        return value.setScale(6, RoundingMode.HALF_EVEN).toPlainString();
    }
}
