package net.saisimon.agtms.core.service;

import java.util.Date;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.domain.entity.Notification;
import net.saisimon.agtms.core.enums.NotificationStatuses;
import net.saisimon.agtms.core.enums.NotificationTypes;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 消息通知服务接口
 * 
 * @author saisimon
 *
 */
public interface NotificationService extends BaseService<Notification, Long>, Ordered {
	
	default Notification sendNotification(String title, String content, NotificationTypes type, Long operatorId) {
		if (SystemUtils.isBlank(title) || SystemUtils.isBlank(content) || type == null || operatorId == null) {
			return null;
		}
		Notification notification = new Notification();
		notification.setContent(content);
		notification.setCreateTime(new Date());
		notification.setOperatorId(operatorId);
		notification.setStatus(NotificationStatuses.UNREAD.getStatus());
		notification.setTitle(title);
		notification.setType(type.getType());
		saveOrUpdate(notification);
		return notification;
	}
	
}
