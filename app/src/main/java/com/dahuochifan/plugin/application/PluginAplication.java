package com.dahuochifan.plugin.application;

import java.util.Map;

import android.app.Application;



 public abstract class  PluginAplication<K,V> extends Application {
	public abstract Map<K,V>  getDesciption();
}
