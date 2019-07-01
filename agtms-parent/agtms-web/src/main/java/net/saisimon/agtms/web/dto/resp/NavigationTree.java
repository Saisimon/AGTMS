package net.saisimon.agtms.web.dto.resp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Data;

@Data
public class NavigationTree implements Serializable, Cloneable {
	
	private static final long serialVersionUID = -3273153285193976032L;
	
	public static final NavigationTree SYSTEM_MODULE_TREE = getSystemModuleTree();
	public static final NavigationTree USER_MODULE_TREE = getUserModuleTree();
	public static final Comparator<NavigationTree> COMPARATOR = (n1, n2) -> {
		int i = n1.getPriority().compareTo(n2.getPriority());
		if (i != 0) {
			return i;
		}
		return n1.getId().compareTo(n2.getId());
	};
	
	private String id;
	
	private String title;
	
	private String icon;
	
	private Long priority;
	
	private List<NavigationLink> links;
	
	private List<NavigationTree> childrens;
	
	private static NavigationTree getUserModuleTree() {
		NavigationTree userModule = new NavigationTree();
		userModule.setId("0");
		userModule.setIcon("users");
		userModule.setTitle("user.module");
		List<NavigationLink> links = new ArrayList<>();
		links.add(NavigationLink.USER_LINK);
		userModule.setLinks(links);
		return userModule;
	}
	
	private static NavigationTree getSystemModuleTree() {
		NavigationTree systemModule = new NavigationTree();
		systemModule.setId("0");
		systemModule.setIcon("cogs");
		systemModule.setTitle("system.module");
		List<NavigationLink> links = new ArrayList<>();
		links.add(NavigationLink.NAVIGATION_LINK);
		links.add(NavigationLink.TEMPLATE_LINK);
		links.add(NavigationLink.SELECTION_LINK);
		links.add(NavigationLink.TASK_LINK);
		links.add(NavigationLink.OPERATION_LINK);
		systemModule.setLinks(links);
		return systemModule;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		NavigationTree tree = (NavigationTree) super.clone();
		if (tree.childrens != null) {
			List<NavigationTree> cloneChildrens = new ArrayList<>(tree.childrens.size());
			for (NavigationTree children : tree.childrens) {
				cloneChildrens.add((NavigationTree) children.clone());
			}
			tree.childrens = cloneChildrens;
		}
		if (tree.links != null) {
			List<NavigationLink> cloneLinks = new ArrayList<>(tree.links.size());
			for (NavigationLink link : tree.links) {
				cloneLinks.add((NavigationLink) link.clone());
			}
			tree.links = cloneLinks;
		}
		return tree;
	}
	
	@Data
	public static class NavigationLink implements Serializable, Cloneable {
		
		private static final long serialVersionUID = -3939528526240558953L;
		
		public static final NavigationLink USER_LINK = new NavigationLink("/user/main", "user.management");
		public static final NavigationLink NAVIGATION_LINK = new NavigationLink("/navigation/main", "navigation.management");
		public static final NavigationLink TEMPLATE_LINK = new NavigationLink("/template/main", "template.management");
		public static final NavigationLink TASK_LINK = new NavigationLink("/task/main", "task.management");
		public static final NavigationLink OPERATION_LINK = new NavigationLink("/operation/main", "operation.management");
		public static final NavigationLink SELECTION_LINK = new NavigationLink("/selection/main", "selection.management");

		private String link;
		
		private String name;
		
		public NavigationLink(String link, String name) {
			this.link = link;
			this.name = name;
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
		
	}
	
}
