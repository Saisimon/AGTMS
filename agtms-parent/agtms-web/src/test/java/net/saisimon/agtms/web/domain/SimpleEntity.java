package net.saisimon.agtms.web.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import net.saisimon.agtms.annotation.FieldInfo;
import net.saisimon.agtms.annotation.TemplateInfo;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.web.repository.SimpleEntityRepository;
import net.saisimon.agtms.web.selection.GenderSelection;

@Entity
@Data
@Table(name = "simple_entity")
@TemplateInfo(key = "simpleEntity", repository = SimpleEntityRepository.class, title = "测试", functions = { Functions.VIEW, Functions.CREATE, Functions.EDIT, Functions.REMOVE, Functions.BATCH_EDIT, Functions.BATCH_REMOVE, Functions.EXPORT, Functions.IMPORT })
public class SimpleEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	@FieldInfo(columnOrdered = 0, columnTitle="基本信息", fieldOrdered = 0, fieldTitle = "年龄", fieldType = Classes.LONG, filter = true, sorted = true)
	private Integer age;
	
	@Column
	@FieldInfo(columnOrdered = 0, fieldOrdered = 1, fieldTitle = "姓名", filter = true, uniqued = true, required = true)
	private String name;
	
	@Column
	@FieldInfo(columnOrdered = 0, fieldOrdered = 2, fieldTitle = "性别", fieldType = Classes.LONG, view = Views.SELECTION, selection = GenderSelection.class, filter = true)
	private Integer gender;
	
	@Column
	@FieldInfo(columnOrdered = 0, fieldOrdered = 3, fieldTitle = "生日", fieldType = Classes.DATE, filter = true, sorted = true, required = true)
	private Date birthday;
	
	@Column
	@FieldInfo(columnOrdered = 1, fieldOrdered = 0, fieldTitle = "个人主页", view = Views.LINK)
	private String home;
	
	@Column
	@FieldInfo(columnOrdered = 2, fieldOrdered = 0, fieldTitle = "介绍", view = Views.TEXTAREA, hidden = true)
	private String desc;
	
}