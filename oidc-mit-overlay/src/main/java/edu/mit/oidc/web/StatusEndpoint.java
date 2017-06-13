/*******************************************************************************
 * Copyright 2017 The MIT Internet Trust Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package edu.mit.oidc.web;

import java.util.HashMap;
import java.util.Map;

import org.mitre.openid.connect.view.JsonEntityView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * Checks the status of the underlying system components. 
 * 
 * @author jricher
 *
 */
@Controller
public class StatusEndpoint {

	public static final String URL = "status";
	
	@RequestMapping("/" + URL)
	public String getStatus(Model m) {
		
		Map<String, Map<String, String>> e = new HashMap<>();
		
		// get database status
		Map<String, String> dbStatus = getDbStatus();
		e.put("database", dbStatus);
		
		// get kerberos status
		Map<String, String> kerbStatus = getKerbStatus();
		e.put("kerberos", kerbStatus);
		
		// get LDAP status
		Map<String, String> ldapStatus = getLdapStatus();
		e.put("ldap", ldapStatus);
		
		m.addAttribute(JsonEntityView.ENTITY, e);
		
		return JsonEntityView.VIEWNAME;
	}

	/**
	 * @return
	 */
	private Map<String, String> getLdapStatus() {
		// TODO Auto-generated method stub
		return null;
		
	}

	/**
	 * @return
	 */
	private Map<String, String> getKerbStatus() {
		// TODO Auto-generated method stub
		return null;
		
	}

	/**
	 * @return
	 */
	private Map<String, String> getDbStatus() {
		// TODO Auto-generated method stub
		return null;
		
	}
	
	
	
}
