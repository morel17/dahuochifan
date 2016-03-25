package com.dahuochifan.core.model.custom;

import java.util.List;

public class ShikePage{
	private int pageSize,pageNumber,totalRow,totalPage;
	private List<ShikeInfo> list;
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
	public List<ShikeInfo> getList() {
		return list;
	}
	public void setList(List<ShikeInfo> list) {
		this.list = list;
	}
	
}
