package com.fine.dynamic.code.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
	private static Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);
	private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
				//.sslSocketFactory(sslSocketFactory(), x509TrustManager())
				.retryOnConnectionFailure(true)
				.connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10,TimeUnit.SECONDS)
				.build();

//	@Autowired
//	public OkHttpUtil(OkHttpClient okHttpClient) {
//		OkHttpUtil.okHttpClient = okHttpClient;
//	}

	public static String get(String url, Map<String, String> param) {
		return get(url, param, null);
	}
	
	public static String get(String url, Map<String, String> params, Map<String, String> headers) {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		if(params!=null && params.size()>0) {
			params.forEach((k, v) -> {urlBuilder.addEncodedQueryParameter(k, v);});
		}
		HttpUrl httpUrl = urlBuilder.build();
		
		Request.Builder requestBuilder = new Request.Builder().url(httpUrl);
		if(headers!=null && headers.size()>0) {
			headers.forEach((k, v) -> {requestBuilder.header(k, v);});
		}
		try (Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				logger.error("Response.toString:"+response.toString());
			}
			return ""; 
        } catch (IOException e) {
			logger.error("HttpUrl.toString:"+httpUrl.toString()+";\r\n IOException.printStackTrace:"+e.getMessage());
        	return "";
		}
	}

	public static String post(String url, Map<String, String> params) {
		return post(url, params, null);
	}
		
	public static String post(String url, Map<String, String> params, Map<String, String> headers) {
		Request.Builder requestBuilder = new Request.Builder().url(url);
		if(headers!=null && headers.size()>0) {
			headers.forEach((k, v) -> {requestBuilder.header(k, v);});
		}
		
		FormBody.Builder formBuilder = new FormBody.Builder();
		if(params != null && params.size() > 0) {
			params.forEach((k, v) -> {formBuilder.add(k, v);});
		}
		
		try (Response response = okHttpClient.newCall(requestBuilder.post(formBuilder.build()).build()).execute()) {
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				logger.error("Response.toString:"+response.toString());
			}
			return ""; 
        } catch (IOException e) {
			logger.error("url:"+url+";\r\n IOException.printStackTrace:"+e.getMessage());
        	return "";
		}
	}

	public static String postJson(String url, String json) {
		return postJson(url, json, null);
	}
	
	public static String postJson(String url, String json, Map<String, String> headers) {
		Request.Builder requestBuilder = new Request.Builder().url(url);
		if(headers!=null && headers.size()>0) {
			headers.forEach((k, v) -> {requestBuilder.header(k, v);});
		}
		
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
		
		try (Response response = okHttpClient.newCall(requestBuilder.post(requestBody).build()).execute()) {
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				logger.error("Response.toString:"+response.toString()+":"+response.body().string());
			}
			return ""; 
        } catch (IOException e) {
			logger.error("url:"+url+";\r\n IOException.printStackTrace:"+e.getMessage());
        	return "";
		}
	}
	public static String postJsonRetAll(String url, String json, Map<String, String> headers) {
		Request.Builder requestBuilder = new Request.Builder().url(url);
		if(headers!=null && headers.size()>0) {
			headers.forEach((k, v) -> {requestBuilder.header(k, v);});
		}

		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

		try (Response response = okHttpClient.newCall(requestBuilder.post(requestBody).build()).execute()) {
			boolean success = response.isSuccessful();
			JSONObject json1 = JSONObject.parseObject(response.body().string());
			json1.put("res_success", success);
			return json1.toString();
		} catch (IOException e) {
			logger.error("url:"+url+";\r\n IOException.printStackTrace:"+e.getMessage());
			return "";
		}
	}
	public static String postXml(String url, String xml) {
		return postXml(url, xml, null);
	}
	
	public static String postXml(String url, String xml, Map<String, String> headers) {
		Request.Builder requestBuilder = new Request.Builder().url(url);
		if(headers!=null && headers.size()>0) {
			headers.forEach((k, v) -> {requestBuilder.header(k, v);});
		}
		
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), xml);
		
		try (Response response = okHttpClient.newCall(requestBuilder.post(requestBody).build()).execute()) {
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				logger.error("Response.toString:"+response.toString());
			}
			return ""; 
        } catch (IOException e) {
			logger.error("url:"+url+";\r\n IOException.printStackTrace:"+e.getMessage());
        	return "";
		}
	}
}