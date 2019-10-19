package net.saisimon.agtms.core.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 消息通知实体对象
 * 
 * @author saisimon
 *
 */
@Data
@Entity
@Table(name=Notification.TABLE_NAME)
@Document(collection=Notification.TABLE_NAME)
public class Notification implements Serializable {
	
	private static final long serialVersionUID = 1799449493271035906L;

	public static final String TABLE_NAME = "agtms_notification";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 消息通知类型
	 * 
	 * @see net.saisimon.agtms.core.enums.NotificationTypes
	 */
	@Column(nullable=false)
	private Integer type;
	
	/**
	 * 消息通知标题
	 */
	@Column(nullable=false, length=64)
	private String title;
	
	/**
	 * 消息通知内容
	 */
	@Column(nullable=false, length=4096)
	private String content;
	
	/**
	 * 消息通知状态
	 * 
	 * @see net.saisimon.agtms.core.enums.NotificationStatuses
	 */
	private Integer status;
	
	/**
	 * 消息通知创建时间
	 */
	@Column
	private Date createTime;
	
	/**
	 * 操作人ID
	 */
	@Column(nullable=false)
	private Long operatorId;
	
}
