package edu.sjsu.cmpe275.aop.aspect;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.aspectj.lang.annotation.Around;

@Aspect
@Order(1)
public class RetryAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */
	int count = 0;
	@Around("execution(public void edu.sjsu.cmpe275.aop.SecretService.createSecret(..))")
	public void dummyAdvice(ProceedingJoinPoint joinPoint) {
		System.out.printf("Retry aspect prior to the executution of %s\n", joinPoint.getSignature().getName());
		Object result = null;
		try {
			result = joinPoint.proceed();
			System.out.printf("Finished the executuion of the method %s with result %s\n", joinPoint.getSignature().getName(), result);
		} catch (Throwable e) {
			if(e instanceof IOException && count < 3){
				count++;
				System.out.println("\n\n*** IOException time - : "+count+" ***\n\n");
			}else{
				count = 0;
				e.printStackTrace();
				System.out.printf("Aborted the executution of  %s\n", joinPoint.getSignature().getName());
			}
		}
	}

}



//
//package edu.sjsu.cmpe275.aop.aspect;
//
//import java.io.IOException;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.core.annotation.Order;
//import org.aspectj.lang.annotation.Around;
//
//@Aspect
//@Order(1)
//public class RetryAspect {
//    /*
//     * Following is a dummy implementation of this aspect.
//     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
//     * @throws IOException 
//     */
//
//	@Around("execution(public * edu.sjsu.cmpe275.aop.SecretService.*(..))")
//	public Object dummyAdvice(ProceedingJoinPoint joinPoint) throws IOException {
//		System.out.printf("Retry aspect prior to the executuion of the metohd %s\n", joinPoint.getSignature().getName());
//		Object result = null;
//		try {
//			result = joinPoint.proceed();
//			System.out.printf("Finished the executuion of the metohd %s with result %s\n", joinPoint.getSignature().getName(), result);
//		} catch (Throwable e) {
//			try {
//				result = joinPoint.proceed();
//				System.out.printf("Finished the executuion of the metohd %s with result %s\n", joinPoint.getSignature().getName(), result);
//			}
//			catch (Throwable e1) {
//				try {
//					result = joinPoint.proceed();
//					System.out.printf("Finished the executuion of the metohd %s with result %s\n", joinPoint.getSignature().getName(), result);
//				}
//				catch (Throwable e2) {
//					throw new IOException(); 
////					System.out.printf("Aborted the executuion of the metohd %s\n", joinPoint.getSignature().getName());
//				}
//			}
////			e.printStackTrace();
////			System.out.printf("Aborted the executuion of the metohd %s\n", joinPoint.getSignature().getName());
//		}
//		return result;
//	}
//
//}