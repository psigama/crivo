package com.psigama.airlab.structured.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The verification package of a P1 study (V24, SPEC E9). Written once, at the unblinding, right
 * after the report; the database refuses it for a study that is not unblinded and refuses any
 * change afterwards.
 */
@Entity
@Table(name = "structured_package", schema = "research")
class StructuredPackageEntity {

    @Id
    @Column(name = "study_id")
    private Long studyId;

    @Column(name = "document", nullable = false, updatable = false)
    private String document;

    @Column(name = "document_sha256", nullable = false, length = 64, updatable = false)
    private String documentSha256;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected StructuredPackageEntity() {
    }

    StructuredPackageEntity(Long studyId, String document, String documentSha256, Instant createdAt) {
        this.studyId = studyId;
        this.document = document;
        this.documentSha256 = documentSha256;
        this.createdAt = createdAt;
    }

    String document() {
        return document;
    }

    String documentSha256() {
        return documentSha256;
    }

    Instant createdAt() {
        return createdAt;
    }
}

interface StructuredPackageRepository extends JpaRepository<StructuredPackageEntity, Long> {
}
