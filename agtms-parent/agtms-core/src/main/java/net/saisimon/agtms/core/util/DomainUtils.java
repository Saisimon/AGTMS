package net.saisimon.agtms.core.util;

import java.util.Date;

import cn.hutool.core.date.DateUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.enums.Classes;

public class DomainUtils {
	
	private DomainUtils() {
		throw new IllegalAccessError();
	}
	
	public static void fillCommonFields(Domain newDomain, Domain oldDomain) {
		Date time = new Date();
		if (oldDomain == null) {
			newDomain.setField(Constant.CREATETIME, time, Date.class);
			UserInfo userInfo = AuthUtils.getUserInfo();
			if (userInfo != null) {
				newDomain.setField(Constant.OPERATORID, userInfo.getUserId(), Long.class);
			}
		} else {
			newDomain.setField(Constant.ID, oldDomain.getField(Constant.ID), Long.class);
			newDomain.setField(Constant.CREATETIME, oldDomain.getField(Constant.CREATETIME), Date.class);
			newDomain.setField(Constant.OPERATORID, oldDomain.getField(Constant.OPERATORID), Long.class);
		}
		newDomain.setField(Constant.UPDATETIME, time, Date.class);
	}
	
	public static Object parseFieldValue(Object fieldValue, String fieldType) {
		if (fieldValue != null && fieldType != null) {
			try {
				if (Classes.LONG.getName().equals(fieldType)) {
					return Long.valueOf(fieldValue.toString());
				} else if (Classes.DOUBLE.getName().equals(fieldType)) {
					return Double.valueOf(fieldValue.toString());
				} else if (Classes.DATE.getName().equals(fieldType)) {
					return DateUtil.parseDate(fieldValue.toString()).toJdkDate();
				}
			} catch (Exception e) {
				return null;
			}
		}
		return fieldValue;
	}
	
}
