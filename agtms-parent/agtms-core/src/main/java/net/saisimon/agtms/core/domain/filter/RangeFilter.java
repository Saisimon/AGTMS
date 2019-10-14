package net.saisimon.agtms.core.domain.filter;

import lombok.Getter;
import lombok.Setter;
import net.saisimon.agtms.core.domain.tag.Input;

/**
 * 前端范围过滤条件的对象
 * 
 * @author saisimon
 *
 * @param <T> value 的类型
 */
@Setter
@Getter
public class RangeFilter<T> extends FieldFilter {
	
	private Input<T> from;
	
	private Input<T> to;
	
	public RangeFilter() {
		super("range");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		RangeFilter<T> rangeFilter = (RangeFilter<T>) super.clone();
		if (this.from != null) {
			rangeFilter.from = (Input<T>) this.from.clone();
		}
		if (this.to != null) {
			rangeFilter.to = (Input<T>) this.to.clone();
		}
		return rangeFilter;
	}
	
	public static <T> FieldFilter rangeFilter(T fromValue, String fromType, T toValue, String toType) {
		RangeFilter<T> rangeFilter = new RangeFilter<>();
		rangeFilter.setFrom(new Input<>(fromValue, fromType));
		rangeFilter.setTo(new Input<>(toValue, toType));
		return rangeFilter;
	}
	
}
