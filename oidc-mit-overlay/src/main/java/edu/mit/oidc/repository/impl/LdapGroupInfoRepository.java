package edu.mit.oidc.repository.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.codec.binary.Hex;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.mit.oidc.model.GroupedUserInfo;

/**
 * @author jricher
 *
 */
public class LdapGroupInfoRepository {

	private LdapTemplate ldapTemplate;

	
	
	
	
	/**
	 * @return the ldapTemplate
	 */
	public LdapTemplate getLdapTemplate() {
		return ldapTemplate;
	}

	/**
	 * @param ldapTemplate the ldapTemplate to set
	 */
	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	/**
	 * @param username
	 * @return
	 */
	public List<String> getGroupsForUsername(String username) {
		Filter find = new EqualsFilter("memberUid", username);
		List<String> res = ldapTemplate.search("", find.encode(), attributesMapper);
		
		return res;
	}

	private AttributesMapper attributesMapper = new AttributesMapper() {
		@Override
		public Object mapFromAttributes(Attributes attr) throws NamingException {
			return attr.get("cn").toString();
		}
	};
	
}
