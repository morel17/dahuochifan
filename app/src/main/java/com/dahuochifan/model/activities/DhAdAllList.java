package com.dahuochifan.model.activities;

import java.util.List;

public class DhAdAllList {
	private String tag;
	private int resultcode;
	private List<DhAdDetail> list;
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
	public List<DhAdDetail> getList() {
		return list;
	}
	public void setList(List<DhAdDetail> list) {
		this.list = list;
	}

}
