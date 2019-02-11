package net.saisimon.agtms.web.task;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.web.dto.req.ImportParam;

@Component
public class ImportActuator implements Actuator<ImportParam> {
	
	private static final Sign IMPORT_SIGN = Sign.builder().name(Functions.IMPORT.getFunction()).text(Functions.IMPORT.getFunction()).build();
	
	@Override
	public Result execute(ImportParam param) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public Sign sign() {
		return IMPORT_SIGN;
	}

}
