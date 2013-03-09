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

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;

import com.example.simplelocationservice.LocationService.LocationBinder;


public class LocationServiceHelper{

	private Context context = null;
	private LocationService service = null;
	private boolean bound = false;
	private boolean needToStop = false;
	private boolean needToStart = true;
	public ServiceConnection connection;
	private static LocationServiceHelper instance;
	
	private ArrayList<ILocationListener> listeners;

	public LocationServiceHelper(Context context){

		this.context = context;
		context.startService(new Intent(context, LocationService.class));
		Intent intent = new Intent(context, LocationService.class);

		connection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName className, IBinder service) {
				// We've bound to LocalService, cast the IBinder and get LocalService instance

				LocationBinder binder = (LocationBinder) service;
				LocationServiceHelper.this.service = binder.getService();
				bound = true;

				if(needToStop) {
					LocationServiceHelper.this.service.stop();
					return;
				}

				LocationServiceHelper.this.service.start();
				needToStart = false;
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				bound = false;
			}
		};

		context.bindService(intent, connection, 0);
	}

	public static LocationServiceHelper getInstance(Context context){
		if((instance == null) || (instance.bound == false)) {
			instance = new LocationServiceHelper(context);
		}
		return instance;
	}

	public static void destroy(){
		if(instance != null){
			instance.service.stop();
			instance.context.unbindService(instance.connection);
			if(instance.listeners != null) {
				instance.clearListeners();
			}
			instance.bound = false;
		}
	}
	
	public Location getLocation() {
		return service.getLocation();
	}

	public void onResume(){
		if((service != null) && needToStart) {
			LocationServiceHelper.this.service.start();
		}
	}

	public void onPause(){
		if(!bound){
			needToStop = true;
			return;
		}

		service.stop();
		needToStart = true;
	}
	
	public boolean addListener(ILocationListener listener) {
		if(listeners == null) {
			listeners = new ArrayList<ILocationListener>();
		}
		return listeners.add(listener);
	}
	
	public boolean removeListener(ILocationListener listener) {
		if(listeners == null) {
			return false; //nothing to remove if null...
		}
		return listeners.remove(listener);
	}
	
	public void clearListeners() {
		if(listeners != null) {
			listeners.clear();
		}
	}
	
	public void onLocationChanged(Location location) {
		if(listeners != null) {
			for(ILocationListener listener : listeners) {
				listener.onLocationChanged(location);
			}
		}
	}

}
