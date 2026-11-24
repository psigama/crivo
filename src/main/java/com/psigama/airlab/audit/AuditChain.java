package com.psigama.airlab.audit;

import com.psigama.airlab.support.CanonicalJson;
import com.psigama.airlab.support.Hashes;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The hash rule of the audit chain, as a public pure function, so that the trail exported in a
 * verification package (SPEC E9) can be recomputed with no database and no knowledge of how the
 * rows were numbered. The application stores every event with exactly this hash.
 *
 * <pre>
 * hash = SHA-256( CanonicalJson{ "actorCode", "actorRole", "eventType", "occurredAt",
 *                                "payload", "prevHash", "subjectRef" } )
 * </pre>
 *
 * <p>{@code occurredAt} is the ISO-8601 instant as Java prints it, {@code payload} the canonical JSON
 * of the payload as a string, and the first event points at 64 zeros.
 */
public final class AuditChain {

    private AuditChain() {
    }

    public static String hashOf(String occurredAt, String eventType, String actorRole, String actorCode,
                                String subjectRef, String payload, String prevHash) {
        Map<String, Object> preimage = new LinkedHashMap<>();
        preimage.put("occurredAt", occurredAt);
        preimage.put("eventType", eventType);
        preimage.put("actorRole", actorRole);
        preimage.put("actorCode", actorCode);
        preimage.put("subjectRef", subjectRef);
        preimage.put("payload", payload);
        preimage.put("prevHash", prevHash);
        return Hashes.sha256Hex(CanonicalJson.write(preimage));
    }

    public static String hashOf(ExportedEvent event) {
        return hashOf(event.occurredAt(), event.eventType(), event.actorRole(), event.actorCode(),
                event.subjectRef(), event.payload(), event.prevHash());
    }

    /**
     * Recomputes an exported trail from its first event: each one must point at the one before it
     * (the first at 64 zeros) and hash to what it says.
     */
    public static ChainVerification verify(List<ExportedEvent> events) {
        String expectedPrev = Hashes.ZERO;
        long checked = 0;
        for (ExportedEvent event : events) {
            checked++;
            if (!expectedPrev.equals(event.prevHash())) {
                return ChainVerification.broken(checked, event.seq(),
                        "Evento " + event.seq() + " aponta para um antecessor que nao e o anterior da cadeia.");
            }
            if (!hashOf(event).equals(event.hash())) {
                return ChainVerification.broken(checked, event.seq(),
                        "Evento " + event.seq() + " foi alterado: o hash recalculado nao confere com o gravado.");
            }
            expectedPrev = event.hash();
        }
        return ChainVerification.intact(checked);
    }
}
