package com.dahuochifan.model;

import java.io.Serializable;

public class CookBookLevel implements Serializable {
    private String tags, status, name, cbids;
    private boolean isopen, isselect;
    private int commentnum, maxnum, totalscore;
    private int mynumber;
    private double myprice;
    private double minspending, price;
    private String mytag;
    private int step;
    private int tomorrow_num;
    private int today_num;
    private String prices;


    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public int getTomorrow_num() {
        return tomorrow_num;
    }

    public void setTomorrow_num(int tomorrow_num) {
        this.tomorrow_num = tomorrow_num;
    }

    public int getToday_num() {
        return today_num;
    }

    public void setToday_num(int today_num) {
        this.today_num = today_num;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCbids() {
        return cbids;
    }

    public void setCbids(String cbids) {
        this.cbids = cbids;
    }

    public boolean isIsopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    public boolean isIsselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public int getMaxnum() {
        return maxnum;
    }

    public void setMaxnum(int maxnum) {
        this.maxnum = maxnum;
    }

    public int getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(int totalscore) {
        this.totalscore = totalscore;
    }

    public int getMynumber() {
        return mynumber;
    }

    public void setMynumber(int mynumber) {
        this.mynumber = mynumber;
    }

    public double getMyprice() {
        return myprice;
    }

    public void setMyprice(double myprice) {
        this.myprice = myprice;
    }

    public double getMinspending() {
        return minspending;
    }

    public void setMinspending(double minspending) {
        this.minspending = minspending;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMytag() {
        return mytag;
    }

    public void setMytag(String mytag) {
        this.mytag = mytag;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

}
