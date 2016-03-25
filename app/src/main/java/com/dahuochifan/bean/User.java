package com.dahuochifan.bean;

import com.dahuochifan.model.userinfo.PersonMark;

import java.io.Serializable;

public class User implements Serializable {

    private boolean isLogin;
    private boolean isWechat;

    private long endtime;
    private String status;
    private String nickname;
    private String homecity;
    private String avatar;
    private String userids;
    private String chefids;
    private String validtime;
    private String curcity;
    private String username;
    private String addressinfo;
    private String token;
    private String age;
    private String bg;
    private int role = -99;
    private String homeprov, curprov, otherprovs;
    private String mobile;
    private String wxuserid;

    private PersonMark mark;

    public PersonMark getMark() {
        return mark;
    }

    public void setMark(PersonMark mark) {
        this.mark = mark;
    }

    public void clear() {
        setLogin(false);
        setWechat(false);
        setEndtime(0L);
        setStatus("");
        setNickname("");
        setHomecity("");
        setAvatar("");
        setUserids("");
        setChefids("");
        setValidtime("");
        setCurcity("");
        setCurprov("");
        setUsername("");
        setAddressinfo("");
        setToken("");
        setAge("");
        setBg("");
        setHomeprov("");
        setCurprov("");
        setOtherprovs("");
        setMobile("");
        setWxuserid("");
        if (mark != null) {
            mark.setChefordernum("");
            mark.setDinerordernum("");
            mark.setNewsnum("");
        }
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean isWechat() {
        return isWechat;
    }

    public void setWechat(boolean isWechat) {
        this.isWechat = isWechat;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
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

    public String getHomecity() {
        return homecity;
    }

    public void setHomecity(String homecity) {
        this.homecity = homecity;
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

    public String getValidtime() {
        return validtime;
    }

    public void setValidtime(String validtime) {
        this.validtime = validtime;
    }

    public String getCurcity() {
        return curcity;
    }

    public void setCurcity(String curcity) {
        this.curcity = curcity;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public String getCurprov() {
        return curprov;
    }

    public void setCurprov(String curprov) {
        this.curprov = curprov;
    }

    public String getOtherprovs() {
        return otherprovs;
    }

    public void setOtherprovs(String otherprovs) {
        this.otherprovs = otherprovs;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWxuserid() {
        return wxuserid;
    }

    public void setWxuserid(String wxuserid) {
        this.wxuserid = wxuserid;
    }
}
