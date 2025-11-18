package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.UserEntity;

/**
 * Service contract for profile-related operations.
 */
public interface ProfileService {

    UserEntity save(UserEntity userEntity);

    boolean existsByEmail(String email);

    UserEntity findByEmail(String email);

    void sendVerificationOtp(String email);

    /**
     * Convenience: return the application userId (UUID) for a given email.
     */
    String getLoggedInUserId(String email);
}
