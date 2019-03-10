package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.repository.BaseRepository;

/**
 * 基础服务接口
 * 
 * @author saisimon
 *
 * @param <T> 实体类型
 * @param <ID> 实体 ID 的类型
 */
public interface BaseService<T, ID> {
	
	BaseRepository<T, ID> getRepository();
	
	/**
	 * 根据指定条件查询总数
	 * 
	 * @param filter 指定过滤条件
	 * @return 总数
	 */
	default Long count(FilterRequest filter) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.count(filter);
	}
	
	/**
	 * 根据指定条件判断是否存在
	 * 
	 * @param filter 指定过滤条件
	 * @return 存在返回 true， 否则返回 false
	 */
	default Boolean exists(FilterRequest filter) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.exists(filter);
	}
	
	/**
	 * 根据指定条件查询列表
	 * 
	 * @param filter 指定过滤条件
	 * @return 实体对象列表
	 */
	default List<T> findList(FilterRequest filter, String... properties) {
		return findList(filter, null, properties);
	}
	
	/**
	 * 根据指定条件查询列表
	 * 
	 * @param filter 指定过滤条件
	 * @param sort 指定排序条件
	 * @return 实体对象列表
	 */
	default List<T> findList(FilterRequest filter, FilterSort sort, String... properties) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.findList(filter, sort, properties);
	}
	
	/**
	 * 根据指定条件查询分页信息
	 * 
	 * @param filter 指定过滤条件
	 * @param pageable 指定分页条件
	 * @return 实体对象分页对象
	 */
	default Page<T> findPage(FilterRequest filter, FilterPageable pageable, String... properties) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.findPage(filter, pageable, properties);
	}
	
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
	default Optional<T> findOne(FilterRequest filter, FilterSort sort, String... properties) {
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.findOne(filter, sort, properties);
	}
	
	/**
	 * 根据指定条件删除记录
	 * 
	 * @param filter 指定过滤条件
	 * @return 删除数量
	 */
	default Long delete(FilterRequest filter) {
		Assert.notNull(filter, "filter can not be null");
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.delete(filter);
	}
	
	/**
	 * 根据实体 ID 查询实体对象
	 * 
	 * @param id 实体ID
	 * @return 实体对象的 Optional 对象
	 */
	default Optional<T> findById(ID id) {
		Assert.notNull(id, "id can not be null");
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.find(id);
	}
	
	/**
	 * 删除指定 ID 的实体对象
	 * 
	 * @param id 实体ID
	 * @return 被删除实体对象
	 */
	default void delete(T entity) {
		Assert.notNull(entity, "entity can not be null");
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		repository.delete(entity);
	}
	
	/**
	 * 保存或更新指定实体对象
	 * 
	 * @param entity 实体对象
	 * @return 实体对象
	 */
	default T saveOrUpdate(T entity) {
		Assert.notNull(entity, "entity can not be null");
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		return repository.saveOrUpdate(entity);
	}
	
	/**
	 * 根据 ID 更新指定属性值
	 * 
	 * @param id 实体ID
	 * @param updateMap 需要更新的属性
	 */
	default void update(ID id, Map<String, Object> updateMap) {
		Assert.notNull(id, "id can not be null");
		if (CollectionUtils.isEmpty(updateMap)) {
			return;
		}
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		repository.update(id, updateMap);
	}
	
	/**
	 * 根据指定过滤条件批量更新指定属性值
	 * 
	 * @param filter 指定过滤条件
	 * @param updateMap 需要更新的属性
	 */
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		if (CollectionUtils.isEmpty(updateMap)) {
			return;
		}
		BaseRepository<T, ID> repository = getRepository();
		Assert.notNull(repository, "need repository");
		repository.batchUpdate(filter, updateMap);
	}
	
}
