package com.androidlib.net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangliang on 16/9/12.
 */
public class RequestManager {
	ArrayList<HttpRequest> requestList = null;

	public RequestManager() {
		requestList = new ArrayList<>();
	}

	// crate request
	public HttpRequest createRequest(URLData urlData, List<RequestParameter> parameters, RequestCallback requestCallback) {
		HttpRequest request = new HttpRequest(urlData, parameters, requestCallback);
		addRequest(request);
		return request;
	}

	// create request
	public HttpRequest createRequst(URLData urlData, RequestCallback requestCallback) {
		return createRequest(urlData, null, requestCallback);
	}

	// add request
	public void addRequest(HttpRequest request) {
		if(requestList == null) {
			requestList = new ArrayList<>();
		}
		requestList.add(request);
	}

	// cancel all
	public void cancelRequest() {
		if(requestList == null || requestList.isEmpty()) {
			return;
		}

		Iterator<HttpRequest> iterator = requestList.iterator();
		for(;iterator.hasNext();) {
			HttpRequest request = iterator.next();
			try {
				request.getRequest().abort();
				iterator.remove();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}






}
