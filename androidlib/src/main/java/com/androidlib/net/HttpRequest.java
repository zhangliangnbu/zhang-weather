package com.androidlib.net;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
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
import java.util.List;

/**
 * network request
 * Created by zhangliang on 16/9/9.
 */
public class HttpRequest implements Runnable{

	private URLData urlData = null;
	private RequestCallback requestCallback = null;
	private List<RequestParameter> parameter = null;
	private String url = null;
	private HttpUriRequest request = null;
	private HttpResponse response = null;
	private DefaultHttpClient httpClient;

	protected Handler handler;

	public HttpRequest(final URLData data, final List<RequestParameter> params,
	                   final RequestCallback callBack) {
		urlData = data;

		url = urlData.getUrl();
		this.parameter = params;
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
		if(urlData.getNetType().equals("get")) {
			if(parameter != null && parameter.size() > 0) {
				StringBuffer stringBuffer = new StringBuffer();
				for(RequestParameter p : parameter) {
					if(stringBuffer.length() == 0) {
						stringBuffer.append(p.getName() + "=" + BaseUtils.UrlEncodeUnicode(p.getValue()));
					} else {
						stringBuffer.append("&" + p.getName() + "=" + BaseUtils.UrlEncodeUnicode(p.getValue()));
					}
				}
				request = new HttpGet(url + "?" + stringBuffer.toString());
			} else {
				request = new HttpGet(url);
			}

		} else if(urlData.getNetType().equals("post")) {
			request = new HttpPost(url);
			if(parameter != null && parameter.size() > 0) {
				List<BasicNameValuePair> list = new ArrayList<>();
				for(RequestParameter p : parameter) {
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
}
