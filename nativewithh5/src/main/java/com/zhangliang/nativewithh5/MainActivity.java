package com.zhangliang.nativewithh5;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
	private Button btnShowAlert;
	private TextView tvMessage;
	private WebView wvAds;

	@SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnShowAlert = (Button) findViewById(R.id.btnShowAlert);
		tvMessage = (TextView) findViewById(R.id.tvMessage);
		wvAds = (WebView) findViewById(R.id.wvAds);

		wvAds.getSettings().setJavaScriptEnabled(true);
		// 资源文件路径必须为 main/assets/104.html, 不然不能识别
		wvAds.loadUrl("file:///android_asset/103.html");

		// native 操作 h5页面
		btnShowAlert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String color = "#00ee00";
				wvAds.loadUrl("javascript: changeColor('" + color + "');");
			}
		});

		// h5页面操作 native代码
		wvAds.addJavascriptInterface(new JSInteface1(), "baobao");
	}



	class JSInteface1 {
		@JavascriptInterface
		public void callAndroidMethod(int a, float b, String c, boolean d) {
			// 根据H5, 当前页面动作
			if (d) {
				String strMessage = "-" + (a + 1) + "-" + (b + 1) + "-" + c + "-" + d;

				new AlertDialog.Builder(MainActivity.this).setTitle("title").setMessage(strMessage).show();
			}
		}

		@JavascriptInterface
		public void gotoAnyWhere(String url) {
			// 根据H5, 跳转页面

			// 不用字典跳转
//			gotoAnyWhere(url);

			// 反射
//			gotoAnyWhere2(url);

			// 使用字典和反射
			gotoAnyWhere3(url);
		}
	}
}
