package com.zhangweather.activity.personcenter;

import android.os.Bundle;
import android.widget.TextView;

import com.zhangweather.R;
import com.zhangweather.base.AppBaseActivity;
import com.zhangweather.engine.User;

/**
 * Created by zhangliang on 16/9/19.
 */

public class PersonCenterActivity extends AppBaseActivity {
	@Override
	protected void initVariables() {

	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_personcenter);

		TextView tvPersonCenter = (TextView)findViewById(R.id.tvPersonCenter);
		tvPersonCenter.setText(
				User.getInstance().getUserName());
	}

	@Override
	protected void loadData() {

	}
}
