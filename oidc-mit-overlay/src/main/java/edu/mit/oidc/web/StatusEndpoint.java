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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.repository.impl.JpaOAuth2ClientRepository;
import org.mitre.openid.connect.view.HttpCodeView;
import org.mitre.openid.connect.view.JsonEntityView;
import org.mitre.openid.connect.view.JsonErrorView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.security.kerberos.authentication.KerberosTicketValidation;
import org.springframework.security.kerberos.authentication.KerberosTicketValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.ImmutableMap;

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
	
	private static Logger logger = LoggerFactory.getLogger(StatusEndpoint.class);

	// used to test database connectivity
	@Autowired
	public JpaOAuth2ClientRepository clientRepository;
	
	// used to test ldap connectivity
	private LdapTemplate ldapTemplate;

	// used to look up ldap record
	private String testUsername;
	
	// used to call kerberos connectivity
	private KerberosTicketValidator ticketValidator;
	
	@RequestMapping(value = "/" + URL, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getStatus(Model m) {
		
		Map<String, Map<String, Object>> e = new HashMap<>();
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		
		try {
			List<Future<Map<String, Map<String, Object>>>> results = executor.invokeAll(Arrays.asList(
					new Callable<Map<String, Map<String, Object>>>() {
						// get database status
						@Override
						public Map<String, Map<String, Object>> call() throws Exception {
							return getDbStatus();
						}
					}, new Callable<Map<String, Map<String, Object>>>() {
						// get kerberos status
						@Override
						public Map<String, Map<String, Object>> call() throws Exception {
							return getKerbStatus();
						}
					}, new Callable<Map<String, Map<String, Object>>>() {
						// get LDAP status
						@Override
						public Map<String, Map<String, Object>> call() throws Exception {
							return getLdapStatus();
						}
					}));

			// collect all the results and return them
			for (Future<Map<String, Map<String, Object>>> result: results) {
				e.putAll(result.get());
			}
			
			m.addAttribute(JsonEntityView.ENTITY, e);
			return JsonEntityView.VIEWNAME;
		} catch (InterruptedException | ExecutionException ex) {
			
			m.addAttribute(HttpCodeView.CODE, HttpStatus.INTERNAL_SERVER_ERROR);
			m.addAttribute(JsonErrorView.ERROR_MESSAGE, ex.getMessage());
			
			return JsonErrorView.VIEWNAME;
		}
		
		
	}

	/**
	 * Make a test call to the LDAP server to see if it's reachable. 
	 * 
	 * @return
	 */
	private Map<String, Map<String, Object>> getLdapStatus() {
		Map<String, Object> status = new HashMap<>();
		
		try {
			Filter find = new EqualsFilter("uid", getTestUsername());

			List<String> searchResults = ldapTemplate.search("", find.encode(), new AttributesMapper<String>() {

				@Override
				public String mapFromAttributes(Attributes attrs) throws NamingException {
					return attrs.get("uid").get().toString();
				}
			});
			status.put("success", true);
			status.put("users", searchResults);
		} catch (Exception e) {
			status.put("success", false);
			status.put("error", e.getMessage());
		}
		
		return ImmutableMap.of("ldap", status);
	}

	/**
	 * Make a test call to the kerberos server to see if it's reachable.
	 * 
	 * @return
	 */
	private Map<String, Map<String, Object>> getKerbStatus() {
		Map<String, Object> status = new HashMap<>();
		
		try {

			
			/*
			byte[] token;
			KerberosTicketValidation ticket = ticketValidator.validateTicket(token);
			
			status.put("success", true);
			status.put("ticketValidation", ticket.username());
			*/
			
			throw new NoSuchMethodException("Kerberos test not implemented.");
			
		} catch (Exception e) {
			status.put("success", false);
			status.put("error", e.getMessage());
		}
		
		return ImmutableMap.of("kerberos", status);
	}

	/**
	 * Make a test call to the database to see if it's connected.
	 * 
	 * @return
	 */
	private Map<String, Map<String, Object>> getDbStatus() {
		
		Map<String, Object> status = new HashMap<>();
		
		try {
			Collection<ClientDetailsEntity> allClients = clientRepository.getAllClients();
			status.put("success", true);
			status.put("clientCount", allClients.size());
		} catch (Exception e) {
			status.put("success", false);
			status.put("error", e.getMessage());
		}
		
		return ImmutableMap.of("database", status);
	}

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
	 * @return the testUsername
	 */
	public String getTestUsername() {
		return testUsername;
	}

	/**
	 * @param testUsername the testUsername to set
	 */
	public void setTestUsername(String testUsername) {
		this.testUsername = testUsername;
	}
	
	
	
}
