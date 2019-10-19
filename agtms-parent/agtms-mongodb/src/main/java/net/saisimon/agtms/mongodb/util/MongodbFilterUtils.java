package net.saisimon.agtms.mongodb.util;

import static net.saisimon.agtms.core.constant.Constant.Operator.EXISTS;
import static net.saisimon.agtms.core.constant.Constant.Operator.GT;
import static net.saisimon.agtms.core.constant.Constant.Operator.GTE;
import static net.saisimon.agtms.core.constant.Constant.Operator.IN;
import static net.saisimon.agtms.core.constant.Constant.Operator.LNREGEX;
import static net.saisimon.agtms.core.constant.Constant.Operator.LREGEX;
import static net.saisimon.agtms.core.constant.Constant.Operator.LT;
import static net.saisimon.agtms.core.constant.Constant.Operator.LTE;
import static net.saisimon.agtms.core.constant.Constant.Operator.NE;
import static net.saisimon.agtms.core.constant.Constant.Operator.NIN;
import static net.saisimon.agtms.core.constant.Constant.Operator.NREGEX;
import static net.saisimon.agtms.core.constant.Constant.Operator.REGEX;
import static net.saisimon.agtms.core.constant.Constant.Operator.RNREGEX;
import static net.saisimon.agtms.core.constant.Constant.Operator.RREGEX;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.filter.FilterParam;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * Mongodb 过滤工具类
 * 
 * @author saisimon
 *
 */
public class MongodbFilterUtils {
	
	private MongodbFilterUtils() {}
	
	public static Query query(FilterRequest filterRequest) {
		Criteria criteria = criteria(filterRequest);
		if (criteria != null) {
			return Query.query(criteria);
		}
		return new Query();
	}
	
	private static Criteria criteria(FilterRequest filterRequest) {
		if (filterRequest == null) {
			return null;
		}
		Criteria criteria = new Criteria();
		if (!CollectionUtils.isEmpty(filterRequest.getAndFilters())) {
			List<Criteria> andCriterias = parseFilters(filterRequest.getAndFilters());
			if (!CollectionUtils.isEmpty(andCriterias)) {
				criteria.andOperator(andCriterias.toArray(new Criteria[andCriterias.size()]));
			}
		}
		if (!CollectionUtils.isEmpty(filterRequest.getOrFilters())) {
			List<Criteria> orCriterias = parseFilters(filterRequest.getOrFilters());
			if (!CollectionUtils.isEmpty(orCriterias)) {
				criteria.orOperator(orCriterias.toArray(new Criteria[orCriterias.size()]));
			}
		}
		return criteria;
	}
	
	private static List<Criteria> parseFilters(List<FilterRequest> filters) {
		List<Criteria> criterias = new ArrayList<>();
		for (FilterRequest filter : filters) {
			if (filter.getClass() == FilterParam.class) {
				FilterParam param = (FilterParam)filter;
				Criteria criteria = criteria(param);
				if (criteria != null) {
					criterias.add(criteria);
				}
			} else {
				criterias.add(criteria(filter));
			}
		}
		return criterias;
	}
	
	private static Criteria criteria(FilterParam param) {
		if (param == null) {
			return null;
		}
		Criteria criteria = null;
		String key = param.getKey();
		if (Constant.ID.equalsIgnoreCase(key)) {
			key = Constant.MONGODBID;
		}
		Object value = DomainUtils.parseFieldValue(param.getValue(), param.getType());
		if (SystemUtils.isNotBlank(key) && value != null) {
			criteria = Criteria.where(key);
			switch (param.getOperator()) {
			case LT:
				criteria.lt(value);
				break;
			case GT:
				criteria.gt(value);
				break;
			case LTE:
				criteria.lte(value);
				break;
			case GTE:
				criteria.gte(value);
				break;
			case LREGEX:
				criteria.regex(Pattern.compile(value.toString() + "$", Pattern.CASE_INSENSITIVE));
				break;
			case RREGEX:
				criteria.regex(Pattern.compile("^" + value.toString(), Pattern.CASE_INSENSITIVE));
				break;
			case REGEX:
				criteria.regex(Pattern.compile(Pattern.quote(value.toString()), Pattern.CASE_INSENSITIVE));
				break;
			case LNREGEX:
				criteria.not().regex(Pattern.compile(value.toString()  + "$", Pattern.CASE_INSENSITIVE));
				break;
			case RNREGEX:
				criteria.not().regex(Pattern.compile("^" + value.toString(), Pattern.CASE_INSENSITIVE));
				break;
			case NREGEX:
				criteria.not().regex(Pattern.compile(Pattern.quote(value.toString()), Pattern.CASE_INSENSITIVE));
				break;
			case NE:
				criteria.ne(value);
				break;
			case EXISTS:
				criteria.exists("true".equals(value.toString()));
				break;
			case IN:
				if (value.getClass().isArray()) {
					criteria.in((Object[]) value);
				} else if (value instanceof Collection<?>) {
					criteria.in((Collection<?>) value);
				} else {
					criteria.is(value);
				}
				break;
			case NIN:
				if (value.getClass().isArray()) {
					criteria.nin((Object[]) value);
				} else if (value instanceof Collection<?>) {
					criteria.nin((Collection<?>) value);
				} else {
					criteria.ne(value);
				}
				break;
			default:
				criteria.is(value);
				break;
			}
		}
		return criteria;
	}
	
}
