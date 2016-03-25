package com.dahuochifan.model.cheflist;

import com.dahuochifan.model.ChefPic;
import com.dahuochifan.model.NotifyObj;
import com.dahuochifan.model.activities.DhAdObj;

import java.io.Serializable;
import java.util.List;

public class ChefList implements Serializable {

    private String nickname, eatennum, chefids, userids, avatar, collectnum, likenum, username, addressinfo, nativeland, longitude, latitude, mobile;
    private String homeprov, homecity;
    private boolean islike, iscollect;
    private int distance;
    private String eatenway;
    private String story;
    private List<ChefPic> bgs;
    private List<NotifyObj> notify;
    private String homebg;
    private String tags;
    private DhAdObj activity;
    private boolean isopen;
    private String gooddish;
    private String lunchtimes, dinnertimes;
    private int advanceminute;
    private int limitdistance;
    private int tablewarefee, deliveryfee, deliverydistance;
    private int model;
    private List<TopAd> headers;
    private List<ChefActs> acts;


    public List<ChefActs> getActs() {
        return acts;
    }

    public void setActs(List<ChefActs> acts) {
        this.acts = acts;
    }

    public List<TopAd> getHeaders() {
        return headers;
    }

    public void setHeaders(List<TopAd> headers) {
        this.headers = headers;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getDeliverydistance() {
        return deliverydistance;
    }

    public void setDeliverydistance(int deliverydistance) {
        this.deliverydistance = deliverydistance;
    }

    public int getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(int deliveryfee) {
        this.deliveryfee = deliveryfee;
    }

    public int getTablewarefee() {
        return tablewarefee;
    }

    public void setTablewarefee(int tablewarefee) {
        this.tablewarefee = tablewarefee;
    }

    public int getLimitdistance() {
        return limitdistance;
    }

    public void setLimitdistance(int limitdistance) {
        this.limitdistance = limitdistance;
    }

    public int getAdvanceminute() {
        return advanceminute;
    }

    public void setAdvanceminute(int advanceminute) {
        this.advanceminute = advanceminute;
    }

    public String getLunchtimes() {
        return lunchtimes;
    }

    public void setLunchtimes(String lunchtimes) {
        this.lunchtimes = lunchtimes;
    }

    public String getDinnertimes() {
        return dinnertimes;
    }

    public void setDinnertimes(String dinnertimes) {
        this.dinnertimes = dinnertimes;
    }

    public String getGooddish() {
        return gooddish;
    }

    public void setGooddish(String gooddish) {
        this.gooddish = gooddish;
    }

    public boolean isopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public DhAdObj getActivity() {
        return activity;
    }

    public void setActivity(DhAdObj activity) {
        this.activity = activity;
    }

    public String getHomebg() {
        return homebg;
    }

    public void setHomebg(String homebg) {
        this.homebg = homebg;
    }

    public List<NotifyObj> getNotify() {
        return notify;
    }

    public void setNotify(List<NotifyObj> notify) {
        this.notify = notify;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getEatenway() {
        return eatenway;
    }

    public void setEatenway(String eatenway) {
        this.eatenway = eatenway;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCollectnum() {
        return collectnum;
    }

    public void setCollectnum(String collectnum) {
        this.collectnum = collectnum;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEatennum() {
        return eatennum;
    }

    public void setEatennum(String eatennum) {
        this.eatennum = eatennum;
    }

    public String getChefids() {
        return chefids;
    }

    public void setChefids(String chefids) {
        this.chefids = chefids;
    }

    public String getUserids() {
        return userids;
    }

    public void setUserids(String userids) {
        this.userids = userids;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLikenum() {
        return likenum;
    }

    public boolean isIslike() {
        return islike;
    }

    public void setIslike(boolean islike) {
        this.islike = islike;
    }

    public boolean isIscollect() {
        return iscollect;
    }

    public void setIscollect(boolean iscollect) {
        this.iscollect = iscollect;
    }

    public void setLikenum(String likenum) {
        this.likenum = likenum;
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

    public String getNativeland() {
        return nativeland;
    }

    public void setNativeland(String nativeland) {
        this.nativeland = nativeland;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<ChefPic> getBgs() {
        return bgs;
    }

    public void setBgs(List<ChefPic> bgs) {
        this.bgs = bgs;
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

}
