package com.agriserve.entity.enums;

/**
 * Distinguishes between the two metric categories stored in SatisfactionMetric:
 *
 * OFFICER_PERFORMANCE  → derived from Feedback.rating on AdvisorySession
 * PROGRAM_SATISFACTION → derived from Participation.workshopRating on Workshop
 *
 * These must NEVER be mixed in the same aggregation query.
 */
public enum MetricType {
    OFFICER_PERFORMANCE,
    PROGRAM_SATISFACTION,
    WORKSHOP_SATISFACTION
}
