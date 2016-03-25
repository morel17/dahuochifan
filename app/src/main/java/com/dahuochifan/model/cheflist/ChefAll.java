package com.dahuochifan.model.cheflist;

import java.io.Serializable;

public class ChefAll implements Serializable{
	private String tag, resultcode;
	private ChefPage page;
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getResultcode() {
		return resultcode;
	}
	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}
	public ChefPage getPage() {
		return page;
	}
	public void setPage(ChefPage page) {
		this.page = page;
	}

}
