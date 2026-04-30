package com.agriserve.security;

import com.agriserve.repository.AdvisorySessionRepository;
import com.agriserve.repository.FarmerRepository;
import com.agriserve.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Security helper bean registered as "securityService" in the Spring context.
 *
 * Used in {@code @PreAuthorize} SpEL expressions to perform fine-grained
 * ownership and assignment checks, for example:
 *
 * <pre>
 *   @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#farmerId)")
 *   @PreAuthorize("hasRole('ADMIN') or @securityService.isAssignedOfficer(#sessionId)")
 *   @PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER') or @securityService.canAccessSession(#sessionId)")
 * </pre>
 *
 * All methods are designed to be exception-safe (return {@code false} on any
 * error) so they never interrupt the security filter chain with an unexpected
 * exception.
 */
@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final FarmerRepository farmerRepository;
    private final AdvisorySessionRepository sessionRepository;

    // ─── Farmer ownership ────────────────────────────────────────────────────

    /**
     * Returns {@code true} when the currently logged-in user is the FARMER who
     * owns the profile identified by {@code farmerId}.
     *
     * <p>Ownership is determined by comparing the authenticated user's ID with
     * the {@code user_id} stored on the {@link com.agriserve.entity.Farmer}
     * record.</p>
     *
     * @param farmerId the Farmer primary-key to check ownership against
     */
    public boolean isOwner(Long farmerId) {
        try {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            return farmerRepository.findById(farmerId)
                    .map(farmer -> farmer.getUser() != null
                            && farmer.getUser().getUserId().equals(currentUserId))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Convenience alias – returns {@code true} when the currently logged-in
     * user is the Farmer linked to the advisory session whose farmer PK is
     * {@code sessionFarmerId}.
     *
     * @param sessionFarmerId the farmerId stored on the AdvisorySession
     */
    public boolean isSessionOwner(Long sessionFarmerId) {
        return isOwner(sessionFarmerId);
    }

    // ─── General helpers ─────────────────────────────────────────────────────

    /**
     * Returns the user-ID of the currently authenticated user.
     *
     * <p>Exposed so that SpEL expressions in {@code @PreAuthorize} can compare
     * a path-variable directly against the current principal without needing a
     * static utility call:</p>
     * <pre>
     *   @PreAuthorize("(hasRole('EXTENSION_OFFICER') and #officerId == @securityService.getCurrentUserId())")
     * </pre>
     */
    public Long getCurrentUserId() {
        try {
            return SecurityUtils.getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }

    // ─── Session-level checks ────────────────────────────────────────────────

    /**
     * Returns {@code true} when the currently logged-in user is the Extension
     * Officer <em>assigned</em> to the advisory session identified by
     * {@code sessionId}.
     *
     * <p>This prevents an officer from modifying or viewing sessions that were
     * assigned to a different officer.</p>
     *
     * @param sessionId the AdvisorySession primary-key
     */
    public boolean isAssignedOfficer(Long sessionId) {
        try {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            return sessionRepository.findById(sessionId)
                    .map(session -> session.getOfficer() != null
                            && session.getOfficer().getUserId().equals(currentUserId))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns {@code true} when the currently logged-in user is either:
     * <ul>
     *   <li>the FARMER who owns this session, or</li>
     *   <li>the EXTENSION_OFFICER who is assigned to this session.</li>
     * </ul>
     *
     * <p>Used on the "Get Session by ID" endpoint so that both parties can fetch
     * their own session details without granting blanket read access to all
     * sessions.</p>
     *
     * @param sessionId the AdvisorySession primary-key
     */
    public boolean canAccessSession(Long sessionId) {
        try {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            return sessionRepository.findById(sessionId)
                    .map(session -> {
                        // Check if current user is the assigned officer
                        boolean isOfficer = session.getOfficer() != null
                                && session.getOfficer().getUserId().equals(currentUserId);
                        // Check if current user is the farmer who owns the session
                        boolean isFarmer = session.getFarmer() != null
                                && session.getFarmer().getUser() != null
                                && session.getFarmer().getUser().getUserId().equals(currentUserId);
                        return isOfficer || isFarmer;
                    })
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
}
