package com.dahuochifan.core.model.address;

import java.util.List;

public class AddressAll {
	private String tag;
	private int resultcode;
	private List<AddressInfo> list;
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
	public List<AddressInfo> getList() {
		return list;
	}
	public void setList(List<AddressInfo> list) {
		this.list = list;
	}
	
}
