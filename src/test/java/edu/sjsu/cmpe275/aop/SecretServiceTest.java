//package edu.sjsu.cmpe275.aop;
//
//import org.junit.Test;
//
//public class SecretServiceTest {
//
//	
//
//    @Test
//    public void testOne() {
//    	
//    }
//
//    @Test
//    public void testCaseN() { }
//}
package edu.sjsu.cmpe275.aop;

import javafx.collections.transformation.SortedList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/context.xml")
public class SecretServiceTest {

    @Autowired
    private SecretService secretService;

    @Autowired
    private SecretStats secretStats;

    private static final String USER_1 = "user1";

    private static final String USER_2 = "user2";

    private static final String USER_3 = "user3";

    private static final String USER_4 = "user4";

    private static final String USER_1_SECRET = "user1secret";

    private static final String USER_2_SECRET = "user2secret";

    private static final String USER_3_SECRET = "user3secret";

    private static final String USER_4_SECRET = "user4secret";


    @Before
    public void cleanup(){
        secretStats.resetStatsAndSystem();
    }

    @Test
    public void getLengthOfLongestSecret_withoutAnySecrets_shouldReturnZero(){
        assertThat(
                secretStats.getLengthOfLongestSecret(),
                is(0)
        );
    }

    @Test
    public void getLengthOfLongestSecret_withSecrets_shouldReturnMaxLength() throws IOException {
        final String maxLengthSecret = "maxLengthSecret";
        //USER 1 creates two different secrets (One with max length)
        secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.createSecret(USER_1, maxLengthSecret);
        //USER 2 creates same secret twice
        secretService.createSecret(USER_2, USER_2_SECRET);
        secretService.createSecret(USER_2, USER_2_SECRET);

        assertThat(
                secretStats.getLengthOfLongestSecret(),
                is(maxLengthSecret.length())
        );
    }

    @Test
    public void getLengthOfLongestSecret_withSecrets_shouldReturnZeroAfterResetStatsAndSystem() throws IOException {
        secretService.createSecret(USER_1, USER_1_SECRET);
        secretStats.resetStatsAndSystem();
        assertThat(
                secretStats.getLengthOfLongestSecret(),
                is(0)
        );
    }

    @Test
    public void getMostTrustedUser_withNoUsers_shouldReturnNull() {
        assertThat(
                secretStats.getMostTrustedUser(),
                is(nullValue())
        );

    }

    @Test
    public void getMostTrustedUser_withNoUserSharedAnySecret_shouldReturnNull() throws IOException {
        //USER 1 creates same secret twice
        secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.createSecret(USER_1, USER_1_SECRET);
        //USER 2 creates same secret twice
        secretService.createSecret(USER_2, USER_2_SECRET);
        secretService.createSecret(USER_2, USER_2_SECRET);
        assertThat(
                secretStats.getMostTrustedUser(),
                is(nullValue())
        );

    }

    /**
     * 1. User1, creates a secret and shares it with User2, User4
     * 2. User2, creates a secret and shares it with User1, User3, User4
     * 3. User3, creates a secret and shares it with User4.
     * 4. User4, creates a secret and shares it with User1, User2
     * <p>
     *         sharedWithUser    sharedByUser
     * User1        2                   2
     * User2        2                   3
     * User3        1                   1
     * User4        3                   2
     * <p>
     * Most trusted user : max no of shared with user -> user4
     * <p>
     */
    @Test
    public void getMostTrustedUser_withUsersSharedSomeSecrets_shouldReturnMostTrustedUser() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secretService.shareSecret(USER_1, secret, USER_4);

        secret = secretService.createSecret(USER_2, USER_2_SECRET);
        secretService.shareSecret(USER_2, secret, USER_1);
        secretService.shareSecret(USER_2, secret, USER_3);
        secretService.shareSecret(USER_2, secret, USER_4);

        secret = secretService.createSecret(USER_3, USER_3_SECRET);
        secretService.shareSecret(USER_3, secret, USER_4);

        secret = secretService.createSecret(USER_4, USER_4_SECRET);
        secretService.shareSecret(USER_4, secret, USER_1);
        secretService.shareSecret(USER_4, secret, USER_2);

        assertThat(
                secretStats.getMostTrustedUser(),
                is(USER_4)
        );

    }

    @Test
    public void getMostTrustedUser_withUsersUnsharingSomeSecrets_shouldNotAffectStats() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secretService.unshareSecret(USER_1, secret, USER_2);

        assertThat(
                secretStats.getMostTrustedUser(),
                is(USER_2)
        );
    }

    @Test
    public void getMostTrustedUser_ifMultipleUserSharesSameSecretToSameUser_shouldConsiderDifferentSharing() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secretService.shareSecret(USER_1, secret, USER_3);
        secretService.shareSecret(USER_2, secret, USER_3);

        assertThat(
                secretStats.getMostTrustedUser(),
                is(USER_3)
        );
    }

    @Test
    public void getMostTrustedUser_withUserSharingSecretWithItSelf_shouldNotAffectStats() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_1);
        secretService.shareSecret(USER_1, secret, USER_2);

        assertThat(
                secretStats.getMostTrustedUser(),
                is(USER_2)
        );
    }

    @Test
    public void getMostTrustedUser_ifTieForMostTrustedUser_returnUserBasedOnAlphabeticalOrder() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secretService.shareSecret(USER_1, secret, USER_3);

        assertThat(
                secretStats.getMostTrustedUser(),
                is(USER_2)
        );
    }

    @Test
    public void getWorstSecretKeeper_withNoUsers_shouldReturnNull() {
        assertThat(
                secretStats.getWorstSecretKeeper(),
                is(nullValue())
        );

    }

    @Test
    public void getWorstSecretKeeper_withNoUserSharedAnySecret_shouldReturnNull() throws IOException {
        //USER 1 creates same secret twice
        secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.createSecret(USER_1, USER_1_SECRET);
        //USER 2 creates same secret twice
        secretService.createSecret(USER_2, USER_2_SECRET);
        secretService.createSecret(USER_2, USER_2_SECRET);
        assertThat(
                secretStats.getWorstSecretKeeper(),
                is(nullValue())
        );

    }

    /**
     * 1. User1, creates a secret and shares it with User2, User4
     * 2. User2, creates a secret and shares it with User1, User3, User4
     * 3. User3, creates a secret and shares it with User4.
     * 4. User4, creates a secret and shares it with User1, User2
     * <p>
     *         sharedWithUser    sharedByUser
     * User1        2                   2
     * User2        2                   3
     * User3        1                   1
     * User4        3                   2
     * <p>
     * Worst Secret Keeper : net sharing balance min(With - By) -> user2
     * <p>
     */
    @Test
    public void getWorstSecretKeeper_withUsersSharedSomeSecrets_shouldReturnWorstSecretKeeper() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secretService.shareSecret(USER_1, secret, USER_4);

        secret = secretService.createSecret(USER_2, USER_2_SECRET);
        secretService.shareSecret(USER_2, secret, USER_1);
        secretService.shareSecret(USER_2, secret, USER_3);
        secretService.shareSecret(USER_2, secret, USER_4);

        secret = secretService.createSecret(USER_3, USER_3_SECRET);
        secretService.shareSecret(USER_3, secret, USER_4);

        secret = secretService.createSecret(USER_4, USER_4_SECRET);
        secretService.shareSecret(USER_4, secret, USER_1);
        secretService.shareSecret(USER_4, secret, USER_2);

        assertThat(
               secretStats.getWorstSecretKeeper(),
                is(USER_2)
        );
    }

    @Test
    public void getWorstSecretKeeper_withUsersUnsharingSomeSecrets_shouldNotAffectStats() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secretService.unshareSecret(USER_1, secret, USER_2);

        assertThat(
                secretStats.getWorstSecretKeeper(),
                is(USER_1)
        );
    }

    @Test
    public void getWorstSecretKeeper_ifMultipleUserSharesSameSecretToSameUser_shouldConsiderDifferentSharing() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secretService.shareSecret(USER_1, secret, USER_3);
        secretService.shareSecret(USER_2, secret, USER_3);

        assertThat(
                secretStats.getWorstSecretKeeper(),
                is(USER_1)
        );
    }

    @Test
    public void getWorstSecretKeeper_withUserSharingSecretWithItSelf_shouldNotAffectStats() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secretService.shareSecret(USER_2, secret, USER_2);

        assertThat(
                secretStats.getWorstSecretKeeper(),
                is(USER_1)
        );
    }

    @Test
    public void getWorstSecretKeeper_ifTieForWorstSecretKeeper_returnUserBasedOnAlphabeticalOrder() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secret = secretService.createSecret(USER_2, USER_2_SECRET);
        secretService.shareSecret(USER_2, secret, USER_1);

        assertThat(
                secretStats.getWorstSecretKeeper(),
                is(USER_1)
        );
    }

    @Test
    public void getWorstSecretKeeper_ifNoNegativeNetSharingBalance_returnUserWithZeroBalance() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);

        assertThat(
                secretStats.getWorstSecretKeeper(),
                is(USER_1)
        );
    }

    @Test
    public void getWorstSecretKeeper_ifNoNegativeNetSharingBalanceWithTie_returnUserWithZeroBalanceAlphabetically() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secret = secretService.createSecret(USER_3, USER_3_SECRET);

        assertThat(
                secretStats.getWorstSecretKeeper(),
                is(USER_1)
        );
    }

    @Test
    public void getBestKnownSecret_withSharing_returnsContentOfBestKnownSecret() throws IOException {
        UUID secret = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret, USER_2);
        secretService.shareSecret(USER_1, secret, USER_3);
        secretService.readSecret(USER_2, secret);
        secretService.readSecret(USER_3, secret);

        assertThat(
                secretStats.getBestKnownSecret(),
                is(USER_1_SECRET)
        );
    }

    @Test
    public void getBestKnownSecret_withMultipleReadsBySameUser_shouldOnlyConsiderOne() throws IOException {
        UUID secret1 = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret1, USER_2);
        secretService.shareSecret(USER_2, secret1, USER_3);
        secretService.shareSecret(USER_2, secret1, USER_4);
        UUID secret2 = secretService.createSecret(USER_2, USER_2_SECRET);
        secretService.shareSecret(USER_2, secret2, USER_4);
        secretService.shareSecret(USER_4, secret2, USER_1);
        secretService.readSecret(USER_3, secret1);
        secretService.readSecret(USER_3, secret1);
        secretService.readSecret(USER_1, secret2);
        secretService.readSecret(USER_4, secret2);

        assertThat(
                secretStats.getBestKnownSecret(),
                is(USER_2_SECRET)
        );
    }

    @Test
    public void getBestKnownSecret_ifTie_returnSecretContentAlphabetically() throws IOException {
        UUID secret1 = secretService.createSecret(USER_1, USER_1_SECRET);
        secretService.shareSecret(USER_1, secret1, USER_2);
        secretService.shareSecret(USER_2, secret1, USER_3);
        secretService.shareSecret(USER_2, secret1, USER_4);
        UUID secret2 = secretService.createSecret(USER_2, USER_2_SECRET);
        secretService.shareSecret(USER_2, secret2, USER_4);
        secretService.shareSecret(USER_4, secret2, USER_1);
        secretService.readSecret(USER_3, secret1);
        secretService.readSecret(USER_3, secret1);
        secretService.readSecret(USER_1, secret2);
        secretService.readSecret(USER_4, secret2);
        secretService.readSecret(USER_4, secret1);

        assertThat(
                secretStats.getBestKnownSecret(),
                is(USER_1_SECRET)
        );
    }

    @Test
    public void validationForIOException_ifLessThanTwoRetries_shouldExecuteProperly() throws IOException {
        // createSecret specifically throws IOException for user3 2 times
        UUID secret = secretService.createSecret(USER_3, USER_3_SECRET);
        assertThat(
                secretStats.getBestKnownSecret(),
                is(nullValue())
        );
        assertThat(
                secretStats.getLengthOfLongestSecret(),
                is(USER_3_SECRET.length())
        );
    }

    @Test(expected = IOException.class)
    public void validationForIOException_ifMoreThanTwoRetries_throwsIOException() throws IOException {
        // createSecret specifically throws IOException for user4 3 times
        UUID secret = secretService.createSecret(USER_4, USER_4_SECRET);
    }

}
