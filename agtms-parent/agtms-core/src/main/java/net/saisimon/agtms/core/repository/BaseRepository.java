package net.saisimon.agtms.core.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;

/**
 * 基础 Repository 接口
 * 
 * @author saisimon
 *
 * @param <T> 实体类型
 * @param <ID> 实体 ID 的类型
 */
public interface BaseRepository<T, ID> {
	
	/**
	 * 根据指定条件查询总数
	 * 
	 * @param filter 指定过滤条件
	 * @return 总数
	 */
	Long count(FilterRequest filter);
	
	/**
	 * 根据指定条件判断是否存在
	 * 
	 * @param filter 指定过滤条件
	 * @return 存在返回 true， 否则返回 false
	 */
	Boolean exists(FilterRequest filter);
	
	/**
	 * 根据指定条件查询列表
	 * 
	 * @param filter 指定过滤条件
	 * @return 实体对象列表
	 */
	default List<T> findList(FilterRequest filter) {
		return findList(filter, null);
	}
	
	/**
	 * 根据指定条件查询列表
	 * 
	 * @param filter 指定过滤条件
	 * @param sort 指定排序条件
	 * @return 实体对象列表
	 */
	List<T> findList(FilterRequest filter, FilterSort sort);
	
	/**
	 * 根据指定条件查询分页信息
	 * 
	 * @param filter 指定过滤条件
	 * @param pageable 指定分页条件
	 * @return 实体对象分页对象
	 */
	Page<T> findPage(FilterRequest filter, FilterPageable pageable);
	
	/**
	 * 根据指定条件查询一条记录
	 * 
	 * @param filter 指定过滤条件
	 * @return 实体对象
	 */
	default Optional<T> findOne(FilterRequest filter) {
		return findOne(filter, null);
	}
	
	/**
	 * 根据指定条件查询一条记录
	 * 
	 * @param filter 指定过滤条件
	 * @param sort 指定排序条件
	 * @return 实体对象
	 */
	Optional<T> findOne(FilterRequest filter, FilterSort sort);
	
	/**
	 * 根据指定条件删除记录
	 * 
	 * @param filter 指定过滤条件
	 * @return 删除数量
	 */
	Long delete(FilterRequest filter);
	
	/**
	 * 根据实体 ID 查询实体对象
	 * 
	 * @param id 实体ID
	 * @return 实体对象的 Optional 对象
	 */
	Optional<T> findById(ID id);
	
	/**
	 * 删除指定 ID 的实体对象
	 * 
	 * @param id 实体ID
	 * @return 被删除实体对象
	 */
	T deleteEntity(ID id);
	
	/**
	 * 保存或更新指定实体对象
	 * 
	 * @param entity 实体对象
	 * @return 实体对象
	 */
	T saveOrUpdate(T entity);
	
	/**
	 * 根据指定过滤条件批量更新指定属性值
	 * 
	 * @param filter 指定过滤条件
	 * @param updateMap 需要更新的属性
	 */
	void batchUpdate(FilterRequest filter, Map<String, Object> updateMap);
	
}
