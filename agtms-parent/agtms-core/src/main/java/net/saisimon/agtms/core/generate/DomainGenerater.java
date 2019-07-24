package net.saisimon.agtms.core.generate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.util.Assert;

import cn.hutool.core.io.FileUtil;
import net.saisimon.agtms.core.classloader.GenerateClassLoader;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.generate.Generate;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 自定义对象生成器
 * 
 * @author saisimon
 *
 */
public class DomainGenerater {
	
	/**
	 * 默认包名
	 */
	private static final String DEFAULT_PACKAGE = Generate.class.getPackage().getName();
	/**
	 * 默认包路径
	 */
	private static final String DEFAULT_PACKAGE_PATH = DEFAULT_PACKAGE.replaceAll("\\.", "/");
	/**
	 * Domain 接口包路径
	 */
	private static final String DOMAIN_PACKAGE_PATH = Type.getInternalName(Domain.class);
	/**
	 * set 前缀
	 */
	private static final String SETTER_PREFIX = "set";
	/**
	 * get 前缀
	 */
	private static final String GETTER_PREFIX = "get";
	
	/**
	 * 父类
	 */
	private static final String SUPER_FULL_PATH_NAME = "java/lang/Object";
	/**
	 * 自定义对象的类加载器映射集合
	 * key 为命名空间
	 * value 为类加载器
	 */
	private static final Map<String, GenerateClassLoader> GENERATE_CLASSLOADER_MAP = new ConcurrentHashMap<>();
	
	private final AgtmsProperties agtmsProperties;
	
	public DomainGenerater(AgtmsProperties agtmsProperties) {
		this.agtmsProperties = agtmsProperties;
	}
	
	/**
	 * 构建自定义对象名称
	 * 
	 * @param key 自定义对象的唯一标识
	 * @return 自定义对象名称
	 */
	public String buildGenerateName(String key) {
		Assert.notNull(key, "key can not be null");
		return "Generate$" + key;
	}
	
	/**
	 * 生成自定义对象 class 对象，模板对象为 Generate.class
	 * 
	 * @param namespace 命名空间
	 * @param fieldMap 字段与类型的映射集合
	 * @param name 自定义对象名
	 * @return 自定义对象 class 对象
	 * @throws GenerateException 生成自定义对象异常
	 * 
	 * @see net.saisimon.agtms.core.domain.generate.Generate
	 */
	public Class<Domain> generate(String namespace, Map<String, String> fieldMap, String name) throws GenerateException {
		return generate(namespace, fieldMap, name, false, Generate.class);
	}
	
	/**
	 * 生成自定义对象 class 对象，模板对象为 Generate.class
	 * 
	 * @param namespace 命名空间
	 * @param fieldMap 字段与类型的映射集合
	 * @param name 自定义对象名
	 * @param force 强制刷新生成
	 * @return 自定义对象 class 对象
	 * @throws GenerateException 生成自定义对象异常
	 * 
	 * @see net.saisimon.agtms.core.domain.generate.Generate
	 */
	public Class<Domain> generate(String namespace, Map<String, String> fieldMap, String name, boolean force) throws GenerateException {
		return generate(namespace, fieldMap, name, force, Generate.class);
	}
	
	/**
	 * 生成自定义对象 class 对象
	 * 
	 * @param namespace 命名空间
	 * @param fieldMap 字段与类型的映射集合
	 * @param name 自定义对象名
	 * @param force 强制刷新生成
	 * @param templateClass 自定义对象的模板对象
	 * @return 自定义对象 class 对象
	 * @throws GenerateException 生成自定义对象异常
	 */
	@SuppressWarnings("unchecked")
	public Class<Domain> generate(String namespace, Map<String, String> fieldMap, String name, boolean force, Class<?> templateClass) throws GenerateException {
		String domainName = name;
		if (fieldMap == null || SystemUtils.isBlank(namespace) || SystemUtils.isBlank(domainName)) {
			return null;
		}
		Map<String, String> map = new HashMap<>(fieldMap);
		if (null != templateClass) {
			for (Field field : templateClass.getDeclaredFields()) {
				map.put(field.getName(), field.getType().getName());
			}
		}
		if (domainName.indexOf('.') > 0) {
			domainName = domainName.replace(".", "_");
		}
		String domainFullName = DEFAULT_PACKAGE + "." + domainName;
		String domainFullPathName = DEFAULT_PACKAGE_PATH + "/" + domainName;
		File file = FileUtil.file(agtmsProperties.getGenerateClasspath() + File.separator + namespace + File.separator + domainFullPathName + ".class");
		GenerateClassLoader oldClassloader = GENERATE_CLASSLOADER_MAP.get(namespace);
		try {
			Class<?> oldClass = null;
			if (force || !file.exists() || (oldClass = getOldClass(map, oldClassloader, domainFullName)) == null) {
				generateClassFile(map, domainFullPathName, file);
				oldClassloader = new GenerateClassLoader(agtmsProperties.getGenerateClasspath(), namespace);
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
	 * @param namespace 命名空间
	 * @param domainName 自定义对象名
	 * @return 删除状态
	 */
	public boolean removeDomainClass(String namespace, String domainName) {
		if (SystemUtils.isBlank(namespace) || SystemUtils.isBlank(domainName)) {
			return false;
		}
		String domainFullName = DEFAULT_PACKAGE + "." + domainName;
		GenerateClassLoader oldClassloader = GENERATE_CLASSLOADER_MAP.get(namespace);
		if (oldClassloader != null) {
			oldClassloader.removeGenerateClassName(domainFullName);
		}
		String domainFullPathName = DEFAULT_PACKAGE_PATH + "/" + domainName;
		File file = new File(agtmsProperties.getGenerateClasspath() + File.separator + namespace + File.separator + domainFullPathName + ".class");
		if (file.exists()) {
			return file.delete();
		}
		return true;
	}
	
	private Class<?> getOldClass(Map<String, String> map, GenerateClassLoader oldClassloader, String domainFullName) throws IOException {
		if (oldClassloader == null) {
			return null;
		}
		try {
			Class<?> oldClass = oldClassloader.loadClass(domainFullName);
			if (needUpdate(map, oldClass)) {
				return null;
			}
			return oldClass;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	private void generateClassFile(Map<String, String> map, String domainFullPathName, File file) throws ClassNotFoundException, IOException, FileNotFoundException {
		FileUtils.createDir(file.getParentFile());
		file.createNewFile();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, domainFullPathName, null, SUPER_FULL_PATH_NAME, new String[]{DOMAIN_PACKAGE_PATH});
		buildConstructer(cw, SUPER_FULL_PATH_NAME);
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
	}
	
	private void buildConstructer(ClassWriter cw, String superFullPathName) {
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superFullPathName, "<init>", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private void buildField(ClassWriter cw, String fieldName, String fieldDesc) {
		cw.visitField(Opcodes.ACC_PRIVATE, fieldName, fieldDesc, null, null).visitEnd();
	}

	private void buildSetterMethod(ClassWriter cw, String domainFullName, String fieldName, String fieldDesc) {
		String setterMethodName = SETTER_PREFIX + SystemUtils.capitalize(fieldName);
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
	
	private void buildGetterMethod(ClassWriter cw, String domainFullName, String fieldName, String fieldDesc) {
		String getterMethodName = GETTER_PREFIX + SystemUtils.capitalize(fieldName);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, getterMethodName, "()" + fieldDesc, null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, domainFullName, fieldName, fieldDesc);
		int returnOpcode = parseReturnOpcode(fieldDesc);
		mv.visitInsn(returnOpcode);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private boolean needUpdate(Map<String, String> fieldMap, Class<?> oldClass) throws IOException {
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
	
	private int parseReturnOpcode(String fieldDesc) {
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
	
	private int parseLoadOpcode(String fieldDesc) {
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
