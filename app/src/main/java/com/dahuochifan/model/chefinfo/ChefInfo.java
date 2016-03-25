package com.dahuochifan.model.chefinfo;

import com.dahuochifan.model.ChefPic;
import com.dahuochifan.model.cheflist.ChefActs;

import java.util.List;

/**
 * Created by Morel on 2015/11/16.
 */
public class ChefInfo {
    private String model, gooddish, homecity, chefids, story, username, dinnertimes, nickname, eatenway;
    private String userids, avatar, addressinfo, homeprov, lunchtimes, tags, cbids;
    private int eatennum, tablewarefee, deliverydistance, likenum, distance, deliveryfee, collectnum;
    private int limitdistance, advanceminute, today_num, tomorrow_num,commentnum,totalscore;
    private float longitude, latitude;
    private boolean islike, iscollect,isopen;
    private List<ChefPic> bgs;
    private List<CBPic> cookbookpic;
    private List<ChefActs> acts;
    private int maxnum;

    public int getMaxnum() {
        return maxnum;
    }

    public void setMaxnum(int maxnum) {
        this.maxnum = maxnum;
    }

    public List<ChefActs> getActs() {
        return acts;
    }

    public int getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(int totalscore) {
        this.totalscore = totalscore;
    }

    public boolean isopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public String getCbids() {
        return cbids;
    }

    public void setCbids(String cbids) {
        this.cbids = cbids;
    }

    public int getToday_num() {
        return today_num;
    }

    public void setToday_num(int today_num) {
        this.today_num = today_num;
    }

    public int getTomorrow_num() {
        return tomorrow_num;
    }

    public void setTomorrow_num(int tomorrow_num) {
        this.tomorrow_num = tomorrow_num;
    }

    public void setActs(List<ChefActs> acts) {
        this.acts = acts;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAddressinfo() {
        return addressinfo;
    }

    public void setAddressinfo(String addressinfo) {
        this.addressinfo = addressinfo;
    }

    public String getHomeprov() {
        return homeprov;
    }

    public void setHomeprov(String homeprov) {
        this.homeprov = homeprov;
    }

    public String getLunchtimes() {
        return lunchtimes;
    }

    public void setLunchtimes(String lunchtimes) {
        this.lunchtimes = lunchtimes;
    }

    public boolean iscollect() {
        return iscollect;
    }

    public void setIscollect(boolean iscollect) {
        this.iscollect = iscollect;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGooddish() {
        return gooddish;
    }

    public void setGooddish(String gooddish) {
        this.gooddish = gooddish;
    }

    public String getHomecity() {
        return homecity;
    }

    public void setHomecity(String homecity) {
        this.homecity = homecity;
    }

    public String getChefids() {
        return chefids;
    }

    public void setChefids(String chefids) {
        this.chefids = chefids;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDinnertimes() {
        return dinnertimes;
    }

    public void setDinnertimes(String dinnertimes) {
        this.dinnertimes = dinnertimes;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEatenway() {
        return eatenway;
    }

    public void setEatenway(String eatenway) {
        this.eatenway = eatenway;
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

    public int getEatennum() {
        return eatennum;
    }

    public void setEatennum(int eatennum) {
        this.eatennum = eatennum;
    }

    public int getTablewarefee() {
        return tablewarefee;
    }

    public void setTablewarefee(int tablewarefee) {
        this.tablewarefee = tablewarefee;
    }

    public int getDeliverydistance() {
        return deliverydistance;
    }

    public void setDeliverydistance(int deliverydistance) {
        this.deliverydistance = deliverydistance;
    }

    public int getLikenum() {
        return likenum;
    }

    public void setLikenum(int likenum) {
        this.likenum = likenum;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(int deliveryfee) {
        this.deliveryfee = deliveryfee;
    }

    public int getCollectnum() {
        return collectnum;
    }

    public void setCollectnum(int collectnum) {
        this.collectnum = collectnum;
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

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public boolean islike() {
        return islike;
    }

    public void setIslike(boolean islike) {
        this.islike = islike;
    }

    public List<ChefPic> getBgs() {
        return bgs;
    }

    public void setBgs(List<ChefPic> bgs) {
        this.bgs = bgs;
    }

    public List<CBPic> getCookbookpic() {
        return cookbookpic;
    }

    public void setCookbookpic(List<CBPic> cookbookpic) {
        this.cookbookpic = cookbookpic;
    }
}
