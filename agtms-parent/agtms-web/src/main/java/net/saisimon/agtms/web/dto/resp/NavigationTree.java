package net.saisimon.agtms.web.dto.resp;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 导航树信息对象
 * 
 * @author saisimon
 *
 */
@Data
public class NavigationTree implements Serializable {
	
	private static final long serialVersionUID = -8070305119606166744L;

	private String id;
	
	private String name;
	
	private String icon;
	
	private List<NavigationLink> links;
	
	private List<NavigationTree> childrens;
	
	@Data
	public static class NavigationLink implements Serializable {
		
		private static final long serialVersionUID = 3567625887453907811L;

		private String link;
		
		private String name;
		
	}
	
}
