package com.dahuochifan.model.cookbook;

/**
 * Created by admin on 2015/11/19.
 */
public class CBCookBook {
    private String lunchtimes,status,cbids,name,prices,dinnertimes;
    private boolean isopen;
    private int advanceminute,commentnum,price;
    private int totalscore,maxnum;
    private String minspending;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getMaxnum() {
        return maxnum;
    }

    public void setMaxnum(int maxnum) {
        this.maxnum = maxnum;
    }

    public String getMinspending() {
        return minspending;
    }

    public void setMinspending(String minspending) {
        this.minspending = minspending;
    }

    public String getLunchtimes() {
        return lunchtimes;
    }

    public void setLunchtimes(String lunchtimes) {
        this.lunchtimes = lunchtimes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCbids() {
        return cbids;
    }

    public void setCbids(String cbids) {
        this.cbids = cbids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getDinnertimes() {
        return dinnertimes;
    }

    public void setDinnertimes(String dinnertimes) {
        this.dinnertimes = dinnertimes;
    }

    public boolean isopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    public int getAdvanceminute() {
        return advanceminute;
    }

    public void setAdvanceminute(int advanceminute) {
        this.advanceminute = advanceminute;
    }

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public int getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(int totalscore) {
        this.totalscore = totalscore;
    }

}
