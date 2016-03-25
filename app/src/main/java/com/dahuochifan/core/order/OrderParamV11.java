package com.dahuochifan.core.order;

import java.io.Serializable;
import java.util.List;

public class OrderParamV11  implements Serializable{
	private String mids, token, platform, eatenway, lunchtime, dinnertime, dinername, dinermobile, dineraddr, whenindex, chefids;
	private String total;
	private List<OrderListV11> list;
	private String imei;
	private String daids;
	private String cuids;
	private String coordinate;
	private String actids;
	private String tablewarefee,deliveryfee;
	private String distance;

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDeliveryfee() {
		return deliveryfee;
	}

	public void setDeliveryfee(String deliveryfee) {
		this.deliveryfee = deliveryfee;
	}

	public String getTablewarefee() {
		return tablewarefee;
	}

	public void setTablewarefee(String tablewarefee) {
		this.tablewarefee = tablewarefee;
	}

	public String getActids() {
		return actids;
	}

	public void setActids(String actids) {
		this.actids = actids;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getCuids() {
		return cuids;
	}

	public void setCuids(String cuids) {
		this.cuids = cuids;
	}

	public String getDaids() {
		return daids;
	}

	public void setDaids(String daids) {
		this.daids = daids;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getMids() {
		return mids;
	}
	public void setMids(String mids) {
		this.mids = mids;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getEatenway() {
		return eatenway;
	}
	public void setEatenway(String eatenway) {
		this.eatenway = eatenway;
	}
	public String getLunchtime() {
		return lunchtime;
	}
	public void setLunchtime(String lunchtime) {
		this.lunchtime = lunchtime;
	}
	public String getDinnertime() {
		return dinnertime;
	}
	public void setDinnertime(String dinnertime) {
		this.dinnertime = dinnertime;
	}
	public String getDinername() {
		return dinername;
	}
	public void setDinername(String dinername) {
		this.dinername = dinername;
	}
	public String getDinermobile() {
		return dinermobile;
	}
	public void setDinermobile(String dinermobile) {
		this.dinermobile = dinermobile;
	}
	public String getDineraddr() {
		return dineraddr;
	}
	public void setDineraddr(String dineraddr) {
		this.dineraddr = dineraddr;
	}
	public String getWhenindex() {
		return whenindex;
	}
	public void setWhenindex(String whenindex) {
		this.whenindex = whenindex;
	}
	public String getChefids() {
		return chefids;
	}
	public void setChefids(String chefids) {
		this.chefids = chefids;
	}
	public List<OrderListV11> getList() {
		return list;
	}
	public void setList(List<OrderListV11> list) {
		this.list = list;
	}

}
