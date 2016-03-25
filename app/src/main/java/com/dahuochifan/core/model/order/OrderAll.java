package com.dahuochifan.core.model.order;


public class OrderAll {
	private String tag;
	private int resultcode;
	private OrderPage page;
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
	public OrderPage getPage() {
		return page;
	}
	public void setPage(OrderPage page) {
		this.page = page;
	}

}
