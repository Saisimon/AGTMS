package net.saisimon.agtms.core.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import org.springframework.util.Assert;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import net.saisimon.agtms.core.util.FileUtils;

/**
 * 自动生成的自定义对象的类加载器
 * 
 * @author saisimon
 *
 */
public class GenerateClassLoader extends URLClassLoader {
	
	private final Set<String> generateClasses = new ConcurrentHashSet<>();
	
	public GenerateClassLoader(String generateClasspath, String namespace) {
		super(new URL[]{});
		Assert.notNull(namespace, "need namespace");
		File generateClassDir = FileUtil.file(generateClasspath + "/" + namespace);
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
					return Thread.currentThread().getContextClassLoader().loadClass(name);
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
