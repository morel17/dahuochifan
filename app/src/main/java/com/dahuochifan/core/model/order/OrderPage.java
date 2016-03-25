package com.dahuochifan.core.model.order;

import java.util.List;

public class OrderPage {
	private int pageSize, pageNumber, totalRow, totalPage;
	private List<OrderInfo> list;
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public List<OrderInfo> getList() {
		return list;
	}
	public void setList(List<OrderInfo> list) {
		this.list = list;
	}

}
