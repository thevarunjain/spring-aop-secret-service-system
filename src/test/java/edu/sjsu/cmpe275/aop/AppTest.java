package edu.sjsu.cmpe275.aop;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;


/*
 * testCase1:  Bob cannot read Alice’s secret, which has not been shared with Bob
 * testCase2:  Alice shares a secret with Bob, and Bob can read it.
 * testCase3:  Alice shares a secret with Bob, and Bob shares Alice’s it with Carl, and Carl can read this secret.
 * testCase4:  Alice shares her secret with Bob; Bob shares Carl’s secret with Alice and encounters UnauthorizedException.
 * testCase5:  Alice shares a secret with Bob, Bob shares it with Carl, Alice unshares it with Carl, and Carl cannot read this secret anymore.
 * testCase6:  Alice shares a secret with Bob and Carl; Carl shares it with Bob, then Alice unshares it with Bob; Bob cannot read this secret anymore.
 * testCase7:  Alice shares a secret with Bob; Bob shares it with Carl, and then unshares it with Carl. Carl can still read this secret.
 * testCase8:  Alice shares a secret with Bob; Carl unshares it with Bob, and encounters UnauthorizedException.
 * testCase9:  Alice shares a secret with Bob; Bob shares it with Carl; Alice unshares it with Bob; Bob shares it with Carl with again, and encounters UnauthorizedException. 
 * testCase10: Alice stores the same secrete object twice, and get two different UUIDs.
 * testCase11: Alice creates a secret having length > 100 and gets IllegalArgumentException exception.
 * testCase12: Alice creates a secret having length = 100 and does not get any exception.
 * testCase13: Alice creates a secret with userId as null and gets IllegalArgumentException exception.
 * testCase14: Alice creates a secret with userId as null and gets IllegalArgumentException exception.
*/

@FunctionalInterface interface FailingRunnable { void run() throws Exception ;}
class MyAssertions{
	  public static void assertDoesNotThrow(FailingRunnable action){
	    try{
	      action.run();
	    }
	    catch(Exception ex){
	      throw new Error("expected action not to throw, but it did!", ex);
	    }
	  }
	}

	
public class AppTest {

    /*
     * These are dummy test cases. You may add test cases based on your own need.
     */

	@Autowired
	SecretService secretService;
	@Autowired
	SecretStats stats;
	
	@Before
	public void setUp() throws Exception {
		System.out.println("---------------------------------------------");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml"); 
		try {
			secretService = (SecretService) ctx.getBean("secretService");
			stats = (SecretStats) ctx.getBean("secretStats");
		} finally {
			ctx.close();
		}
		
	}

	@Test(expected = NotAuthorizedException.class)
    public void testCase1() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase1: Bob cannot read Alice’s secret, which has not been shared with Bob.
		System.out.println("\ntestCase1:");
		System.out.println("---------------------------------------------");
		UUID aliceSecret = secretService.createSecret("Alice", "Alice Secret");
		secretService.readSecret("Bob", aliceSecret);
	}

	@Test
    public void testCase2() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		MyAssertions.assertDoesNotThrow(new FailingRunnable() {
//			@Override
			public void run() throws Exception {
				// testCase2: Alice shares a secret with Bob, and Bob can read it.
				System.out.println("\ntestCase2:");
				System.out.println("---------------------------------------------");
				UUID aliceSecret = secretService.createSecret("Alice", "Alice Secret");
				secretService.shareSecret("Alice", aliceSecret, "Bob");
				secretService.readSecret("Bob", aliceSecret);
			  }
		});
	}
	
	@Test
    public void testCase3() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		MyAssertions.assertDoesNotThrow(new FailingRunnable() {
//			@Override
			public void run() throws Exception {
				// testCase3: Alice shares a secret with Bob, and Bob shares Alice’s id with Carl, and Carl can read this secret.
				System.out.println("\ntestCase3:");
				System.out.println("---------------------------------------------");
				UUID aliceSecret = secretService.createSecret("Alice", "Alice Secret");
				secretService.shareSecret("Alice", aliceSecret, "Bob");
				secretService.shareSecret("Bob", aliceSecret, "Carl");
				secretService.readSecret("Carl", aliceSecret);
			  }
		});
	}
	
	@Test(expected = NotAuthorizedException.class)
    public void testCase4() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase4: Alice shares her secret with Bob; Bob shares Carl’s secret with Alice and encounters UnauthorizedException.
		System.out.println("\ntestCase4:");
		System.out.println("---------------------------------------------");
		UUID aliceSecret = secretService.createSecret("Alice", "Alice Secret");
		secretService.shareSecret("Alice", aliceSecret, "Bob");
		UUID carlSecret = secretService.createSecret("Carl", "Carl Secret");
		secretService.shareSecret("Bob", carlSecret, "Alice");
	  }
	
	@Test(expected = NotAuthorizedException.class)
    public void testCase5() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase5: Alice shares a secret with Bob, Bob shares it with Carl, Alice unshares it with Carl, and Carl cannot read this secret anymore.
		System.out.println("\ntestCase5:");
		System.out.println("---------------------------------------------");
		UUID aliceSecret = secretService.createSecret("Alice", "Alice Secret");
		secretService.shareSecret("Alice", aliceSecret, "Bob");
		secretService.shareSecret("Bob", aliceSecret, "Carl");
		secretService.unshareSecret("Alice", aliceSecret, "Carl");
		secretService.readSecret("Carl", aliceSecret);
	  }
	
	@Test(expected = NotAuthorizedException.class)
    public void testCase6() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase6: Alice shares a secret with Bob and Carl; Carl shares it with Bob, then Alice unshares it with Bob; Bob cannot read this secret anymore.
		System.out.println("\ntestCase6:");
		System.out.println("---------------------------------------------");
		UUID aliceSecret = secretService.createSecret("Alice", "Alice Secret");
		secretService.shareSecret("Alice", aliceSecret, "Bob");
		secretService.shareSecret("Alice", aliceSecret, "Carl");
		secretService.shareSecret("Carl", aliceSecret, "Bob");
		secretService.unshareSecret("Alice", aliceSecret, "Bob");
		secretService.readSecret("Bob", aliceSecret);
	  }
	
	@Test
    public void testCase7() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase7: Alice shares a secret with Bob; Bob shares it with Carl, and then unshares it with Carl. Carl can still read this secret.
		MyAssertions.assertDoesNotThrow(new FailingRunnable() {
//			@Override
			public void run() throws Exception {
				System.out.println("\ntestCase7:");
				System.out.println("---------------------------------------------");
				UUID aliceSecret = secretService.createSecret("Alice", "Alice Secret");
				secretService.shareSecret("Alice", aliceSecret, "Bob");
				secretService.shareSecret("Bob", aliceSecret, "Carl");
				try {
					secretService.unshareSecret("Bob", aliceSecret, "Carl");
				}
				catch(NotAuthorizedException e) {
					System.out.println("Bob does not have access rights to unshare Alice's secret.");
				}
				secretService.readSecret("Carl", aliceSecret);
			  }
		});
	  }
	
	@Test(expected = NotAuthorizedException.class)
    public void testCase8() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase8: Alice shares a secret with Bob; Carl unshares it with Bob, and encounters UnauthorizedException.
		System.out.println("\ntestCase8:");
		System.out.println("---------------------------------------------");
		UUID aliceSecret = secretService.createSecret("Alice", "Alice Secret");
		secretService.shareSecret("Alice", aliceSecret, "Bob");
		secretService.unshareSecret("Carl", aliceSecret, "Bob");
	  }
	
	@Test(expected = NotAuthorizedException.class)
    public void testCase9() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase9: Alice shares a secret with Bob; Bob shares it with Carl; Alice unshares it with Bob; Bob shares it with Carl with again, and encounters UnauthorizedException.
		System.out.println("\ntestCase9:");
		System.out.println("---------------------------------------------");
		UUID aliceSecret = secretService.createSecret("Alice", "Alice Secret");
		secretService.shareSecret("Alice", aliceSecret, "Bob");
		secretService.shareSecret("Bob", aliceSecret, "Carl");
		secretService.unshareSecret("Alice", aliceSecret, "Bob");
		secretService.shareSecret("Bob", aliceSecret, "Carl");
	  }
	
	@Test
    public void testCase10() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase10: Alice stores the same secrete object twice, and get two different UUIDs.
		MyAssertions.assertDoesNotThrow(new FailingRunnable() {
//			@Override
			public void run() throws Exception {
				System.out.println("\ntestCase10:");
				System.out.println("---------------------------------------------");
				UUID aliceSecret1 = secretService.createSecret("Alice", "Alice Secret");
				UUID aliceSecret2 = secretService.createSecret("Alice", "Alice Secret");
				System.out.println(aliceSecret1);
				assert(!aliceSecret1.equals(aliceSecret2));
			  }
		});
	  }
	
	@Test(expected = IllegalArgumentException.class)
    public void testCase11() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase11: Alice creates a secret having length > 100 and gets IllegalArgumentException exception.
		System.out.println("\ntestCase11:");
		System.out.println("---------------------------------------------");
		secretService.createSecret("Alice", "AcRhQ9FQ65eVD1Ss6rt3VCFQN4AXtQygmRty2WoX6dTn21xThsjy1lxKZnhGLIHODRrZO2Gd2gPk0bQ84WAZC3l5z7PqaQMVnE2h3");
	  }
	
	@Test
    public void testCase12() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase12: Alice creates a secret having length = 100 and does not get any exception.
		System.out.println("\ntestCase12:");
		System.out.println("---------------------------------------------");
		secretService.createSecret("Alice", "cRhQ9FQ65eVD1Ss6rt3VCFQN4AXtQygmRty2WoX6dTn21xThsjy1lxKZnhGLIHODRrZO2Gd2gPk0bQ84WAZC3l5z7PqaQMVnE2h3");
	  }
	
	@Test(expected = IllegalArgumentException.class)
    public void testCase13() throws IllegalArgumentException, NotAuthorizedException, IOException { 
		// testCase13: Alice creates a secret with userId as null and gets IllegalArgumentException exception.
		System.out.println("\ntestCase13:");
		System.out.println("---------------------------------------------");
		secretService.createSecret(null, "Null User Secret");

	}
	
	
//	@Test
//    public void testCase14() throws IllegalArgumentException, NotAuthorizedException, IOException
//    {
//    	UUID secret = secretService.createSecret("Alice", "My little secret");
//    	UUID secret2 = secretService.createSecret("Alice", "My little secret is not so little ");
//    	UUID secret3 = secretService.createSecret("Alice", "My little secret is not so little that you can call it little");
//    	UUID secret4 = secretService.createSecret("Carl", "My little secret is not so little that you can call it little");
//    	secretService.shareSecret("Alice", secret, "Bob");
//    	secretService.shareSecret("Alice", secret2, "Bob");
//    	secretService.shareSecret("Alice", secret3, "Bob");
//    	secretService.shareSecret("Alice", secret, "Carl");
//    	secretService.shareSecret("Alice", secret2, "Carl");
//    	secretService.shareSecret("Alice", secret3, "Carl");
//    	secretService.shareSecret("Alice", secret3, "Alice");
//    	secretService.shareSecret("Alice", secret, "Den");
//    	secretService.shareSecret("Bob", secret, "Ed");
//    	secretService.shareSecret("Bob", secret2, "Ed");
//    	secretService.shareSecret("Bob", secret3, "Ed");
//    	secretService.shareSecret("Carl", secret4, "Ed");
//    	secretService.shareSecret("Carl", secret4, "Alice");
//    	secretService.shareSecret("Carl", secret4, "Bob");
//    	secretService.shareSecret("Carl", secret4, "Den");
//    	secretService.readSecret("Alice", secret);
//    	secretService.readSecret("Bob", secret);
//    	secretService.readSecret("Bob", secret);
//    	secretService.readSecret("Bob", secret);
//    	secretService.readSecret("Carl", secret);
//    	secretService.readSecret("Carl", secret);
//    	
//    final String USER_1 = "user1";
//
//    final String USER_2 = "user2";
//
//    final String USER_3 = "user3";
//
//    final String USER_4 = "user4";
//
//    final String USER_1_SECRET = "user1secret";
//
//    final String USER_2_SECRET = "user2secret";
//
//    final String USER_3_SECRET = "user3secret";
//
//    final String USER_4_SECRET = "user4secret";
    

        
//    	test1: Bob cannot read Alice’s secret, which has not been shared with Bob
    	
//	
//      UUID s1 = secretService.createSecret("Alice", "Hello!");
//      secretService.readSecret("Bob", s1);
    	
//  	test2: Alice shares a secret with Bob, and Bob can read it.
    	
      
//      UUID s1 = secretService.createSecret("Alice", "Hello!");
//      secretService.shareSecret("Alice", s1, "Bob");
//      secretService.readSecret("Bob", s1);
    	
//
////    	test3: Alice shares a secret with Bob, and Bob shares Alice’s it with Carl, and Carl can read this secret.
//      
//      UUID s1 = secretService.createSecret("Alice", "Hello!");
//      secretService.shareSecret("Alice", s1, "Bob");
//      secretService.shareSecret("Bob", s1, "Carl");
//      secretService.readSecret("Carl", s1);
//      
//      
////    	test4: Alice shares a secret with Bob, Bob shares it with Carl, Alice unshares it with Carl, and Carl cannot read this secret anymore.
//      
//      UUID s1 = secretService.createSecret("Alice", "Hello!");
//      secretService.shareSecret("Alice", s1, "Bob");
//      secretService.shareSecret("Bob", s1, "Carl");
//      secretService.unshareSecret("Alice", s1, "Carl");
//      secretService.readSecret("Carl", s1);

		
////    	testJ: Alice stores the same secrete object twice, and get two different UUIDs.
//
//      UUID s1 = secretService.createSecret("Alice", "World");
//      UUID s2 = secretService.createSecret("Alice", "World");

//    }
}