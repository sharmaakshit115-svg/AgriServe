package com.agriserve.util;

import com.agriserve.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility for extracting the currently authenticated user's principal from the SecurityContext.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Returns the UserPrincipal for the currently authenticated user.
     *
     * @throws IllegalStateException if no authenticated user is present
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user in the security context");
        }
        return (UserPrincipal) authentication.getPrincipal();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }
}
