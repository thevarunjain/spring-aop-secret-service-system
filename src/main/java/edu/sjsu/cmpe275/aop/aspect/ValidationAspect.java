package edu.sjsu.cmpe275.aop.aspect;

import java.awt.List;
import java.lang.reflect.Type;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.weaver.ast.Instanceof;
import org.aspectj.weaver.ast.Var;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.core.annotation.Order;

@Aspect
@Order(0)
public class ValidationAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */

	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public void validation(JoinPoint joinPoint) {
		System.out.printf("Validation for %s\n", joinPoint.getSignature().getName());
		Object[] args = joinPoint.getArgs();	
		
		if(joinPoint.getSignature().getName().equals("createSecret") ) {
			Object name = args[0];
			Object secret = args[1];			

				if(name == null) {
					throw new IllegalArgumentException("Name not found");
				}
				if(secret!= null && secret.toString().length()>100){
				throw new IllegalArgumentException("Secret is more than 100 characters");
			}
		}else{
			for(Object w : args) {
				if(w == null) {
					throw new IllegalArgumentException("One or more argument not passed in "+joinPoint.getSignature().getName());
				}
			}
		}
	
	}

}
