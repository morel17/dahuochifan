package com.dahuochifan.model.comment;

import java.util.List;

public class CommentsPage {
	private int pageSize, pageNumber, totalRow, totalPage;
	private List<CommentsInfo> list;
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
	public List<CommentsInfo> getList() {
		return list;
	}
	public void setList(List<CommentsInfo> list) {
		this.list = list;
	}
}
