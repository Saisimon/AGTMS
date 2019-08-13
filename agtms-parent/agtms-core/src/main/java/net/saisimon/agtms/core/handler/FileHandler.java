package net.saisimon.agtms.core.handler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import net.saisimon.agtms.core.enums.FileTypes;

/**
 * 文件处理接口
 * 
 * @author saisimon
 *
 */
public interface FileHandler {

	/**
	 * 将指定数据集合填入指定文件
	 * 
	 * @param file  输出文件
	 * @param datas 数据集合
	 * @throws IOException 写入异常
	 */
	void populate(File file, List<List<Object>> datas) throws IOException;

	/**
	 * 获取指定文件中数据量
	 * 
	 * @param file 输入文件
	 * @return 数据大小
	 * @throws IOException 读取异常
	 */
	int size(MultipartFile file) throws IOException;

	/**
	 * 取出指定文件中的数据
	 * 
	 * @param file 输入文件
	 * @return 数据集合
	 * @throws IOException 读取异常
	 */
	List<List<String>> fetch(File file) throws IOException;

	/**
	 * @param mergedFile 合并后的文件
	 * @param files      待合并的文件集合
	 * @throws IOException 合并异常
	 */
	void merge(File mergedFile, List<File> files) throws IOException;

	/**
	 * 文件处理器对应的文件类型枚举
	 * 
	 * @return 文件类型枚举
	 */
	FileTypes type();

}
