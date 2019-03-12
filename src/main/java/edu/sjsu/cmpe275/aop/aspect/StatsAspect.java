package edu.sjsu.cmpe275.aop.aspect;

import java.lang.Thread.State;
import java.lang.annotation.Target;
import java.security.AccessController;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLEngineResult.Status;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.org.eclipse.jdt.internal.core.dom.rewrite.NodeInfoStore.StringPlaceholderData;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.NotAuthorizedException;
import edu.sjsu.cmpe275.aop.SecretStatsImpl;
import edu.sjsu.cmpe275.aop.aspect.AccessControlAspect;;

@Aspect
@Order(0)
public class StatsAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */

		@Autowired SecretStatsImpl stats;
//		Map<UUID, ArrayList> accessController = new HashMap<UUID, ArrayList>();
//		Map<UUID, ArrayList> readCountSecret = new HashMap<UUID, ArrayList>();
	
		
		
		@AfterReturning(pointcut ="execution(public java.util.UUID edu.sjsu.cmpe275.aop.SecretService.createSecret(..))", returning ="retVal")
		public void putTempOwner(JoinPoint joinPoint, UUID retVal){
			System.out.println("**Secret Creation**");
			Object [] args = joinPoint.getArgs();
			String tempOwner = args[0].toString();
			String tempSecret= args[1].toString();
			stats.generateLength((String)args[1]);			// message 
			stats.creatingSecret(retVal,tempOwner, tempSecret);
		}	
	
		
		@AfterReturning(pointcut = "execution(public void edu.sjsu.cmpe275.aop.SecretService.shareSecret(..))",  returning = "retVal")
//		@After("execution(public void edu.sjsu.cmpe275.aop.SecretService.shareSecret(..))")
		public void addingUserInAuthorizedList(JoinPoint joinPoint, Object retVal) {
			System.out.println("**Secret Sharing**");
			Object[] args = joinPoint.getArgs();
			String userId= args[0].toString();
			UUID secretId = (UUID) args[1];
			String targetId = args[2].toString();
			stats.sharingSecret(userId,secretId,targetId);
		}
		
		@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..))",  returning = "retVal")
//		@After("execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..))")
		public void validatingUserBeforeReading(JoinPoint joinPoint, Object retVal) {
//			public void validatingUserBeforeReading(JoinPoint joinPoint) {
			System.out.println("**Secret Reading**");
			Object[] args = joinPoint.getArgs();
			String userId= args[0].toString();
			UUID secretId= (UUID) args[1];
			System.out.println("Calling reading secreted from before"+ userId + secretId);
			stats.readingSecret(userId,secretId);
		}
		
		
		@AfterReturning(pointcut = "execution(public void edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))", returning = "retVal")
//		@After("execution(public void edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
		public void removingUserFromAuthorizedList(JoinPoint joinPoint, Object retVal) {
			System.out.println("**Secret Unsharing**");
			Object[] args = joinPoint.getArgs();
			
			UUID secretId= (UUID) args[1];
			String targetId = args[2].toString();
				
			stats.unsharingSecret(secretId,targetId);
		}
}
