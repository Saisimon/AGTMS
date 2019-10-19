package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

/**
 * 消息通知信息对象
 * 
 * @author saisimon
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NotificationInfo extends BaseInfo {

	private static final long serialVersionUID = -4174649699142236363L;

	private String title;
	
	private String content;
	
	private String type;
	
	private Date createTime;
	
}
