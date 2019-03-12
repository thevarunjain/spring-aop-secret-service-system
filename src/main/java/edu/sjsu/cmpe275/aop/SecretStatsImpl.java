package edu.sjsu.cmpe275.aop;
import edu.sjsu.cmpe275.aop.SecretServiceImpl;
import edu.sjsu.cmpe275.aop.aspect.StatsAspect;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.aspectj.org.eclipse.jdt.internal.core.JavaModelManager.EclipsePreferencesListener;
//import edu.sjsu.cmpe275.aop.SecretServiceImpl;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.IfPointcut.IfFalsePointcut;
import org.aspectj.weaver.tools.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class SecretStatsImpl implements SecretStats {
    /***
     * Following is a dummy implementation.
     * You are expected to provide an actual implementation based on the requirements.
     */

	
	       Map<UUID, ArrayList> accessController = new HashMap<UUID, ArrayList>();
		   Map<UUID, ArrayList> readCountSecret = new HashMap<UUID, ArrayList>();
		   Map<String, HashSet> trustedUser = new HashMap<String, HashSet>();
		   Map<String, HashSet> worstKeeper = new HashMap<String, HashSet>();
		   Map<UUID, String> allSecrets = new HashMap<UUID, String>(); 
		   
	
	int longestSecretLength = 0;	
	
@Override
	public void resetStatsAndSystem() {
		// TODO Auto-generated method stub
		longestSecretLength = 0;
		accessController.clear();
		readCountSecret.clear();
		trustedUser.clear();
		worstKeeper.clear();
		allSecrets.clear();
		System.out.println("System Resest Done Successfully");
	}

@Override
	public int getLengthOfLongestSecret() {
		return longestSecretLength;
	}

@Override
	public String getMostTrustedUser() {
		// TODO Auto-generated method stub
		int maxShared = -1;
		String userID = null;
		for(Map.Entry<String, HashSet> entry : trustedUser.entrySet()) {
			System.out.println(entry.getValue().size()+"ssssssssssssssssssssssssssssssssssssssss"+entry.getKey());
				if(entry.getValue().size()>maxShared) {
					maxShared = entry.getValue().size();
					userID = entry.getKey();
				}else if (entry.getValue().size()==maxShared) {
					if(userID.compareToIgnoreCase(entry.getKey()) < 0){
						userID = userID;
					}else if(userID.compareToIgnoreCase(entry.getKey()) > 0) {
						userID = entry.getKey();
					}
				}
		}
		
		System.err.println(trustedUser);
		System.err.println(accessController);
		return userID;
	}

@Override
	public String getWorstSecretKeeper() {
		int minShared = Integer.MAX_VALUE;
		String userId =null;
		for(Map.Entry<String, HashSet> entry : worstKeeper.entrySet()) {
				int countOut  = entry.getValue().size();
				int countIn = 0;
				if(trustedUser.containsKey(entry.getKey())) {
					countIn = trustedUser.get(entry.getKey()).size();
				}
				int sharingScore = countIn- countOut;
				if(sharingScore<minShared) {
					minShared = sharingScore;
					userId = entry.getKey();
				}else if(sharingScore == minShared) {
					if(userId.compareToIgnoreCase(entry.getKey()) < 0) {
						userId = userId;
					}else if(userId.compareToIgnoreCase(entry.getKey()) > 0) {
						userId = entry.getKey();
					}
				}
		}	
		return userId;
	}

@Override
	public String getBestKnownSecret() {
		// TODO Auto-generated method stub
		int maxReads = -1;
		UUID secret = null;
		for(Map.Entry<UUID, ArrayList> entry : readCountSecret.entrySet()) {
				if(entry.getValue().size()>maxReads){
					maxReads = entry.getValue().size();
					secret = entry.getKey();
				}else if(entry.getValue().size()==maxReads && allSecrets.get(entry.getKey())!=null){				// when tie and is not tied with null key
						if(allSecrets.get(secret).compareToIgnoreCase(allSecrets.get(entry.getKey())) > 0 ){
						secret = entry.getKey();
					}
						
				}
		}
		return allSecrets.get(secret);
	}

	public void generateLength(String message) {
		// TODO Auto-generated method stub
		if(message == null) {
			int nullLength = 0;
			if(longestSecretLength>nullLength) {
				longestSecretLength = nullLength;
			}
		}else if(message.length()>longestSecretLength) {
			longestSecretLength=message.length();
		}
	}
	
	public void authorizeShareSecret(String userId, UUID secretId) throws NotAuthorizedException {
		if(accessController.get(secretId)!=null){
//			ArrayList allowedUser = accessController.get(secretId);
				if(accessController.get(secretId).indexOf(userId) == -1){					//if user is in authorized user list,
					throw new NotAuthorizedException(userId+"is not authorized to share the secret");
				}
		}else {
			throw new NotAuthorizedException("No such secret found");
		}
	}

	public void authorizeReadSecret(String userId, UUID secretId) throws NotAuthorizedException {
		// TODO Auto-generated method stub
		if(accessController.get(secretId)!=null) {
			if(accessController.get(secretId).indexOf(userId) == -1){					//if user is in authorized user,
				throw new NotAuthorizedException(userId+" is not authorized to access the secret");
			}
		}else {
			throw new NotAuthorizedException("No such secret found");
		}
	}

	public void authorizeUnshareSecret(String userId, UUID secretId, String targetId) {
		// TODO Auto-generated method stub
		if(accessController.get(secretId) != null){
				if(accessController.get(secretId).get(0) == userId){					//if user is in authorized user,
					if(accessController.get(secretId).indexOf(targetId)==-1) {
						throw new NotAuthorizedException("Secret not shared with "+targetId+", cannot delete "+targetId);
					}else {
						System.out.println("Unsharing is validated for "+targetId);	
					}
				}else{
					throw new NotAuthorizedException(userId+" is not authorized to unshare the secret");
				}
	}else{
		throw new NotAuthorizedException("No such secret found");
	}
		
	}
	
	public void creatingSecret(UUID retVal, String tempOwner, String tempSecret) {
		// TODO Auto-generated method stub
		ArrayList<String> authorizedUser = new ArrayList<String>();
		authorizedUser.add(tempOwner);
		accessController.put(retVal, authorizedUser);
		allSecrets.put(retVal,tempSecret);
		System.out.println("Secret " +retVal +" has been shared by "+ tempOwner);
	}
	
	public void sharingSecret(String userId, UUID secretId, String targetId) {
//		if(accessController.get(secretId).indexOf(targetId)==-1) {
		//Adding targetId to access controller
		if(accessController.get(secretId).indexOf(targetId)==-1) {
				accessController.get(secretId).add(targetId);
			System.out.println("Secret has been shared with "+targetId);
		}
			// Trusted User Implementation
				String trustedSignature = userId + secretId.toString();
				System.err.println(userId);
				System.err.println(targetId);
				System.err.println(trustedSignature);
				if(userId != targetId) {
					if(trustedUser.get(targetId)!=null) {
						trustedUser.get(targetId).add(trustedSignature);
					}else {
						HashSet<String> hash = new HashSet<String>(); 
						hash.add(trustedSignature);
						trustedUser.put(targetId, hash);
					}
				}else {
					System.out.println(userId+" shared secret to own "+ targetId);
				}
				System.err.println(trustedUser);

			//Worst Secret Keeper
				String worstSignature = targetId + secretId.toString();
				if(userId != targetId) {
					if(worstKeeper.get(userId)!=null) {
						worstKeeper.get(userId).add(worstSignature);
					}else {
						HashSet<String> hash = new HashSet<String>(); 
						hash.add(worstSignature);
						worstKeeper.put(userId, hash);
					}
				}else {
					System.out.println(userId+" shared secret to own "+ targetId);
				}
	}

	public void readingSecret(String userId, UUID secretId) {
		// TODO Auto-generated method stub
		if(readCountSecret.get(secretId)!=null){
			if(readCountSecret.get(secretId).indexOf(userId)==-1) {
				readCountSecret.get(secretId).add(userId);	
			}
			System.out.println("Secret has been read by "+userId);
		}else{
			ArrayList<String> readerList = new ArrayList<String>();
			readerList.add(userId);
			readCountSecret.put(secretId,readerList);
		}
	
	}

	public void unsharingSecret(UUID secretId, String targetId) {
		// TODO Auto-generated method stub
		if(accessController.get(secretId).get(0) != targetId){
			accessController.get(secretId).remove(targetId);
				System.out.println("Secret has been unshared with "+targetId);
		}else {
			System.err.println("Creater has tried to delete itself, handled silently");
		}
	}   
}