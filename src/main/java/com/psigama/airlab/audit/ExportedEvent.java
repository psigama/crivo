package com.psigama.airlab.audit;

/**
 * One event of the trail as it leaves the database, for the verification package (SPEC E9): every
 * field that enters the hash, as text, plus the position, which does not.
 *
 * @param payload the canonical JSON of the payload, exactly as hashed
 */
public record ExportedEvent(long seq,
                            String occurredAt,
                            String eventType,
                            String actorRole,
                            String actorCode,
                            String subjectRef,
                            String payload,
                            String prevHash,
                            String hash) {
}
