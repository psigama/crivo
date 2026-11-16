package io.github.rogeriocisi.airlab.structured;

import java.time.Instant;

/**
 * The public verification package of a P1 study (SPEC E9): one canonical JSON document, written at
 * the unblinding, whose SHA-256 is in the audit trail. It carries what anyone needs to redo, outside
 * the application, the medium draw of every pair, the commitment of every reading, the scoring of
 * every pair and the three analyses — and nothing that names a person: no first name and no salt of
 * a request. {@link VerificationPackageCheck} is the reference verifier.
 */
public record VerificationPackage(String studyCode, String document, String sha256, Instant createdAt) {

    /** The format tag, first thing the verifier checks. */
    public static final String FORMAT = "crivo-pacote-p1/1";

    /** The file name a download gets. */
    public String fileName() {
        return "pacote-" + studyCode + ".json";
    }
}
