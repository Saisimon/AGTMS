package net.saisimon.agtms.web.dto.resp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class NavigationTree implements Serializable, Cloneable {
	
	private static final long serialVersionUID = -3273153285193976032L;
	
	public static final NavigationTree SYSTEM_MODEL_TREE = getSystemModelTree();
	
	private Long id;
	
	private String title;
	
	private String icon;
	
	private Integer priority;
	
	private List<NavigationLink> links;
	
	private List<NavigationTree> childrens;
	
	private static NavigationTree getSystemModelTree() {
		NavigationTree systemModel = new NavigationTree();
		systemModel.setId(0L);
		systemModel.setIcon("cogs");
		systemModel.setTitle("system.model");
		List<NavigationLink> links = new ArrayList<>();
		links.add(NavigationLink.NAVIGATION_LINK);
		links.add(NavigationLink.TEMPLATE_LINK);
		links.add(NavigationLink.TASK_LINK);
		links.add(NavigationLink.OPERATION_LINK);
		systemModel.setLinks(links);
		return systemModel;
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
		
		public static final NavigationLink NAVIGATION_LINK = new NavigationLink("/navigation/main", "navigation.management");
		public static final NavigationLink TEMPLATE_LINK = new NavigationLink("/template/main", "template.management");
		public static final NavigationLink TASK_LINK = new NavigationLink("/task/main", "task.management");
		public static final NavigationLink OPERATION_LINK = new NavigationLink("/operation/main", "operation.management");

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
