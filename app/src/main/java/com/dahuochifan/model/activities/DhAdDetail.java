package com.dahuochifan.model.activities;

import java.io.Serializable;

public class DhAdDetail implements Serializable {
	
	private int sort;
	private String remark;
	private String status;
	private String ids;
	private String type_status;
	private int discount;
	private int version;
	private double limit_price;
	private String createtime;
	private int limit_num;
	private String lastdate;
	
	private String detail;
	private String head;
	private String startdate;
	private String enddate;
	private String acids;

	public String getAcids() {
		return acids;
	}
	public void setAcids(String acids) {
		this.acids = acids;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getType_status() {
		return type_status;
	}
	public void setType_status(String type_status) {
		this.type_status = type_status;
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public double getLimit_price() {
		return limit_price;
	}
	public void setLimit_price(double limit_price) {
		this.limit_price = limit_price;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public int getLimit_num() {
		return limit_num;
	}
	public void setLimit_num(int limit_num) {
		this.limit_num = limit_num;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getLastdate() {
		return lastdate;
	}
	public void setLastdate(String lastdate) {
		this.lastdate = lastdate;
	}

}
