package com.dahuochifan.model.userinfo;

import com.dahuochifan.model.ChefPic;

import java.util.List;

public class PersonInfoDetail {
	private String status, nickname, eatennum, avatar, userids, chefids, likenum, username, addressinfo, token, bg, longitude, latitude, homeprov, otherprovs,
			curprov, collectnum, mobile;
	private int role;
	private int ordernum;
	private List<ChefPic> bgs;
	private String curcity, homecity,age;
	private String dinernum;
	private String manager;
	private PersonMark mark;


	public PersonMark getMark() {
		return mark;
	}

	public void setMark(PersonMark mark) {
		this.mark = mark;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getDinernum() {
		return dinernum;
	}

	public void setDinernum(String dinernum) {
		this.dinernum = dinernum;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getCurcity() {
		return curcity;
	}

	public void setCurcity(String curcity) {
		this.curcity = curcity;
	}

	public String getHomecity() {
		return homecity;
	}

	public void setHomecity(String homecity) {
		this.homecity = homecity;
	}

	public List<ChefPic> getBgs() {
		return bgs;
	}

	public void setBgs(List<ChefPic> bgs) {
		this.bgs = bgs;
	}

	public int getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(int ordernum) {
		this.ordernum = ordernum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUserids() {
		return userids;
	}

	public void setUserids(String userids) {
		this.userids = userids;
	}

	public String getChefids() {
		return chefids;
	}

	public void setChefids(String chefids) {
		this.chefids = chefids;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAddressinfo() {
		return addressinfo;
	}

	public void setAddressinfo(String addressinfo) {
		this.addressinfo = addressinfo;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getBg() {
		return bg;
	}

	public void setBg(String bg) {
		this.bg = bg;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getHomeprov() {
		return homeprov;
	}

	public void setHomeprov(String homeprov) {
		this.homeprov = homeprov;
	}

	public String getOtherprovs() {
		return otherprovs;
	}

	public void setOtherprovs(String otherprovs) {
		this.otherprovs = otherprovs;
	}

	public String getCurprov() {
		return curprov;
	}

	public void setCurprov(String curprov) {
		this.curprov = curprov;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEatennum() {
		return eatennum;
	}

	public void setEatennum(String eatennum) {
		this.eatennum = eatennum;
	}

	public String getLikenum() {
		return likenum;
	}

	public void setLikenum(String likenum) {
		this.likenum = likenum;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getCollectnum() {
		return collectnum;
	}

	public void setCollectnum(String collectnum) {
		this.collectnum = collectnum;
	}

}
