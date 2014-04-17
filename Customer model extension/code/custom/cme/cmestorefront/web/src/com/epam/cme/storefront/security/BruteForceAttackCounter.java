package com.epam.cme.storefront.security;

/**
 * Interface for checking brute force attack attempts.
 */
public interface BruteForceAttackCounter {
    /**
     * Method registers user login failure.
     * 
     * @param userUid
     *            that the failure is registered for
     */
    void registerLoginFailure(final String userUid);

    /**
     * Method checks if user reached attack threshold.
     * 
     * @param userUid
     *            user uid against which the check is performed
     * @return true if this one is an attack
     */
    boolean isAttack(final String userUid);

    /**
     * Method resets the counter for the given user uid
     * 
     * @param userUid
     *            user uid that failed logins counter will be reset
     */
    void resetUserCounter(final String userUid);

    /**
     * Method returns current user failed login counter value.
     * 
     * @param userUid
     *            user uid to return failed login number
     * @return the number of failed logins for the user
     */
    int getUserFailedLogins(final String userUid);
}
