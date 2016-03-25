package com.dahuochifan.model.userinfo;

/**
 * Created by Morel on 2015/12/2.
 * 用于显示消息的标注数量
 */
public class PersonMark {
    private String dinerordernum;//主厨的订单消息数量
    private String chefordernum;//食客的订单消息数量
    private String newsnum;//通知消息数量

    public String getDinerordernum() {
        return dinerordernum;
    }

    public void setDinerordernum(String dinerordernum) {
        this.dinerordernum = dinerordernum;
    }

    public String getChefordernum() {
        return chefordernum;
    }

    public void setChefordernum(String chefordernum) {
        this.chefordernum = chefordernum;
    }

    public String getNewsnum() {
        return newsnum;
    }

    public void setNewsnum(String newsnum) {
        this.newsnum = newsnum;
    }
}
