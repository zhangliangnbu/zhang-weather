package com.androidlib.net;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.androidlib.cache.CacheManager;
import com.androidlib.utils.BaseUtils;
import com.androidlib.utils.CollectionUtils;
import com.androidlib.utils.FrameConstants;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

/**
 * network request
 * Created by zhangliang on 16/9/9.
 */
public class HttpRequest implements Runnable{
	// TODO ??? 不应该写死,而是从外部传入
	private final static String cookiePath = "/data/data/com.zhangweather/cache/cookie";

	// 区分get还是post的枚举
	private static final String REQUEST_GET = "get";
	private static final String REQUEST_POST = "post";

	private URLData urlData = null;
	private RequestCallback requestCallback = null;
	private List<RequestParameter> parameterList = null;
	private String url = null;
	private String newUrl = null;// url + parameters
	private HttpUriRequest request = null;
	private HttpResponse response = null;
	private DefaultHttpClient httpClient;

	private Handler handler;

	//新增的头信息
	HashMap<String, String> headers;

	// 服务器与APP之间的时差, 用于校准
	private static long deltaBetweenServerAndClientTime;

	public HttpRequest(final URLData data, final List<RequestParameter> params,
	                   final RequestCallback callBack) {
		urlData = data;

		url = urlData.getUrl();
		this.parameterList = params;
		requestCallback = callBack;

		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}

		handler = new Handler();

		headers = new HashMap<>();
	}

	/**
	 * 获取HttpUriRequest请求
	 */
	public HttpUriRequest getRequest() {
		return request;
	}

	@Override
	public void run() {
		// 构造request
		switch (urlData.getNetType()) {
			case REQUEST_GET:
				createGetRequest();// 分两种格式的请求
				break;
			case REQUEST_POST:
				createPostRequest();
				break;
			default:
				return;
		}


		request.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		request.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

		// 添加header
		addHttpHeaders();

		// 添加Cookie到请求头中
		addCookie();

		if(newUrl != null) {
			Log.d("newUrl", newUrl);
		}
		Log.d("headers", "below===");
		logRequest();

		// response
		makeResponse();
	}

	private boolean isIrregularGetRequest = false;// 非正规的URL
	private void createGetRequest() {
		if(parameterList != null && parameterList.size() > 0) {
			if(isIrregularGetRequest) {
				int index = url.indexOf("sk/");
				String subUrl = url.substring(0, index + 3);
				String cityCode = parameterList.get(0).getValue();
				newUrl = subUrl.concat(cityCode).concat(".html");
			} else {
				// sort parameters
				sortParameters();
				StringBuffer stringBuffer = new StringBuffer();
				for(RequestParameter p : parameterList) {
					if(stringBuffer.length() == 0) {
						stringBuffer.append(p.getName() + "=" + BaseUtils.UrlEncodeUnicode(p.getValue()));
					} else {
						stringBuffer.append("&" + p.getName() + "=" + BaseUtils.UrlEncodeUnicode(p.getValue()));
					}
				}
				newUrl = url + "?" + stringBuffer.toString();
			}
		} else {
			newUrl = url;
		}

		//缓存判断(需要缓存则看是否有缓存)
		if(urlData.getExpires() > 0) {
			String content = CacheManager.getInstance().getFileCache(newUrl);
			Log.d("content", content + "");
			if(content != null) {
				handleNetworkOK(content);
				return;
			}
		}

		request = new HttpGet(newUrl);
	}

	private void createPostRequest() {
		newUrl = url;
		request = new HttpPost(newUrl);
		if(parameterList != null && parameterList.size() > 0) {
			List<BasicNameValuePair> list = new ArrayList<>();
			for(RequestParameter p : parameterList) {
				list.add(new BasicNameValuePair(p.getName(), p.getValue()));
			}
			try {
				((HttpPost)request).setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private void makeResponse() {

		try {
			// 响应
			response = httpClient.execute(request);
			logResponse(response);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				// 更新校准时间
				updateDeltaBetweenServerAndClientTime();

				// 根据gzip, 获取response string
				String strResponse = parseResponseToString();

				Log.d("strResponse", strResponse);
				String result = formatJsonResponse(strResponse);
				Log.d("strResponsef", result);

//				strResponse = "{'isError':false,'errorType':0,'errorMessage':'','result':{'city':'北京','cityid':'101010100','temp':'17','WD':'西南风','WS':'2级','SD':'54%','WSE':'2','time':'23:15','isRadar':'1','Radar':'JC_RADAR_AZ9010_JB','njd':'暂无实况','qy':'1016'}}";

				// 回调 失败或成功
				if(requestCallback != null) {
					final Response responseInJson = JSON.parseObject(result, Response.class);
					if(responseInJson.hasError()) {
						handleNetworkError(responseInJson.getErrorMessage());
					} else {
						handleNetworkOK(responseInJson.getResult());
						// 是否缓存
						if(urlData.getNetType().equals(REQUEST_GET) && urlData.getExpires() > 0) {
							CacheManager.getInstance().putFileCache(newUrl, responseInJson.getResult(), urlData.getExpires());
						}
						// 保存cookie
						saveCookie();
					}
				}

			} else {
				handleNetworkError("网络异常" + statusCode);
			}

		} catch (IOException e) {
			e.printStackTrace();
			handleNetworkError("网络异常");
		}
	}

	// 根据gzip, 获取response string
	private String parseResponseToString() {
		if(response == null || response.getEntity() == null) {
			return null;
		}

		Header encode = response.getEntity().getContentEncoding();
		ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();

		// 没有contentEncoding 或 没有gzip, 直接获取
		if(encode == null || TextUtils.isEmpty(encode.getValue()) || !encode.getValue().contains("gzip")) {
			try {
				response.getEntity().writeTo(byteArrayInputStream);
				return new String(byteArrayInputStream.toByteArray()).trim();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				BaseUtils.closeIO(byteArrayInputStream);
			}
		}

		// 有gzip
		InputStream is = null;
		try {
			is = new GZIPInputStream(response.getEntity().getContent());
			return BaseUtils.inputStreamToString(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			BaseUtils.closeIO(is);
		}
	}



	private void handleNetworkError(final String errorMsg) {
		if(handler == null || requestCallback == null) {
			return;
		}
		// handler所在的线程(UI线程)
		handler.post(new Runnable() {
			@Override
			public void run() {
				requestCallback.onFail(errorMsg);
			}
		});
	}

	private void handleNetworkOK(final String result) {
		if(handler == null || requestCallback == null) {
			return;
		}
		// handler所在的线程(UI线程)
		handler.post(new Runnable() {
			@Override
			public void run() {
				requestCallback.onSuccess(result);
			}
		});
	}

	/**
	 * be sure url唯一
	 */
	private void sortParameters() {
		Collections.sort(parameterList, new Comparator<RequestParameter>() {
			/**
			 * @return parameter 1 比较char, 大则返1; 2 都相等则比较长度, 长则返回1; 3 相等返回0; 其余都返回-1
			 */
			@Override
			public int compare(@NonNull RequestParameter lhs, @NonNull RequestParameter rhs) {
				if(TextUtils.isEmpty(lhs.getName())) {
					return -1;
				}
				if(TextUtils.isEmpty(rhs.getName())) {
					return 1;
				}

				String p1 = lhs.getName().toUpperCase();
				String p2 = rhs.getName().toUpperCase();

				// 获取最小长度
				int minlength;
				int flag = 0;
				if(p1.length() > p2.length()) {
					minlength = p2.length();
					flag = 1;
				} else if(p1.length() < p2.length()){
					minlength = p1.length();
					flag =-1;
				} else {
					minlength = p1.length();
					flag = 0;
				}

				// 比较
				char ch1, ch2;
				for (int i = 0; i < minlength; i ++) {
					ch1 = p1.charAt(i);
					ch2 = p2.charAt(i);
					if(ch1 > ch2) {
						return 1;
					} else if(ch1 < ch2) {
						return -1;
					}
				}

				return flag;
			}
		});
	}

	/**
	 * 转换返回的数据为具有errorCode的格式的数据(伪装成从服务器返回的协议数据)
	 */
	private String formatJsonResponse(String result) {
		StringBuilder stringBuilder = new StringBuilder();

		boolean isError = false;
		int errorType = 0;
		String errorMessage = "''";
		String res = "";
		if(result == null || "".equals(result)) {
			isError = true;
			errorType = 1;
			errorMessage = "暂无数据";
			res = "";
		} else {
			int index1 = result.indexOf(":{");
			int index2 = result.indexOf("}");
			res = result.substring(index1 + 1, index2 + 1);
		}

		stringBuilder.append("{");

		stringBuilder.append("'isError':");
		stringBuilder.append(isError);
		stringBuilder.append(",");

		stringBuilder.append("'errorType':");
		stringBuilder.append(errorType);
		stringBuilder.append(",");

		stringBuilder.append("'errorMessage':");
		stringBuilder.append(errorMessage);
		stringBuilder.append(",");

		stringBuilder.append("'result':");
		stringBuilder.append(res);
		stringBuilder.append("}");

		return stringBuilder.toString();
	}

	private synchronized void saveCookie() {
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		List<SerializableCookie> serializableCookies = null;

		if(!CollectionUtils.isEmpty(cookies)) {
			serializableCookies = new ArrayList<>();
			for(Cookie cookie : cookies) {
				serializableCookies.add(new SerializableCookie(cookie));
			}
		}

		BaseUtils.saveObject(cookiePath, serializableCookies);

	}

	private void addCookie() {
		List<SerializableCookie> cookies = null;
		Object object = BaseUtils.restoreObject(cookiePath);
		if(object != null) {
			cookies = (ArrayList<SerializableCookie>) object;
		}

		if(!CollectionUtils.isEmpty(cookies)) {
			BasicCookieStore basicCookieStore = new BasicCookieStore();
			basicCookieStore.addCookies(cookies.toArray(new Cookie[]{}));
			httpClient.setCookieStore(basicCookieStore);
		} else {
			httpClient.setCookieStore(null);
		}

	}

	/**
	 * TODO 设置额外的头信息, 应该从外部传入, 如直接传入hashmap
	 */
	private void addHttpHeaders() {
		if(request == null || CollectionUtils.isEmpty(headers)) {
			return;
		}

		headers.clear();
		headers.put(FrameConstants.ACCEPT_CHARSET, "UTF-8, *");
		headers.put(FrameConstants.USER_AGENT, "Zhang Weather App");
		headers.put(FrameConstants.ACCEPT_ENCODING, "gzip");

		for(Map.Entry<String, String> entry : headers.entrySet()) {
			if(entry.getKey() != null) {
				request.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	private void logRequest() {
		if(request == null) {
			return;
		}

		// TODO allHeaders size == 0 !!!
		// 可能是在执行过程中添加的, 但是手动添加的为什么没有添加上呢?
		Header[] allHeaders = request.getAllHeaders();
		for(Header header : allHeaders) {
			Log.d(header.getName(), header.getValue());
		}
	}

	private void logResponse(HttpResponse response) {
		Log.d("log", "response header");
		if(response == null) {
			return;
		}

		for(Header header : response.getAllHeaders()) {
			Log.d(header.getName(), header.getValue());
		}

	}

	private SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	private void updateDeltaBetweenServerAndClientTime() {
		if(response == null) {
			return;
		}

		Header header = response.getLastHeader("Date");
		if(header == null) {
			return;
		}

		String serverDate = header.getValue();//eg: Tue, 20 Sep 2016 07:09:45 GMT
		if(TextUtils.isEmpty(serverDate)) {
			return;
		}

		Date serverDateUAT = null;
		try {
			serverDateUAT = sdf.parse(serverDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));// 设置系统时间为中国时间
		deltaBetweenServerAndClientTime = serverDateUAT.getTime() + 8 * 60 * 60 * 1000 - System.currentTimeMillis();

		Log.d("serverDateLong", String.valueOf(serverDateUAT.getTime()));
		Log.d("systemDateLong", String.valueOf(System.currentTimeMillis()));
	}



}
