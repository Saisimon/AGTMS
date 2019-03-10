package net.saisimon.agtms.core.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
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
	default Boolean exists(FilterRequest filter) {
		Long count = count(filter);
		return count == null ? null : count > 0;
	}
	
	/**
	 * 根据指定条件查询列表
	 * 
	 * @param filter 指定过滤条件
	 * @return 实体对象列表
	 */
	default List<T> findList(FilterRequest filter, String... properties) {
		return findList(filter, (FilterSort)null, properties);
	}
	
	/**
	 * 根据指定条件查询列表
	 * 
	 * @param filter 指定过滤条件
	 * @param sort 指定排序条件
	 * @return 实体对象列表
	 */
	List<T> findList(FilterRequest filter, FilterSort sort, String... properties);
	
	/**
	 * 根据指定条件查询列表
	 * 
	 * @param filter 指定过滤条件
	 * @param pageable 指定分页条件
	 * @return 实体对象列表
	 */
	List<T> findList(FilterRequest filter, FilterPageable pageable, String... properties);
	
	/**
	 * 根据指定条件查询分页信息
	 * 
	 * @param filter 指定过滤条件
	 * @param pageable 指定分页条件
	 * @return 实体对象分页对象
	 */
	Page<T> findPage(FilterRequest filter, FilterPageable pageable, String... properties);
	
	/**
	 * 根据指定条件查询一条记录
	 * 
	 * @param filter 指定过滤条件
	 * @return 实体对象
	 */
	default Optional<T> findOne(FilterRequest filter, String... properties) {
		return findOne(filter, null, properties);
	}
	
	/**
	 * 根据指定条件查询一条记录
	 * 
	 * @param filter 指定过滤条件
	 * @param sort 指定排序条件
	 * @return 实体对象
	 */
	Optional<T> findOne(FilterRequest filter, FilterSort sort, String... properties);
	
	/**
	 * 根据 ID 查询实体记录
	 * 
	 * @param id 实体ID
	 * @param sort 指定排序条件
	 * @return 实体对象
	 */
	default Optional<T> find(ID id) {
		if (id == null) {
			return Optional.empty();
		}
		FilterRequest filter = FilterRequest.build().and(Constant.ID, id);
		return findOne(filter, (FilterSort)null);
	}
	
	/**
	 * 根据指定条件删除记录
	 * 
	 * @param filter 指定过滤条件
	 * @return 删除数量
	 */
	Long delete(FilterRequest filter);
	
	/**
	 * 删除指定实体记录
	 * 
	 * @param entity 实体对象
	 */
	void delete(T entity);
	
	/**
	 * 保存或更新指定实体对象
	 * 
	 * @param entity 实体对象
	 * @return 实体对象
	 */
	T saveOrUpdate(T entity);
	
	/**
	 * 根据 ID 更新指定属性值
	 * 
	 * @param id 实体ID
	 * @param updateMap 需要更新的属性
	 */
	default void update(ID id, Map<String, Object> updateMap) {
		if (id == null || CollectionUtils.isEmpty(updateMap)) {
			return;
		}
		FilterRequest filter = FilterRequest.build().and(Constant.ID, id);
		batchUpdate(filter, updateMap);
	}
	
	/**
	 * 根据指定过滤条件批量更新指定属性值
	 * 
	 * @param filter 指定过滤条件
	 * @param updateMap 需要更新的属性
	 */
	void batchUpdate(FilterRequest filter, Map<String, Object> updateMap);
	
}
