package edu.mit.oidc.service.impl;

import java.util.Set;

import org.mitre.openid.connect.service.impl.DefaultScopeClaimTranslationService;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * @author jricher
 *
 */
public class GroupedScopeClaimTranslationService extends DefaultScopeClaimTranslationService {

	private SetMultimap<String, String> scopesToClaims = HashMultimap.create();

	public GroupedScopeClaimTranslationService() {
		scopesToClaims.put("groups", "groups");
	}

	/* (non-Javadoc)
	 * @see org.mitre.openid.connect.service.impl.DefaultScopeClaimTranslationService#getClaimsForScope(java.lang.String)
	 */
	@Override
	public Set<String> getClaimsForScope(String scope) {
		if (scopesToClaims.containsKey(scope)) {
			return scopesToClaims.get(scope);
		} else {
			return super.getClaimsForScope(scope);
		}
		
	}
	
}
