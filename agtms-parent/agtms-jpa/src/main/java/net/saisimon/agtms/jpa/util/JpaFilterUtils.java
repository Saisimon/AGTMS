package net.saisimon.agtms.jpa.util;

import static net.saisimon.agtms.core.constant.Constant.Operator.ALL;
import static net.saisimon.agtms.core.constant.Constant.Operator.EXISTS;
import static net.saisimon.agtms.core.constant.Constant.Operator.GT;
import static net.saisimon.agtms.core.constant.Constant.Operator.GTE;
import static net.saisimon.agtms.core.constant.Constant.Operator.IN;
import static net.saisimon.agtms.core.constant.Constant.Operator.LT;
import static net.saisimon.agtms.core.constant.Constant.Operator.LTE;
import static net.saisimon.agtms.core.constant.Constant.Operator.NE;
import static net.saisimon.agtms.core.constant.Constant.Operator.NIN;
import static net.saisimon.agtms.core.constant.Constant.Operator.REGEX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.filter.FilterParam;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.jpa.domain.Statement;

public class JpaFilterUtils {
	
	public static Statement where(FilterRequest filterRequest) {
		if (filterRequest == null) {
			return null;
		}
		Statement statement = new Statement();
		StringBuilder where = new StringBuilder();
		if (!CollectionUtils.isEmpty(filterRequest.getAndFilters())) {
			Statement filter = filter(filterRequest.getAndFilters(), true);
			where.append(filter.getExpression());
			statement.addArgs(filter.getArgs());
		}
		if (!CollectionUtils.isEmpty(filterRequest.getOrFilters())) {
			if (where.length() != 0) {
				where.append(" OR ");
			}
			Statement filter = filter(filterRequest.getOrFilters(), false);
			where.append(filter.getExpression());
			statement.addArgs(filter.getArgs());
		}
		statement.setExpression(where);
		return statement;
	}
	
	public static String orderby(FilterSort filterSort) {
		StringBuilder sort = new StringBuilder();
		if (filterSort != null) {
			Map<String, String> sortMap = filterSort.getSortMap();
			if (CollectionUtils.isEmpty(sortMap)) {
				sort.append(Constant.ID);
			} else {
				for (Entry<String, String> entry : sortMap.entrySet()) {
					if (sort.length() != 0) {
						sort.append(", ");
					}
					sort.append(entry.getKey()).append(" ").append(entry.getValue().toUpperCase());
				}
			}
		} else {
			sort.append(Constant.ID);
		}
		return sort.toString();
	}
	
	public static <T> Specification<T> specification(FilterRequest filterRequest, String... properties) {
		return new Specification<T>() {
			
			private static final long serialVersionUID = -2129612923958716561L;
			
			@SuppressWarnings({ "rawtypes"})
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (properties != null && properties.length > 0) {
					Path[] paths = new Path[properties.length];
					for (int i = 0; i < properties.length; i++) {
						paths[i] = root.get(properties[i]);
					}
					query.multiselect(paths);
				}
				if (filterRequest == null) {
					return query.getRestriction();
				}
				List<Predicate> predicates = new ArrayList<>();
				if (!CollectionUtils.isEmpty(filterRequest.getAndFilters())) {
					List<Predicate> ps = parseFilters(root, query, criteriaBuilder, filterRequest.getAndFilters());
					if (!CollectionUtils.isEmpty(ps)) {
						predicates.add(criteriaBuilder.and(ps.toArray(new Predicate[ps.size()])));
					}
				}
				if (!CollectionUtils.isEmpty(filterRequest.getOrFilters())) {
					List<Predicate> ps = parseFilters(root, query, criteriaBuilder, filterRequest.getOrFilters());
					if (!CollectionUtils.isEmpty(ps)) {
						predicates.add(criteriaBuilder.or(ps.toArray(new Predicate[ps.size()])));
					}
				}
				if (predicates.size() > 0) {
					query.where(predicates.toArray(new Predicate[predicates.size()]));
				}
				return query.getRestriction();
			}
			
			private List<Predicate> parseFilters(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, List<FilterRequest> filters) {
				List<Predicate> ps = new ArrayList<>();
				for (FilterRequest filter : filters) {
					if (filter.getClass() == FilterParam.class) {
						FilterParam param = (FilterParam)filter;
						Predicate p = predicate(root, criteriaBuilder, param);
						if (p != null) {
							ps.add(p);
						}
					} else {
						Specification<T> specification = specification(filter);
						ps.add(specification.toPredicate(root, query, criteriaBuilder));
					}
				}
				return ps;
			}
		};
	}
	
	private static <T> Predicate predicate(Root<T> root, CriteriaBuilder criteriaBuilder, FilterParam param) {
		if (param == null) {
			return null;
		}
		Predicate predicate = null;
		String key = param.getKey();
		Object value = param.getValue();
		String operator = param.getOperator();
		String type = param.getType();
		value = DomainGenerater.parseFieldValue(value, type);
		if (value != null) {
			switch (operator) {
				case LT:
					if (value instanceof Date) {
						predicate = criteriaBuilder.lessThan(buildExpression(root, key), (Date) value);
					} else if (value instanceof Number) {
						predicate = criteriaBuilder.lt(buildExpression(root, key), (Number)value);
					} else {
						predicate = criteriaBuilder.lessThan(buildExpression(root, key), value.toString());
					}
					break;
				case GT:
					if (value instanceof Date) {
						predicate = criteriaBuilder.greaterThan(buildExpression(root, key), (Date) value);
					} else if (value instanceof Number) {
						predicate = criteriaBuilder.gt(buildExpression(root, key), (Number)value);
					} else {
						predicate = criteriaBuilder.greaterThan(buildExpression(root, key), value.toString());
					}
					break;
				case LTE:
					if (value instanceof Date) {
						predicate = criteriaBuilder.lessThanOrEqualTo(buildExpression(root, key), (Date) value);
					} else if (value instanceof Number) {
						predicate = criteriaBuilder.le(buildExpression(root, key), (Number)value);
					} else {
						predicate = criteriaBuilder.lessThanOrEqualTo(buildExpression(root, key), value.toString());
					}
					break;
				case GTE:
					if (value instanceof Date) {
						predicate = criteriaBuilder.greaterThanOrEqualTo(buildExpression(root, key), (Date) value);
					} else if (value instanceof Number) {
						predicate = criteriaBuilder.ge(buildExpression(root, key), (Number)value);
					} else {
						predicate = criteriaBuilder.greaterThanOrEqualTo(buildExpression(root, key), value.toString());
					}
					break;
				case REGEX:
					predicate = criteriaBuilder.like(buildExpression(root, key), "%" + value.toString() + "%");
					break;
				case NE:
					predicate = criteriaBuilder.notEqual(buildExpression(root, key), value);
					break;
				case EXISTS:
					predicate = criteriaBuilder.isNotEmpty(buildExpression(root, key));
					break;
				case IN:
				case ALL:
					if (value.getClass().isArray()) {
						predicate = buildExpression(root, key).in(Arrays.asList((Object[])value));
					} else if (value instanceof Collection<?>) {
						predicate = buildExpression(root, key).in((Collection<?>) value);
					} else {
						predicate = criteriaBuilder.equal(buildExpression(root, key), value);
					}
					break;
				case NIN:
					if (value.getClass().isArray()) {
						predicate = criteriaBuilder.not(buildExpression(root, key).in(Arrays.asList((Object[])value)));
					} else if (value instanceof Collection<?>) {
						predicate = criteriaBuilder.not(buildExpression(root, key).in((Collection<?>) value));
					} else {
						predicate = criteriaBuilder.notEqual(buildExpression(root, key), value);
					}
					break;
				default:
					predicate = criteriaBuilder.equal(buildExpression(root, key), value);
					break;
			}
		}
		return predicate;
	}
	
	private static <Y, T> Expression<Y> buildExpression(Root<T> root, String key) {
		String[] ks = key.split("\\.");
		if (ks.length > 1) {
			Join<?, ?> join = null;
			for (int i = 0; i < ks.length - 1; i++) {
				join = root.join(ks[i]);
			}
			return join.get(ks[ks.length - 1]);
		} else {
			return root.get(key);
		}
	}
	
	private static Statement filter(List<FilterRequest> filters, boolean and) {
		Statement statement = new Statement();
		StringBuilder sql = new StringBuilder();
		sql.append("( ");
		for (FilterRequest filter : filters) {
			if (sql.length() != 2) {
				if (and) {
					sql.append(" AND ");
				} else {
					sql.append(" OR ");
				}
			}
			if (filter.getClass() == FilterParam.class) {
				Statement expression = expression((FilterParam)filter);
				sql.append(expression.getExpression());
				statement.addArgs(expression.getArgs());
			} else {
				Statement where = where(filter);
				sql.append(where.getExpression());
				statement.addArgs(where.getArgs());
			}
		}
		sql.append(" )");
		statement.setExpression(sql);
		return statement;
	}
	
	private static Statement expression(FilterParam param) {
		Statement statement = new Statement();
		if (param == null) {
			return statement;
		}
		StringBuilder expression = new StringBuilder();
		String key = param.getKey();
		Object value = param.getValue();
		String operator = param.getOperator();
		String type = param.getType();
		value = DomainGenerater.parseFieldValue(value, type);
		if (value != null) {
			switch (operator) {
				case LT:
					expression.append(key).append(" < ?");
					statement.addArgs(value);
					break;
				case GT:
					expression.append(key).append(" > ?");
					statement.addArgs(value);
					break;
				case LTE:
					expression.append(key).append(" <= ?");
					statement.addArgs(value);
					break;
				case GTE:
					expression.append(key).append(" >= ?");
					statement.addArgs(value);
					break;
				case REGEX:
					expression.append(key).append(" LIKE ?");
					statement.addArgs("%" + value + "%");
					break;
				case NE:
					expression.append(key).append(" <> ?");
					statement.addArgs(value);
					break;
				case EXISTS:
					expression.append(key).append(" IS NOT NULL");
					break;
				case IN:
				case ALL:
					if (value.getClass().isArray()) {
						StringBuilder in = new StringBuilder();
						Object[] arr = (Object[]) value;
						for (Object val : arr) {
							statement.addArgs(val);
							if (in.length() != 0) {
								in.append(", ");
							}
							in.append("?");
						}
						expression.append(key).append(key).append(" IN (").append(in).append(")");
					} else if (value instanceof Collection<?>) {
						StringBuilder in = new StringBuilder();
						Collection<?> col = (Collection<?>) value;
						for (Object val : col) {
							statement.addArgs(val);
							if (in.length() != 0) {
								in.append(", ");
							}
							in.append("?");
						}
						expression.append(key).append(" IN (").append(in).append(")");
					} else {
						expression.append(key).append(" = ?");
						statement.addArgs(value);
					}
					break;
				case NIN:
					if (value.getClass().isArray()) {
						StringBuilder in = new StringBuilder();
						Object[] arr = (Object[]) value;
						for (Object val : arr) {
							statement.addArgs(val);
							if (in.length() != 0) {
								in.append(", ");
							}
							in.append("?");
						}
						expression.append(key).append(" NOT IN (").append(in).append(")");
					} else if (value instanceof Collection<?>) {
						StringBuilder in = new StringBuilder();
						Collection<?> col = (Collection<?>) value;
						for (Object val : col) {
							statement.addArgs(val);
							if (in.length() != 0) {
								in.append(", ");
							}
							in.append("?");
						}
						expression.append(key).append(" NOT IN (").append(in).append(")");
					} else {
						expression.append(key).append(" <> ?");
						statement.addArgs(value);
					}
					break;
				default:
					expression.append(key).append(" = ?");
					statement.addArgs(value);
					break;
			}
		}
		statement.setExpression(expression);
		return statement;
	}
	
}
