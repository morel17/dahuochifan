package com.dahuochifan.core.model.order;

import java.io.Serializable;

public class OrderInfo implements Serializable {
	private String nickname, remark, dinnertime, number, avatar, chefids, cbids, createtime, name, lunchtime, mobile, startdate, enddate, cbstatus, olstatus,
			homeprov, homecity;
	private String dinername, dineraddr, eatenway;
	private int eatennum;
	private double price;
	private int commentnum;
	private boolean iscomment;
	private String whenindex;
	private String eatenwhen;
	private String dinermobile;
	private String tag, zitiaddr, cbname;
	private String olids;
	private long olversion;
	private String paystatus;
	private double total;
	private double discount;
	private long confirmtime;
	private OrderTrack track;
	private boolean isgrapcoupon;
	private int coupondiscount;
	private OrderCoupon grapcoupon;
	private int countdown;
	private int deliveryfee,tablewarefee;

	public int getTablewarefee() {
		return tablewarefee;
	}

	public void setTablewarefee(int tablewarefee) {
		this.tablewarefee = tablewarefee;
	}

	public int getDeliveryfee() {
		return deliveryfee;
	}

	public void setDeliveryfee(int deliveryfee) {
		this.deliveryfee = deliveryfee;
	}

	public int getCountdown() {
		return countdown;
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}

	public OrderCoupon getGrapcoupon() {
		return grapcoupon;
	}

	public void setGrapcoupon(OrderCoupon grapcoupon) {
		this.grapcoupon = grapcoupon;
	}

	public boolean isgrapcoupon() {
		return isgrapcoupon;
	}

	public void setIsgrapcoupon(boolean isgrapcoupon) {
		this.isgrapcoupon = isgrapcoupon;
	}


	public int getCoupondiscount() {
		return coupondiscount;
	}

	public void setCoupondiscount(int coupondiscount) {
		this.coupondiscount = coupondiscount;
	}

	public OrderTrack getTrack() {
		return track;
	}

	public void setTrack(OrderTrack track) {
		this.track = track;
	}

	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public long getConfirmtime() {
		return confirmtime;
	}
	public void setConfirmtime(long confirmtime) {
		this.confirmtime = confirmtime;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getPaystatus() {
		return paystatus;
	}
	public void setPaystatus(String paystatus) {
		this.paystatus = paystatus;
	}
	public long getOlversion() {
		return olversion;
	}
	public void setOlversion(long olversion) {
		this.olversion = olversion;
	}
	public String getOlids() {
		return olids;
	}
	public void setOlids(String olids) {
		this.olids = olids;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getZitiaddr() {
		return zitiaddr;
	}
	public void setZitiaddr(String zitiaddr) {
		this.zitiaddr = zitiaddr;
	}
	public String getCbname() {
		return cbname;
	}
	public void setCbname(String cbname) {
		this.cbname = cbname;
	}
	public String getDinermobile() {
		return dinermobile;
	}
	public void setDinermobile(String dinermobile) {
		this.dinermobile = dinermobile;
	}
	public String getEatenwhen() {
		return eatenwhen;
	}
	public void setEatenwhen(String eatenwhen) {
		this.eatenwhen = eatenwhen;
	}
	public String getWhenindex() {
		return whenindex;
	}
	public void setWhenindex(String whenindex) {
		this.whenindex = whenindex;
	}
	public int getCommentnum() {
		return commentnum;
	}
	public void setCommentnum(int commentnum) {
		this.commentnum = commentnum;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDinnertime() {
		return dinnertime;
	}
	public void setDinnertime(String dinnertime) {
		this.dinnertime = dinnertime;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getChefids() {
		return chefids;
	}
	public void setChefids(String chefids) {
		this.chefids = chefids;
	}
	public String getCbids() {
		return cbids;
	}
	public void setCbids(String cbids) {
		this.cbids = cbids;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLunchtime() {
		return lunchtime;
	}
	public void setLunchtime(String lunchtime) {
		this.lunchtime = lunchtime;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getEatennum() {
		return eatennum;
	}
	public void setEatennum(int eatennum) {
		this.eatennum = eatennum;
	}

	public boolean isIscomment() {
		return iscomment;
	}
	public void setIscomment(boolean iscomment) {
		this.iscomment = iscomment;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getCbstatus() {
		return cbstatus;
	}
	public void setCbstatus(String cbstatus) {
		this.cbstatus = cbstatus;
	}
	public String getOlstatus() {
		return olstatus;
	}
	public void setOlstatus(String olstatus) {
		this.olstatus = olstatus;
	}
	public String getHomeprov() {
		return homeprov;
	}
	public void setHomeprov(String homeprov) {
		this.homeprov = homeprov;
	}
	public String getHomecity() {
		return homecity;
	}
	public void setHomecity(String homecity) {
		this.homecity = homecity;
	}
	public String getDinername() {
		return dinername;
	}
	public void setDinername(String dinername) {
		this.dinername = dinername;
	}
	public String getDineraddr() {
		return dineraddr;
	}
	public void setDineraddr(String dineraddr) {
		this.dineraddr = dineraddr;
	}
	public String getEatenway() {
		return eatenway;
	}
	public void setEatenway(String eatenway) {
		this.eatenway = eatenway;
	}

}
