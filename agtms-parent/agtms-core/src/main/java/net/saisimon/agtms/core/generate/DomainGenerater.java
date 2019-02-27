package net.saisimon.agtms.core.generate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.util.Assert;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.classloader.GenerateClassLoader;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.generate.Generate;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.StringUtils;

/**
 * 自定义对象生成器
 * 
 * @author saisimon
 *
 */
@Slf4j
public class DomainGenerater {
	
	private static final String DEFAULT_PACKAGE = Generate.class.getPackage().getName();
	private static final String DEFAULT_PACKAGE_PATH = DEFAULT_PACKAGE.replaceAll("\\.", "/");
	private static final String DOMAIN_PACKAGE_PATH = Type.getInternalName(Domain.class);
	private static final String SETTER_PREFIX = "set";
	private static final String GETTER_PREFIX = "get";
	
	private static final Map<String, GenerateClassLoader> GENERATE_CLASSLOADER_MAP = new HashMap<>();
	
	/**
	 * 构建自定义对象名称
	 * 
	 * @param id
	 * @return
	 */
	public static String buildGenerateName(String key) {
		Assert.notNull(key, "key can not be null");
		return "Generate$" + key;
	}
	
	/**
	 * 生成自定义对象 class 文件
	 * 
	 * @param namespace
	 * @param fieldMap
	 * @param domainName
	 * @param force
	 * @return
	 * @throws GenerateException
	 */
	public static Class<Domain> generate(String namespace, Map<String, String> fieldMap, String domainName, boolean force) throws GenerateException {
		return generate(namespace, fieldMap, domainName, force, Generate.class);
	}
	
	/**
	 * 生成自定义对象 class 文件
	 * 
	 * @param namespace
	 * @param fieldMap
	 * @param domainName
	 * @param force
	 * @param templateClass
	 * @return
	 * @throws GenerateException
	 */
	@SuppressWarnings("unchecked")
	public static Class<Domain> generate(String namespace, Map<String, String> fieldMap, String domainName, boolean force, Class<?> templateClass) throws GenerateException {
		if (fieldMap == null || StringUtils.isBlank(domainName)) {
			return null;
		}
		Map<String, String> map = new HashMap<>(fieldMap);
		if (null != templateClass) {
			for (Field field : templateClass.getDeclaredFields()) {
				map.put(field.getName(), field.getType().getName());
			}
		}
		if (domainName.indexOf('.') > 0) {
			domainName = domainName.replace("\\.", "_");
		}
		String domainFullName = DEFAULT_PACKAGE + "." + domainName;
		String domainFullPathName = DEFAULT_PACKAGE_PATH + "/" + domainName;
		File file = FileUtil.file(GenerateClassLoader.GENERATE_CLASS_PATH + File.separator + namespace + File.separator + domainFullPathName + ".class");
		GenerateClassLoader oldClassloader = GENERATE_CLASSLOADER_MAP.get(namespace);
		try {
			boolean needUpdate = force || !file.exists();
			Class<?> oldClass = null;
			if (oldClassloader == null) {
				needUpdate = true;
			} else {
				try {
					oldClass = oldClassloader.loadClass(domainFullName);
					needUpdate = needUpdate || needUpdate(map, oldClass);
				} catch (ClassNotFoundException e) {
					needUpdate = true;
				}
			}
			if (needUpdate) {
				FileUtils.createDir(file.getParentFile());
				file.createNewFile();
				String superFullPathName = "java/lang/Object";
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, domainFullPathName, null, superFullPathName, new String[]{DOMAIN_PACKAGE_PATH});
				buildConstructer(cw, superFullPathName);
				for (Entry<String, String> entry : map.entrySet()) {
					String fieldName = entry.getKey();
					String fieldDesc = Type.getDescriptor(Class.forName(entry.getValue()));
					buildField(cw, fieldName, fieldDesc);
					buildSetterMethod(cw, domainFullPathName, fieldName, fieldDesc);
					buildGetterMethod(cw, domainFullPathName, fieldName, fieldDesc);
				}
				cw.visitEnd();
				byte[] bs = cw.toByteArray();
				try (FileOutputStream fos = new FileOutputStream(file)) {
					fos.write(bs);
				}
				oldClassloader = new GenerateClassLoader(namespace);
				GENERATE_CLASSLOADER_MAP.put(namespace, oldClassloader);
				oldClassloader.addGenerateClassName(domainFullName);
				return (Class<Domain>) oldClassloader.loadClass(domainFullName);
			} else {
				return (Class<Domain>) oldClass;
			}
		} catch (Throwable t) {
			if (oldClassloader != null) {
				oldClassloader.removeGenerateClassName(domainFullName);
			}
			if (file.exists()) {
				file.delete();
			}
			throw new GenerateException("Generater " + domainFullName + " Error!", t);
		}
	}
	
	/**
	 * 删除自定义对象 class 文件
	 * 
	 * @param namespace
	 * @param domainName
	 * @return
	 */
	public static boolean removeDomainClass(String namespace, String domainName) {
		String domainFullName = DEFAULT_PACKAGE + "." + domainName;
		GenerateClassLoader oldClassloader = GENERATE_CLASSLOADER_MAP.get(namespace);
		if (oldClassloader != null) {
			oldClassloader.removeGenerateClassName(domainFullName);
		}
		String domainFullPathName = DEFAULT_PACKAGE_PATH + "/" + domainName;
		File file = new File(GenerateClassLoader.GENERATE_CLASS_PATH + File.separator + namespace + File.separator + domainFullPathName + ".class");
		if (file.exists()) {
			return file.delete();
		}
		return true;
	}
	
	public static Object getField(Domain domain, String fieldName) {
		if (domain == null) {
			throw new IllegalArgumentException("domain can not be null");
		}
		try {
			String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(fieldName);
			Method m = domain.getClass().getMethod(getterMethodName, new Class<?>[]{});
			return m.invoke(domain);
		} catch (Exception e) {
			log.error(domain.getClass().getName() + " get field("+ fieldName +") error, msg : " + e.getMessage());
			return null;
		}
	}
	
	public static void setField(Domain domain, String fieldName, Object fieldValue, Class<?> fieldClass) {
		if (domain == null) {
			throw new IllegalArgumentException("domain can not be null");
		}
		try {
			String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(fieldName);
			Method m = domain.getClass().getMethod(setterMethodName, fieldClass);
			m.invoke(domain, fieldValue);
		} catch (Exception e) {
			log.error(domain.getClass().getName() + " set field("+ fieldName +") error, msg : " + e.getMessage());
		}
	}
	
	private static void buildConstructer(ClassWriter cw, String superFullPathName) {
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superFullPathName, "<init>", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private static void buildField(ClassWriter cw, String fieldName, String fieldDesc) {
		cw.visitField(Opcodes.ACC_PRIVATE, fieldName, fieldDesc, null, null).visitEnd();
	}

	private static void buildSetterMethod(ClassWriter cw, String domainFullName, String fieldName, String fieldDesc) {
		String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(fieldName);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, setterMethodName, "(" + fieldDesc + ")V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		int loadOpcode = parseLoadOpcode(fieldDesc);
		mv.visitVarInsn(loadOpcode, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, domainFullName, fieldName, fieldDesc);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}
	
	private static void buildGetterMethod(ClassWriter cw, String domainFullName, String fieldName, String fieldDesc) {
		String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(fieldName);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, getterMethodName, "()" + fieldDesc, null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, domainFullName, fieldName, fieldDesc);
		int returnOpcode = parseReturnOpcode(fieldDesc);
		mv.visitInsn(returnOpcode);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private static boolean needUpdate(Map<String, String> fieldMap, Class<?> oldClass) throws IOException {
		Field[] fields = oldClass.getDeclaredFields();
		if (fieldMap.size() != fields.length) {
			return true;
		}
		for (Field field : fields) {
			String fieldName = field.getName();
			Class<?> fieldClass = field.getType();
			if (!fieldMap.containsKey(fieldName) || !fieldClass.getName().equals(fieldMap.get(fieldName))) {
				return true;
			}
		}
		return false;
	}
	
	private static int parseReturnOpcode(String fieldDesc) {
		int opcode;
		switch (fieldDesc) {
		case "I":
		case "Z":
		case "B":
		case "C":
		case "S":
			opcode = Opcodes.IRETURN;
			break;
		case "D":
			opcode = Opcodes.DRETURN;
			break;
		case "F":
			opcode = Opcodes.FRETURN;
			break;
		case "J":
			opcode = Opcodes.LRETURN;
			break;
		default:
			opcode = Opcodes.ARETURN;
			break;
		}
		return opcode;
	}
	
	private static int parseLoadOpcode(String fieldDesc) {
		int opcode;
		switch (fieldDesc) {
		case "I":
		case "Z":
		case "B":
		case "C":
		case "S":
			opcode = Opcodes.ILOAD;
			break;
		case "D":
			opcode = Opcodes.DLOAD;
			break;
		case "F":
			opcode = Opcodes.FLOAD;
			break;
		case "J":
			opcode = Opcodes.LLOAD;
			break;
		default:
			opcode = Opcodes.ALOAD;
			break;
		}
		return opcode;
	}
	
}
