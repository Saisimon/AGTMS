package net.saisimon.agtms.remote.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.remote.repository.GenerateRemoteRepository;

@Service
public class GenerateRemoteService implements GenerateService {
	
	private static final Sign REMOTE_SIGN = Sign.builder().name("remote").text("REMOTE").order(Ordered.LOWEST_PRECEDENCE).build();
	
	@Autowired
	private GenerateRemoteRepository generateRemoteRepository;
	
	@Override
	public AbstractGenerateRepository getRepository() {
		return generateRemoteRepository;
	}
	
	@Override
	public Sign sign() {
		return REMOTE_SIGN;
	}

	@Override
	public boolean createTable() {
		throw new UnsupportedOperationException("Remote create table");
	}

	@Override
	public boolean alterTable(Template oldTemplate) {
		throw new UnsupportedOperationException("Remote alter table");
	}

	@Override
	public boolean dropTable() {
		throw new UnsupportedOperationException("Remote drop table");
	}

	@Override
	public boolean createIndex(String tableName, String columnName, boolean unique) {
		throw new UnsupportedOperationException("Remote create index");
	}

	@Override
	public boolean dropIndex(String tableName, String columnName) {
		throw new UnsupportedOperationException("Remote drop index");
	}
	
}
