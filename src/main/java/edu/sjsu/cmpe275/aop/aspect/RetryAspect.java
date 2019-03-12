package edu.sjsu.cmpe275.aop.aspect;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.aspectj.lang.annotation.Around;

@Aspect
@Order(2)
public class RetryAspect {
	/*
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */
	static int count = 0;
	@Around("execution(public * edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public Object dummyAdvice(ProceedingJoinPoint joinPoint) throws Throwable{
		System.out.printf("Retry aspect prior to the executuion of the metohd %s\n", joinPoint.getSignature().getName());
		Object result = null;
		count = 0;
		while(count<3)
		{
			try {
				count++;
				result = joinPoint.proceed();
				System.out.printf("Finished the executuion of the metohd %s with result %s\n", joinPoint.getSignature().getName(), result);
				return result;
			} catch (Throwable e) {
				if(e instanceof IOException && count == 3)
				{
					
					System.out.printf("Aborted the executuion of the metohd %s\n", joinPoint.getSignature().getName());
					throw e;
				}
			}
		}
		throw new java.io.IOException();
	}

}