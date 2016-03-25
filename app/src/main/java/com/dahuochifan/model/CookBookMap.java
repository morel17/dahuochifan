package com.dahuochifan.model;

import java.util.List;

public class CookBookMap {
    private List<CookBookLevel> cookbook;
    private List<CookBookPic> pic;

    public List<CookBookLevel> getCookbook() {
        return cookbook;
    }

    public void setCookbook(List<CookBookLevel> cookbook) {
        this.cookbook = cookbook;
    }

    public List<CookBookPic> getPic() {
        return pic;
    }

    public void setPic(List<CookBookPic> pic) {
        this.pic = pic;
    }
}
