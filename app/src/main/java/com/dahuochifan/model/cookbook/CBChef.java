package com.dahuochifan.model.cookbook;

/**
 * Created by Morel on 2015/11/19.
 *
 */
public class CBChef {
    private int limitdistance,deliveryfee,advanceminute,deliverydistance,tablewarefee;
    private String nickname,eatenway,tags;
    private float longitude,latitude;
//    private List<CBTime> lunchtimes,dinnertimes;
    private int today_num,tomorrow_num,maxnum;
    private boolean isopen;

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

    public int getMaxnum() {
        return maxnum;
    }

    public void setMaxnum(int maxnum) {
        this.maxnum = maxnum;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }


    public int getLimitdistance() {
        return limitdistance;
    }

    public void setLimitdistance(int limitdistance) {
        this.limitdistance = limitdistance;
    }

    public int getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(int deliveryfee) {
        this.deliveryfee = deliveryfee;
    }

    public int getAdvanceminute() {
        return advanceminute;
    }

    public void setAdvanceminute(int advanceminute) {
        this.advanceminute = advanceminute;
    }

    public int getDeliverydistance() {
        return deliverydistance;
    }

    public void setDeliverydistance(int deliverydistance) {
        this.deliverydistance = deliverydistance;
    }

    public int getTablewarefee() {
        return tablewarefee;
    }

    public void setTablewarefee(int tablewarefee) {
        this.tablewarefee = tablewarefee;
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
}
