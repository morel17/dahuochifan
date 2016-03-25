package com.dahuochifan.model.discount;

/**
 * Created by morel on 2015/11/5.
 */
public class DiscountDetail {
    private String content;
    private String cuids;
    private int consumemin;
    private String remark;
    private String status;
    private String name;
    private int number;
    private String startdate;
    private String enddate;
    private String userids;
    private boolean isuse;
    private int discount;
    private boolean isvalid;
    private boolean ischecked;

    public boolean ischecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public boolean isvalid() {
        return isvalid;
    }

    public void setIsvalid(boolean isvalid) {
        this.isvalid = isvalid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCuids() {
        return cuids;
    }

    public void setCuids(String cuids) {
        this.cuids = cuids;
    }

    public int getConsumemin() {
        return consumemin;
    }

    public void setConsumemin(int consumemin) {
        this.consumemin = consumemin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getUserids() {
        return userids;
    }

    public void setUserids(String userids) {
        this.userids = userids;
    }

    public boolean isuse() {
        return isuse;
    }

    public void setIsuse(boolean isuse) {
        this.isuse = isuse;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
