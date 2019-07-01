package net.saisimon.agtms.web.dto.resp.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BaseInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String action;
	
	private List<Boolean> disableActions = new ArrayList<>();
	
}
