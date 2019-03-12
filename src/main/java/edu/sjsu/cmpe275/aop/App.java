package edu.sjsu.cmpe275.aop;



//import static org.junit.Assert.assertEquals;

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
        
        try {
    		String alice = "alice";
    		String doug = "doug";
    		String mango = "mango";
    		String mangoo = "mangoo";
    		
    		String s1 = "s1";
    		String s2 = "s2";
    		String s3 = "s3";
    		String s4 = "s4";
    		String s5 = "s5";
    		String s6 = "s6";
    		String s7 = "s7";
    		
    	    UUID s1Id = secretService.createSecret(mango, s1) ;
    	    UUID s2Id = secretService.createSecret(mango, s2) ;
    	    UUID s3Id = secretService.createSecret(alice, s3) ;
    	    UUID s4Id = secretService.createSecret(doug, s4) ;
    	    UUID s5Id = secretService.createSecret(doug, s5) ;
    	    UUID s6Id = secretService.createSecret(mangoo, s6) ;
    	    UUID s7Id = secretService.createSecret(mangoo, s7) ;
    	    
    	    secretService.shareSecret(mango, s1Id, mangoo);
    	    secretService.shareSecret(mango, s2Id, mangoo);
    	    secretService.shareSecret(mangoo, s6Id, alice);
    	    secretService.shareSecret(mangoo, s7Id, mango);
    	    secretService.shareSecret(doug, s4Id, mango);
    	    secretService.shareSecret(doug, s4Id, mangoo);
    	    secretService.shareSecret(alice, s3Id, alice);
    	    secretService.shareSecret(alice, s3Id, doug);
    	    secretService.shareSecret(alice, s3Id, doug);
    	    
    	    System.out.println("Most trusted user: " + stats.getMostTrustedUser());
    	    System.out.println("Worst secret keeper: " + stats.getWorstSecretKeeper());
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
