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

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.repository.impl.JpaOAuth2ClientRepository;
import org.mitre.openid.connect.view.HttpCodeView;
import org.mitre.openid.connect.view.JsonEntityView;
import org.mitre.openid.connect.view.JsonErrorView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

	@Autowired
	public JpaOAuth2ClientRepository clientRepository;
	
	@RequestMapping("/" + URL)
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
		
		status.put("success", false);
		status.put("error", "LDAP not called");
		
		return ImmutableMap.of("ldap", status);
	}

	/**
	 * Make a test call to the kerberos server to see if it's reachable.
	 * 
	 * @return
	 */
	private Map<String, Map<String, Object>> getKerbStatus() {
		Map<String, Object> status = new HashMap<>();
		
		status.put("success", false);
		status.put("error", "Kerberos not called");
		
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
	
	
	
}
