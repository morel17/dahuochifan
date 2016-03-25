package com.dahuochifan.core.order;

import java.io.Serializable;

public class OrderListV11 implements Serializable{
	private String cbids, price, remark, tag,name;
	private int eatennum;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCbids() {
		return cbids;
	}

	public void setCbids(String cbids) {
		this.cbids = cbids;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getEatennum() {
		return eatennum;
	}

	public void setEatennum(int eatennum) {
		this.eatennum = eatennum;
	}

}
