package com.dahuochifan.model.cheflist;

import java.io.Serializable;
import java.util.List;

public class ChefPage implements Serializable{
	private int pageSize, pageNumber, totalRow, totalPage;
	private List<ChefList> list;
	
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
	public List<ChefList> getList() {
		return list;
	}
	public void setList(List<ChefList> list) {
		this.list = list;
	}

}
