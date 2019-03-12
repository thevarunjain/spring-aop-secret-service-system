package edu.sjsu.cmpe275.aop.aspect;
import edu.sjsu.cmpe275.aop.aspect.StatsAspect;
import edu.sjsu.cmpe275.aop.SecretStatsImpl;
import java.awt.List;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.*;
import javafx.util.Pair;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.weaver.ast.And;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.NotAuthorizedException;

@Aspect
@Order(1)
public class AccessControlAspect {
    private static final String[] String = null;
	/***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */
    	@Autowired SecretStatsImpl stats;
	
		
		@Before("execution(public void edu.sjsu.cmpe275.aop.SecretService.shareSecret(..))")
		public void checkAuthorizedUserBeforeSharing  (JoinPoint joinPoint) {
			System.out.printf("Access control for -> %s\n", joinPoint.getSignature().getName());
			Object[] args = joinPoint.getArgs();
			String userId= args[0].toString();
			UUID secretId= (UUID) args[1];
			
			stats.authorizeShareSecret(userId, secretId);
		}
		
		
		@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..))")
		public void validatingUserBeforeReading(JoinPoint joinPoint) {
			System.out.printf("Access control for -> %s\n", joinPoint.getSignature().getName());
			Object[] args = joinPoint.getArgs();
			String userId= args[0].toString();
			UUID secretId= (UUID) args[1];
			System.out.println("Calling authorize secrte from before"+ userId + secretId);
			stats.authorizeReadSecret(userId, secretId);	
		}
		
		
		@Before("execution(public void edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
		public void validatingBeforeRemovingUserFromAuthorizedList(JoinPoint joinPoint) {
			System.out.printf("Access control for -> %s\n", joinPoint.getSignature().getName());
			Object[] args = joinPoint.getArgs();
			
			String userId= args[0].toString();
			UUID secretId= (UUID) args[1];
			String targetId = args[2].toString();
			
			stats.authorizeUnshareSecret(userId, secretId, targetId);	
		}	
}
