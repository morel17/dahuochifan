package com.dahuochifan.model.message;

public class MsgDetAll {
	private String tag;
	private int resultcode;
	private MessageDetail obj;
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
	public MessageDetail getObj() {
		return obj;
	}
	public void setObj(MessageDetail obj) {
		this.obj = obj;
	}

}
