package net.saisimon.agtms.core.domain.grid;

import java.util.Comparator;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import net.saisimon.agtms.core.constant.Constant;

/**
 * 属性对象
 * 
 * @author saisimon
 *
 * @param <T> 属性类型
 */
@Data
@Builder
public class Field<T> {
	
	private String name;
	
	private Object value;
	
	private String type;
	
	private String text;
	
	private boolean required;
	
	private boolean disabled;
	
	private boolean multiple;
	
	private boolean flat;
	
	@Builder.Default
	private String consists = Constant.Field.BRANCH_PRIORITY;
	
	private String state;
	
	private String views;
	
	private String sign;
	
	private boolean searchable;
	
	private List<T> options;
	
	private Integer ordered;
	
	public static final Comparator<Field<?>> COMPARATOR = (f1, f2) -> {
		if (f1.getOrdered() == null) {
			return -1;
		}
		if (f2.getOrdered() == null) {
			return 1;
		}
		return f1.getOrdered().compareTo(f2.getOrdered());
	};
	
}
