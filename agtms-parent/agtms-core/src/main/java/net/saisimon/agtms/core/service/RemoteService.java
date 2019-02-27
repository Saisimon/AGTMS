package net.saisimon.agtms.core.service;

import java.util.List;

import net.saisimon.agtms.core.domain.Template;

public interface RemoteService {
	
	List<Template> templates(String serviceId);
	
}
