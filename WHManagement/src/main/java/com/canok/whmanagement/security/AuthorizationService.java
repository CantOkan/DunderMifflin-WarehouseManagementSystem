package com.canok.whmanagement.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public void verifyClientOwnershipOrEmployee(Long clientId) {
        AuthenticatedUser user = getAuthenticatedUser();
        if (user.getAccountType() == AccountType.EMPLOYEE) {
            return;
        }

        if (!clientId.equals(user.getAccountId())) {
            throw new AccessDeniedException("You are not allowed to access this client resource.");
        }
    }

    public void verifyEmployeeAccess() {
        AuthenticatedUser user = getAuthenticatedUser();
        if (user.getAccountType() != AccountType.EMPLOYEE) {
            throw new AccessDeniedException("Only employees can access this resource.");
        }
    }

    private AuthenticatedUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser)) {
            throw new AccessDeniedException("Authentication is required.");
        }
        return (AuthenticatedUser) authentication.getPrincipal();
    }
}
