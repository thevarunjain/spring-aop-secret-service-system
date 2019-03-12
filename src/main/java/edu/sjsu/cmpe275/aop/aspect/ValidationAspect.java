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
@Order(1)
public class ValidationAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */

	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public void validation(JoinPoint joinPoint) {
		System.out.printf("Validation for %s\n", joinPoint.getSignature().getName());
		Object[] args = joinPoint.getArgs();	
		
		if(joinPoint.getSignature().getName() == "createSecret") {
			Object name = args[0];
			Object secret = args[1];			

				if(name == null || name.toString().length()<=0) {
					throw new IllegalArgumentException("Name not found");
				}
			if(secret.toString().length()==0) {
				throw new IllegalArgumentException("Secret not found");
			}else if(secret.toString().length()>100){
				throw new IllegalArgumentException("Secret too long");
			}
		}else{
			for(Object w : args) {
			System.err.println("object >>>"+w);
				if(w == null || w.toString().length() == 0) {
					throw new IllegalArgumentException("One or more argument not passed in "+joinPoint.getSignature().getName());
				}
			}
		}
	
	}

}
