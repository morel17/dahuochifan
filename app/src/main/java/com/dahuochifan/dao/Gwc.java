package com.dahuochifan.dao;

public class Gwc {
	private Long id;
	/** Not-null value. */
	private String cbids;
	private String mid;
	private String date;
	private String status;
	private String address;
	private String distance;
	private String nickname;
	private String name;
	private String avatar;
	private Double price;
	private Integer totalsource;
	private Integer number;
	private String chefid;
	private String selected;
	public Gwc() {
	}

	public Gwc(Long id) {
		this.id = id;
	}
	public Gwc(Long id,String cbids, String mid) {
		this.id = id;
		this.cbids=cbids;
		this.mid=mid;
	}
	public Gwc(Long id, String cbids, String mid,String date,String status, String address, String distance, String nickname, String name, String avatar, Double price, Integer totalsource,Integer number,String chefid) {
		this.id = id;
		this.cbids = cbids;
		this.mid=mid;
		this.date=date;
		this.status = status;
		this.address = address;
		this.distance = distance;
		this.nickname = nickname;
		this.name = name;
		this.avatar = avatar;
		this.price = price;
		this.totalsource = totalsource;
		this.number=number;
		this.chefid=chefid;
	}
	
	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getChefid() {
		return chefid;
	}

	public void setChefid(String chefid) {
		this.chefid = chefid;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCbids() {
		return cbids;
	}

	public void setCbids(String cbids) {
		this.cbids = cbids;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getTotalsource() {
		return totalsource;
	}

	public void setTotalsource(Integer totalsource) {
		this.totalsource = totalsource;
	}



}
