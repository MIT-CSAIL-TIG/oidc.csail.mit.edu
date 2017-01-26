package edu.mit.oidc.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import sun.security.krb5.KrbException;

@Controller
/**
 * 
 * This simple controller holds a target URL passed in from the login page. 
 * 
 * The redirector MUST start with the configured issuer URL.
 * 
 * @author jricher
 *
 */
public class KerberosRedirector {
	
	private static Logger logger = LoggerFactory.getLogger(KerberosRedirector.class);

	@Autowired
	private ConfigurationPropertiesBean config;
	
	@RequestMapping({"kerberos_login", "cert_login"})
	public View redirectToTarget(@RequestParam("target") String target) {
		if (target.startsWith(config.getIssuer())) {
			return new RedirectView(target, false, false, false);
		} else {
			return new RedirectView("/", true, false, false);
		}
	}
	
	@RequestMapping("kerbtest")
	public String kerbTest() {
		
		try {
			
			String fileName = System.getProperty("java.security.krb5.conf");
			
			BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
			
			String line = in.readLine();
			while(line != null) {
				logger.warn(">> " + line);
				  line = in.readLine();
			}
			in.close();
			
			sun.security.krb5.Config.refresh();
			
			sun.security.krb5.Config.getInstance().getDefaultRealm();
			
			
		} catch (KrbException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "error";
	}
}
