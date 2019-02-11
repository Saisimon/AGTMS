package net.saisimon.agtms.core.domain.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.core.domain.tag.Input;

@Data
@EqualsAndHashCode(callSuper=false)
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
		rangeFilter.from = (Input<T>) rangeFilter.from.clone();
		rangeFilter.to = (Input<T>) rangeFilter.to.clone();
		return rangeFilter;
	}
	
	public static <T> FieldFilter rangeFilter(T fromValue, String fromType, T toValue, String toType) {
		RangeFilter<T> rangeFilter = new RangeFilter<>();
		rangeFilter.setFrom(new Input<>(fromValue, fromType));
		rangeFilter.setTo(new Input<>(toValue, toType));
		return rangeFilter;
	}
	
}
