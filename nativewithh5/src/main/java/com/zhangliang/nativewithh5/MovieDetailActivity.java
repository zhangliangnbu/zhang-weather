package com.zhangliang.nativewithh5;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.androidlib.utils.BaseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MovieDetailActivity extends BaseActivity {
	private WebView wvAds;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_detail);

		wvAds = (WebView) findViewById(R.id.wvAds);
		textView = (TextView) findViewById(R.id.tvId);
		textView.setText(String.valueOf(getIntent().getIntExtra("movieId", 0)));

		// 内置H5页面(如表格等)
		// 获取H5页面数据--处理数据--还原为H5页面
		// 获取H5页面数据
		String rowTemplate = getFromAssets("data1_template.html");
		String realData = getFromAssets("102.html");
		// 处理数据
		StringBuilder sbContent = new StringBuilder();
		ArrayList<MovieInfo> movieInfos = organizeMovieList();
		for(MovieInfo movieInfo : movieInfos) {
			String rowData;
			rowData = rowTemplate.replace("<name>", movieInfo.getName());
			rowData = rowData.replace("<price>", movieInfo.getPrice());
			sbContent.append(rowData);
		}
		realData = realData.replace("<data1DefinedByBaobao>", sbContent);
		// 还原为H5
		wvAds.loadData(realData, "text/html", "utf-8");



	}

	public ArrayList<MovieInfo> organizeMovieList() {
		ArrayList<MovieInfo> movieList = new ArrayList<MovieInfo>();
		movieList.add(new MovieInfo("Movie 1", "120"));
		movieList.add(new MovieInfo("Movie B", "80"));
		movieList.add(new MovieInfo("Movie III", "60"));

		return movieList;
	}

	public class MovieInfo {
		public MovieInfo(String name, String price) {
			this.name = name;
			this.price = price;
		}

		private String name;
		private String price;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}
	}

	public String getFromAssets(String fileName) {
		InputStreamReader inputStreamReader;
		BufferedReader bufferedReader = null;

		try {
			inputStreamReader= new InputStreamReader(getAssets().open(fileName));
			bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder result = new StringBuilder();
			for(String line; (line = bufferedReader.readLine()) !=null;) {
				result.append(line);
			}
			return result.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			BaseUtils.closeIO(bufferedReader);
		}
	}
}
