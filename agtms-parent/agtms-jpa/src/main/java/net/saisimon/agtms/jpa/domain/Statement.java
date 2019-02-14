package net.saisimon.agtms.jpa.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import net.saisimon.agtms.core.util.StringUtils;

@Data
public class Statement {
	
	private String expression;
	
	private List<Object> args;
	
	public Statement addArgs(Object... objects) {
		if (objects == null) {
			return this;
		}
		if (args == null) {
			args = new ArrayList<>();
		}
		for (Object object : objects) {
			args.add(object);
		}
		return this;
	}
	
	public Statement addArgs(List<Object> objects) {
		if (objects == null) {
			return this;
		}
		if (args == null) {
			args = new ArrayList<>();
		}
		for (Object object : objects) {
			args.add(object);
		}
		return this;
	}
	
	public boolean isNotEmpty() {
		return StringUtils.isNotEmpty(expression);
	}
	
}
