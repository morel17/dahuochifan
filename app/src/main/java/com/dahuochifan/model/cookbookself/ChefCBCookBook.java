package com.dahuochifan.model.cookbookself;

/**
 * Created by Morel on 2015/12/7.
 * 厨师自己的菜单信息
 */
public class ChefCBCookBook{
    private String tags,lunchtimes,status,cbids,name,prices,dinnertimes;
    private boolean isopen;
    private int advanceminute,tomorrow_num,commentnum,price,maxnum,today_num,totalscore,step;
    private double minspending;


    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public int getTomorrow_num() {
        return tomorrow_num;
    }

    public void setTomorrow_num(int tomorrow_num) {
        this.tomorrow_num = tomorrow_num;
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

    public int getMaxnum() {
        return maxnum;
    }

    public void setMaxnum(int maxnum) {
        this.maxnum = maxnum;
    }

    public int getToday_num() {
        return today_num;
    }

    public void setToday_num(int today_num) {
        this.today_num = today_num;
    }

    public int getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(int totalscore) {
        this.totalscore = totalscore;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public double getMinspending() {
        return minspending;
    }

    public void setMinspending(double minspending) {
        this.minspending = minspending;
    }
}
