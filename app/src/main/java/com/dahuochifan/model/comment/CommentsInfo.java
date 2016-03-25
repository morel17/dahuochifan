package com.dahuochifan.model.comment;

import java.util.List;

public class CommentsInfo {
    private String createtime;
    private String content;
    private String nickname;
    private String avatar;
    private String userids;
    private String cbids;
    private String cbcids;
    private String url;
    private int score;
    private List<CommentPic> pic;

    public List<CommentPic> getPic() {
        return pic;
    }

    public void setPic(List<CommentPic> pic) {
        this.pic = pic;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getCbids() {
        return cbids;
    }

    public void setCbids(String cbids) {
        this.cbids = cbids;
    }

    public String getCbcids() {
        return cbcids;
    }

    public void setCbcids(String cbcids) {
        this.cbcids = cbcids;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
