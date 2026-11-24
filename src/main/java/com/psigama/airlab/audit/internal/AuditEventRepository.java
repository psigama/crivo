package com.psigama.airlab.audit.internal;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    Optional<AuditEvent> findFirstByOrderBySeqDesc();

    @Query("select e from AuditEvent e order by e.seq asc")
    Stream<AuditEvent> streamAllOrdered();

    @Query("select e from AuditEvent e where e.seq <= :position order by e.seq asc")
    java.util.List<AuditEvent> findUpTo(@org.springframework.data.repository.query.Param("position") long position);

    Optional<AuditEvent> findFirstByEventTypeAndSubjectRefOrderBySeqDesc(
            com.psigama.airlab.audit.AuditEventType eventType, String subjectRef);

    @Query("select distinct e.eventType from AuditEvent e where e.seq > :position")
    java.util.List<com.psigama.airlab.audit.AuditEventType> distinctTypesAfter(
            @org.springframework.data.repository.query.Param("position") long position);
}
