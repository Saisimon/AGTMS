package net.saisimon.agtms.core.domain.tag;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 前端下拉列表的选项
 * 
 * @author saisimon
 *
 * @param <T> value 的类型
 */
@Data
public class Option<T> implements Cloneable {
	
	private T id;
	
	private String label;
	
	private List<Option<T>> children;
	
	private Boolean isDisabled;
	
	public Option(T id, String label) {
		this(id, label, false);
	}
	
	public Option(T id, String label, boolean disabled) {
		this(id, label, disabled, null);
	}
	
	public Option(T id, String label, List<Option<T>> children) {
		this(id, label, false, children);
	}
	
	public Option(T id, String label, boolean disabled, List<Option<T>> children) {
		this.id = id;
		this.label = label;
		this.isDisabled = disabled;
		this.children = children;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		Option<T> option = (Option<T>) super.clone();
		if (this.children != null) {
			List<Option<T>> children = new ArrayList<>(this.children.size());
			for (Option<T> childrenOption : this.children) {
				children.add((Option<T>) childrenOption.clone());
			}
			option.children = children;
		}
		return option;
	}
	
}
