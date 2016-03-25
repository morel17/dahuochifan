package com.dahuochifan.model.message;

public class MessageAll {
	private String tag;
	private int resultcode;
	private MessagePage page;
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
	public MessagePage getPage() {
		return page;
	}
	public void setPage(MessagePage page) {
		this.page = page;
	}

}
