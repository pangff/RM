package com.pangff.rm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class RMActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rm);
	}

}
