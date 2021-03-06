package edu.mit.oidc.repository.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.el.MethodNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.codec.binary.Hex;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

import edu.mit.oidc.model.GroupedUserInfo;

public class LdapUserInfoRepository implements UserInfoRepository {

	private LdapTemplate ldapTemplate;
	
	@Autowired
	private LdapGroupInfoRepository groupRepository;
	
	private MessageDigest digest;
	
	// result cache
	private LoadingCache<String, UserInfo> cache;
	
	private AttributesMapper attributesMapper = new AttributesMapper() {
		@Override
		public Object mapFromAttributes(Attributes attr) throws NamingException {
			
			if (attr.get("uid") == null || attr.get("uidnumber") == null) {
				return null; // we can't go on if there's no UID
			}
			
			UserInfo ui = new GroupedUserInfo();

			// save the UID as the preferred username
			ui.setPreferredUsername(attr.get("uid").get().toString());
			
			// TODO: hash the UID number to get the sub
			String sub = Hex.encodeHexString(digest.digest(attr.get("uidnumber").get().toString().getBytes()));
			ui.setSub(sub);
			/*
			// but for now just use the UID as the sub
			ui.setSub(attr.get("uid").get().toString());
			 */
			
			
			
			//
			// everything else is optional
			//
			
			// email address
			if (attr.get("mail") != null) {
				ui.setEmail(attr.get("mail").get().toString());
				ui.setEmailVerified(true);
			}

			// phone number
			if (attr.get("telephoneNumber") != null) {
				ui.setPhoneNumber(attr.get("telephoneNumber").get().toString());
				ui.setPhoneNumberVerified(true);
			}
			
			// name structure
			if (attr.get("displayName") != null) {
				ui.setName(attr.get("displayName").get().toString());
			}
			
			if (attr.get("givenName") != null) {
				ui.setGivenName(attr.get("givenName").get().toString());
			}
			if (attr.get("sn") != null) {
				ui.setFamilyName(attr.get("sn").get().toString());
			}
			if (attr.get("initials") != null) {
				ui.setMiddleName(attr.get("initials").get().toString());
			}
			
			return ui;
		}
	};
	
	private CacheLoader<String, UserInfo> cacheLoader = new CacheLoader<String, UserInfo>() {

		@Override
		public UserInfo load(String username) throws Exception {
			Filter find = new EqualsFilter("uid", username);
			List<GroupedUserInfo> res = ldapTemplate.search("", find.encode(), attributesMapper);
			
			if (res.isEmpty()) {
				throw new IllegalArgumentException("Unable to load uid: " + username);
			} else if (res.size() == 1) {
				
				GroupedUserInfo userInfo = (GroupedUserInfo) res.get(0);
				
				List<String> groups = getGroupRepository().getGroupsForUsername(username);

				if (groups != null && !groups.isEmpty()) {
					userInfo.setGroups(groups);
				}
				
				return userInfo;
				
			} else {
				throw new IllegalArgumentException("Unable to load uid: " + username);
			}
		}

	};

	public LdapUserInfoRepository() {
		 try {
			this.digest = MessageDigest.getInstance("MD5");
		 } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 
		 this.cache = CacheBuilder.newBuilder()
				 	.maximumSize(100)
				 	.expireAfterAccess(14, TimeUnit.DAYS)
				 	.build(cacheLoader);
		
		 
	}
	
	public LdapTemplate getLdapTemplate() {
		return ldapTemplate;
	}

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	@Override
	public UserInfo getByUsername(String username) {
		try {
			UserInfo ui = cache.get(username);
			return ui;
		} catch (UncheckedExecutionException e) {
			return null;
		} catch (ExecutionException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.mitre.openid.connect.repository.UserInfoRepository#getByEmailAddress(java.lang.String)
	 */
	@Override
	public UserInfo getByEmailAddress(String email) {
		// TODO Auto-generated method stub
		throw new MethodNotFoundException("Unable to search by email in this repostory.");		
	}

	/**
	 * @return the groupRepository
	 */
	public LdapGroupInfoRepository getGroupRepository() {
		return groupRepository;
	}

	/**
	 * @param groupRepository the groupRepository to set
	 */
	public void setGroupRepository(LdapGroupInfoRepository groupRepository) {
		this.groupRepository = groupRepository;
	}

}
