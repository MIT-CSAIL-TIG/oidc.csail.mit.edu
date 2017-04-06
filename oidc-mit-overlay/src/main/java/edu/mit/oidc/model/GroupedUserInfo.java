package edu.mit.oidc.model;

import java.util.List;

import org.mitre.openid.connect.model.DefaultUserInfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author jricher
 *
 */
public class GroupedUserInfo extends DefaultUserInfo {

	private static final long serialVersionUID = -2074183032101831440L;

	private List<String> groups;

	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/* (non-Javadoc)
	 * @see org.mitre.openid.connect.model.DefaultUserInfo#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObject base = super.toJson();
		
		JsonArray grp = new JsonArray();
		if (groups != null) {
			for (String group : groups) {
				grp.add(new JsonPrimitive(group));
			}
		}
		base.add("groups", grp);
		
		return base;
	}
	
}
