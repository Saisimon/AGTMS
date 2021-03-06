package net.saisimon.agtms.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.dto.SimpleResult;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.dto.req.UserAuthParam;

@Slf4j
public abstract class AbstractControllerTest {
	
	@Autowired
	protected MockHttpSession session;
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected MessageSource messageSource;
	
	protected String sendPost(String uri, Map<String, String> param, UserToken token) throws Exception {
		return sendPost(uri, param, token, ResultUtils.SUCCESS_CODE);
	}
	
	protected String sendPost(String uri, Object body, UserToken token) throws Exception {
		return sendPost(uri, body, token, ResultUtils.SUCCESS_CODE);
	}
	
	protected String sendPost(String uri, Map<String, String> param, Object body, UserToken token) throws Exception {
		return sendPost(uri, param, body, token, ResultUtils.SUCCESS_CODE);
	}
	
	protected String sendPost(String uri, Map<String, String> param, UserToken token, int expectedCode) throws Exception {
		String json = send(uri, HttpMethod.POST, param , null, token, status().isOk());
		checkResult(json, expectedCode);
		return json;
	}
	
	protected String sendPost(String uri, Object body, UserToken token, int expectedCode) throws Exception {
		String json = send(uri, HttpMethod.POST, null , body, token, status().isOk());
		checkResult(json, expectedCode);
		return json;
	}
	
	protected String sendPost(String uri, Map<String, String> param, Object body, UserToken token, int expectedCode) throws Exception {
		String json = send(uri, HttpMethod.POST, param , body, token, status().isOk());
		checkResult(json, expectedCode);
		return json;
	}
	
	protected String sendPost(String uri, Map<String, String> param, UserToken token, ResultMatcher matcher) throws Exception {
		return send(uri, HttpMethod.POST, param , null, token, matcher);
	}
	
	protected String sendPost(String uri, Object body, UserToken token, ResultMatcher matcher) throws Exception {
		return send(uri, HttpMethod.POST, null , body, token, matcher);
	}
	
	protected String sendPost(String uri, Map<String, String> param, Object body, UserToken token, ResultMatcher matcher) throws Exception {
		return send(uri, HttpMethod.POST, param , body, token, matcher);
	}
	
	private void checkResult(String json, int expectedCode) {
		Result result = null;
		try {
			result = SystemUtils.fromJson(json, Result.class);
		} catch (Exception e) {}
		Assert.assertEquals(expectedCode, result == null ? -1 : result.getCode() == null ? -1 : result.getCode());
	}
	
	protected String send(String uri, HttpMethod method, Map<String, String> param, Object body, UserToken token, ResultMatcher matcher) throws Exception {
		MockHttpServletRequestBuilder builder = build(uri, method);
		builder.session(session);
		builder.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
		if (token != null) {
			builder.header(AuthUtils.AUTHORIZE_UID, token.getUserId());
			builder.header(AuthUtils.AUTHORIZE_TOKEN, token.getToken());
		}
		if (param != null) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				builder.param(entry.getKey(), entry.getValue());
			}
		}
		if (body != null) {
			builder.contentType(MediaType.APPLICATION_JSON).content(SystemUtils.toJson(body));
		}
		MvcResult mvcResult = mockMvc.perform(builder).andExpect(matcher).andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		if (log.isDebugEnabled()) {
			log.debug("URI: " + uri + ", RESULT: " + result);
		}
		return result;
	}
	
	protected void returnBinary(String uri, Map<String, String> param, Object body) throws Exception {
		returnBinary(uri, param, body, status().isOk());
	}
	
	protected void returnBinary(String uri, Map<String, String> param, Object body, ResultMatcher matcher) throws Exception {
		MockHttpServletRequestBuilder builder = build(uri, HttpMethod.GET);
		builder.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
		if (param != null) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				builder.param(entry.getKey(), entry.getValue());
			}
		}
		if (body != null) {
			builder.contentType(MediaType.APPLICATION_JSON).content(SystemUtils.toJson(body));
		}
		mockMvc.perform(builder).andExpect(matcher);
		if (log.isDebugEnabled()) {
			log.debug("URI: " + uri);
		}
	}
	
	protected String sendMultipart(String uri, Map<String, Object> param, UserToken token, MockMultipartFile... files) throws Exception {
		MockMultipartHttpServletRequestBuilder builder = multipart(uri);
		builder.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
		if (token != null) {
			builder.header(AuthUtils.AUTHORIZE_UID, token.getUserId());
			builder.header(AuthUtils.AUTHORIZE_TOKEN, token.getToken());
		}
		if (param != null) {
			for (Map.Entry<String, Object> entry : param.entrySet()) {
				Object value = entry.getValue();
				if (value == null) {
					continue;
				}
				if (value instanceof Collection) {
					Collection<?> collection = (Collection<?>) value;
					String[] strs = new String[collection.size()];
					int i = 0;
					for (Object obj : collection) {
						if (obj != null) {
							strs[i] = obj.toString();
						}
						i++;
					}
					builder.param(entry.getKey(), strs);
				} else {
					builder.param(entry.getKey(), value.toString());
				}
			}
		}
		if (files != null) {
			for (MockMultipartFile file : files) {
				builder.file(file);
			}
		}
		MvcResult mvcResult = mockMvc.perform(builder).andExpect(status().isOk()).andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		if (log.isDebugEnabled()) {
			log.debug("URI: " + uri + ", RESULT: " + result);
		}
		return result;
	}
	
	protected String buildString(int size) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			builder.append('a');
		}
		return builder.toString();
	}
	
	protected void logout(UserToken token) throws Exception {
		sendPost("/user/logout", null, token, 0);
	}
	
	protected UserToken login(String name, String password) throws Exception {
		UserAuthParam param = new UserAuthParam();
		param.setName(name);
		param.setPassword(password);
		String json = sendPost("/user/auth", param, null, 0);
		@SuppressWarnings("unchecked")
		SimpleResult<UserToken> simpleResult = SystemUtils.fromJson(json, SimpleResult.class, UserToken.class);
		return simpleResult.getData();
	}
	
	private MockHttpServletRequestBuilder build(String uri, HttpMethod method) {
		MockHttpServletRequestBuilder builder = null;
		switch (method) {
		case GET:
			builder = get(uri);
			break;
		case OPTIONS:
			builder = options(uri);
			break;
		case HEAD:
			builder = head(uri);
			break;
		case PUT:
			builder = put(uri);
			break;
		case DELETE:
			builder = delete(uri);
			break;
		case PATCH:
			builder = patch(uri);
			break;
		default:
			builder = post(uri);
			break;
		}
		return builder;
	}
	
	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
	
}
