package net.saisimon.agtms.core.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.PropertyUtils;

public class GenerateClassLoader extends URLClassLoader {
	
	public static final String GENERATE_CLASS_PATH = FileUtil.normalize(PropertyUtils.fetchYaml("extra.class.path", "/tmp/classes").toString());
	
	private final Set<String> generateClasses = new HashSet<>();
	
	public GenerateClassLoader(String namespace) {
		super(new URL[]{});
		File generateClassDir = FileUtil.file(GENERATE_CLASS_PATH + "/" + namespace);
		try {
			FileUtils.createDir(generateClassDir);
		} catch (IOException e) {
			throw new IllegalArgumentException("create " + generateClassDir.getAbsolutePath() + " dir failed");
		}
		addURL(URLUtil.getURL(generateClassDir));
	}
	
	public void addGenerateClassName(String name) {
		this.generateClasses.add(name);
	}
	
	public void removeGenerateClassName(String name) {
		this.generateClasses.remove(name);
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> cls = findLoadedClass(name);
			if (cls == null) {
				if (!generateClasses.contains(name)) {
					return super.loadClass(name, resolve);
				}
				cls = findClass(name);
				generateClasses.add(name);
			}
			if (cls == null) {
				throw new ClassNotFoundException(name);
			}
			if (resolve) {
				resolveClass(cls);
			}
			return cls;
		}
	}
	
}
