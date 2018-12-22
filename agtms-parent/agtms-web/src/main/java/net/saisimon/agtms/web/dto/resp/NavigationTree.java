package net.saisimon.agtms.web.dto.resp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import lombok.Data;

@Data
public class NavigationTree implements Serializable, Cloneable {
	
	private static final long serialVersionUID = -3273153285193976032L;
	
	public static final NavigationTree SYSTEM_MODEL_TREE = getSystemModelTree();
	
	private Long id;
	
	private String title;
	
	private String icon;
	
	private Integer priority;
	
	private Map<String, String> linkMap;
	
	private List<NavigationTree> childrens;
	
	private static NavigationTree getSystemModelTree() {
		NavigationTree systemModel = new NavigationTree();
		systemModel.setId(0L);
		systemModel.setIcon("cogs");
		systemModel.setTitle("system.model");
		Map<String, String> linkMap = new LinkedHashMap<>();
		linkMap.put("/navigate/main", "navigate.management");
		linkMap.put("/template/main", "template.management");
		systemModel.setLinkMap(linkMap);
		return systemModel;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		NavigationTree tree = (NavigationTree) super.clone();
		if (!CollectionUtils.isEmpty(tree.childrens)) {
			List<NavigationTree> cloneChildrens = new ArrayList<>(tree.childrens.size());
			for (NavigationTree children : tree.childrens) {
				cloneChildrens.add((NavigationTree) children.clone());
			}
			tree.childrens = cloneChildrens;
		}
		if (!CollectionUtils.isEmpty(tree.linkMap)) {
			tree.linkMap = new HashMap<>(tree.linkMap);
		}
		return tree;
	}
	
}
