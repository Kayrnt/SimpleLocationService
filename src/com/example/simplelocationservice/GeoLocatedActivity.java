package com.example.simplelocationservice;

import android.app.Activity;

public class GeoLocatedActivity extends Activity{

	@Override
	public void onResume() {
		super.onResume();
		LocationServiceHelper.getInstance(getApplicationContext()).onResume();
	} 
	
	@Override
	public void onPause() {
		super.onPause();
		LocationServiceHelper.getInstance(getApplicationContext()).onPause();
	} 
}
