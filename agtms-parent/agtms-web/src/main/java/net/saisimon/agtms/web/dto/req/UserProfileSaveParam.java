package net.saisimon.agtms.web.dto.req;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserProfileSaveParam implements Serializable {

	private static final long serialVersionUID = 5696615388572482761L;
	
	private String nickname;
	
	private String avatar;
	
	private String remark;
	
}
