package com.dahuochifan.model.activities;

import java.io.Serializable;

public class DhAdObj implements Serializable {
	private String actids;
	private String type_status;
	private int limit_num;//活动剩余次数
	private String discounts;
	private String limitprices;//满足优惠的价格
	private int eatennum;//满足优惠的人数
	private String remark;


	public String getDiscounts() {
		return discounts;
	}

	public void setDiscounts(String discounts) {
		this.discounts = discounts;
	}

	public String getLimitprices() {
		return limitprices;
	}

	public void setLimitprices(String limitprices) {
		this.limitprices = limitprices;
	}

	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getEatennum() {
		return eatennum;
	}
	public void setEatennum(int eatennum) {
		this.eatennum = eatennum;
	}
	public String getActids() {
		return actids;
	}
	public void setActids(String actids) {
		this.actids = actids;
	}
	public String getType_status() {
		return type_status;
	}
	public void setType_status(String type_status) {
		this.type_status = type_status;
	}
	public int getLimit_num() {
		return limit_num;
	}
	public void setLimit_num(int limit_num) {
		this.limit_num = limit_num;
	}

}
