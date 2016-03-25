package com.dahuochifan.model.activities;

import java.io.Serializable;

public class DhAdAllObj implements Serializable{
	private String tag;
	private int resultcode;
	private DhAdDetail obj;
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public int getResultcode() {
		return resultcode;
	}
	public void setResultcode(int resultcode) {
		this.resultcode = resultcode;
	}
	public DhAdDetail getObj() {
		return obj;
	}
	public void setObj(DhAdDetail obj) {
		this.obj = obj;
	}

}

