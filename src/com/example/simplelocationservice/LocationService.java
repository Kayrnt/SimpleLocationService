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


import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

public class LocationService extends Service implements LocationListener {


	// Constants 
	private static final int MINIMUM_DISTANCE_NETWORK = 50;
	private static final int MINIMUM_DISTANCE_GPS = 10;
	private static final int SERVICE_RUNNING_TIMEOUT = 2000;
	// actualisation every X ms
	private static final int MINIMUM_ELAPSED_NETWORK =5*1000;
	private static final int MINIMUM_ELAPSED_GPS = 5*1000;

	private LocationManager locationManager = null;
	private Location lastFix = null;
	private final IBinder mBinder = new LocationBinder();
	private Runnable stopServiceRunnable = null;
	private Handler delayedHandler = null;
	private boolean isRunning = false;

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocationBinder extends Binder {
		public LocationService getService() {
			// Return this instance of LocalService so clients can call public methods
			return LocationService.this;
		}
	}

	// start geolocation
	public void start() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				enableMyLocation();
			}
		}).run();
		delayedHandler.removeCallbacks(stopServiceRunnable);
	}

	// stop geolocation
	public void stop() {
		if(isRunning){
			delayedHandler.postDelayed(stopServiceRunnable, SERVICE_RUNNING_TIMEOUT);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		stopServiceRunnable = new Runnable() {

			@Override
			public void run() {
				stopSelf();
				isRunning = false;
			}
		};
		delayedHandler = new Handler();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		disableMyLocation();
	}

	public synchronized void enableMyLocation() {
		List<String> providers = locationManager.getProviders(false);
		if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, MINIMUM_ELAPSED_NETWORK,
					MINIMUM_DISTANCE_NETWORK, this);
		}
		if (providers.contains(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MINIMUM_ELAPSED_GPS,
					MINIMUM_DISTANCE_GPS, this);
		}
		isRunning = true;
	}

	public synchronized void disableMyLocation() {
		locationManager.removeUpdates(this);
	}

	public Location getLocation() {
		return lastFix;
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		lastFix = location;
		LocationServiceHelper.getInstance(getApplicationContext()).onLocationChanged(location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}
}