package edu.ualr.cpsc4367.simplereceiver;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class MainActivity extends Activity {
	SimpleReceiver intentReceiver = new SimpleReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
		intentfilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		registerReceiver(intentReceiver, intentfilter);
		Log.d("Activity", "onCreate()");
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(intentReceiver);
		super.onDestroy();
	}

}
