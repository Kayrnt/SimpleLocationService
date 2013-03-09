/*
 *  Copyright 2013 Kayrnt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.simplelocationservice;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends GeoLocatedActivity implements ILocationListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.this.startActivity
				(new Intent(MainActivity.this, SecondActivity.class));
			}
		});

	}


	@Override
	public void onResume() {
		super.onResume();
		LocationServiceHelper.getInstance(getApplicationContext()).addListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		LocationServiceHelper.getInstance(getApplicationContext()).removeListener(this);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		Log.i("MainActivity", "location is : "+location);
	}


}
