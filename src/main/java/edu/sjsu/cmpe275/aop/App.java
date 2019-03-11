package edu.sjsu.cmpe275.aop;

import java.util.UUID;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {
        /***
         * Following is a dummy implementation of App to demonstrate bean creation with Application context.
         * You may make changes to suit your need, but this file is NOT part of your submission.
         */
    
    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
        SecretService secretService = (SecretService) ctx.getBean("secretService");
        SecretStats stats = (SecretStats) ctx.getBean("secretStats");

        final String USER_1 = "user1";

        final String USER_2 = "user2";

        final String USER_3 = "user3";

        final String USER_4 = "user4";

        final String USER_1_SECRET = "user1secret";

        final String USER_2_SECRET = "user2secret";

        final String USER_3_SECRET = "user3secret";

        final String USER_4_SECRET = "user4secret";
        
        try {
//        	UUID secret = secretService.createSecret("Alice", "My little secret");
//        	UUID secret2 = secretService.createSecret("Alice", "My little secret is not so little ");
//        	UUID secret3 = secretService.createSecret("Alice", "My little secret is not so little that you can call it little");
//        	UUID secret4 = secretService.createSecret("Carl", "My little secret is not so little that you can call it little");
//        	secretService.shareSecret("Alice", secret, "Bob");
//        	secretService.shareSecret("Alice", secret2, "Bob");
//        	secretService.shareSecret("Alice", secret3, "Bob");
//        	secretService.shareSecret("Alice", secret, "Carl");
//        	secretService.shareSecret("Alice", secret2, "Carl");
//        	secretService.shareSecret("Alice", secret3, "Carl");
//        	secretService.shareSecret("Alice", secret3, "Alice");
//        	secretService.shareSecret("Alice", secret, "Den");
//        	secretService.shareSecret("Bob", secret, "Ed");
//        	secretService.shareSecret("Bob", secret2, "Ed");
//        	secretService.shareSecret("Bob", secret3, "Ed");
//        	secretService.shareSecret("Carl", secret4, "Ed");
//        	secretService.shareSecret("Carl", secret4, "Alice");
//        	secretService.shareSecret("Carl", secret4, "Bob");
//        	secretService.shareSecret("Carl", secret4, "Den");
//        	secretService.readSecret("Alice", secret);
//        	secretService.readSecret("Bob", secret);
//        	secretService.readSecret("Bob", secret);
//        	secretService.readSecret("Bob", secret);
//        	secretService.readSecret("Carl", secret);
//        	secretService.readSecret("Carl", secret);
        	
        	 secretService.createSecret(USER_1, USER_1_SECRET);
             secretService.createSecret(USER_1, USER_1_SECRET);
             //USER 2 creates same secret twice
             secretService.createSecret(USER_2, USER_2_SECRET);
             secretService.createSecret(USER_2, USER_2_SECRET);
            
     
        	
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Longest Secret: " + stats.getLengthOfLongestSecret());
        System.out.println("Best known secret: " + stats.getBestKnownSecret());
        System.out.println("Worst secret keeper: " + stats.getWorstSecretKeeper());
        System.out.println("Most trusted user: " + stats.getMostTrustedUser());
        stats.resetStatsAndSystem();
        ctx.close();
    }
}
