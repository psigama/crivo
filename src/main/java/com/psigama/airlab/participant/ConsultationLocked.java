package com.psigama.airlab.participant;

/**
 * Domain event: a P1 consultation request was locked and its commitment published. Carries the
 * pseudonymous request code only.
 */
public record ConsultationLocked(String code) {
}
