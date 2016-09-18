package com.androidlib.net;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.androidlib.cache.CacheManager;
import com.androidlib.utils.BaseUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * network request
 * Created by zhangliang on 16/9/9.
 */
public class HttpRequest implements Runnable{
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
		if(urlData.getNetType().equals(REQUEST_GET)) {
			if(parameterList != null && parameterList.size() > 0) {
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

		} else if(urlData.getNetType().equals(REQUEST_POST)) {
			request = new HttpPost(url);
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
		} else {
			return;
		}

		request.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		request.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);


		try {
			// 响应
			response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
				response.getEntity().writeTo(byteArrayInputStream);
				String strResponse = new String(byteArrayInputStream.toByteArray()).trim();
				Log.d("strResponse", strResponse);

				strResponse = "{'isError':false,'errorType':0,'errorMessage':'','result':{'city':'北京','cityid':'101010100','temp':'17','WD':'西南风','WS':'2级','SD':'54%','WSE':'2','time':'23:15','isRadar':'1','Radar':'JC_RADAR_AZ9010_JB','njd':'暂无实况','qy':'1016'}}";

				// 回调 失败或成功
				if(requestCallback != null) {
					final Response responseInJson = JSON.parseObject(strResponse, Response.class);
					if(responseInJson.hasError()) {
						handleNetworkError(responseInJson.getErrorMessage());
					} else {
						handleNetworkOK(responseInJson.getResult());
						// 是否缓存
						if(urlData.getNetType().equals(REQUEST_GET) && urlData.getExpires() > 0) {
							CacheManager.getInstance().putFileCache(newUrl, responseInJson.getResult(), urlData.getExpires());
						}
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
}
