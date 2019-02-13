package net.saisimon.agtms.core.domain.sign;

import lombok.Builder;
import lombok.Data;

/**
 * 标志对象
 * 
 * @author saisimon
 *
 */
@Data
@Builder
public class Sign {
	
	private String name;
	
	private String text;
	
	private int order;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sign other = (Sign) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Sign [name=" + name + ", text=" + text + "]";
	}
	
}
