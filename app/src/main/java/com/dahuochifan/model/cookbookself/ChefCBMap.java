package com.dahuochifan.model.cookbookself;

import java.util.List;

/**
 * Created by Morel on 2015/12/7.
 * 主厨自己的CBMAP
 */
public class ChefCBMap{
    private List<ChefCBCookBook> cookbook;
    private List<ChefCBPic> pic;
    private ChefCBInfo chef;

    public ChefCBInfo getChef() {
        return chef;
    }

    public void setChef(ChefCBInfo chef) {
        this.chef = chef;
    }

    public List<ChefCBCookBook> getCookbook() {
        return cookbook;
    }

    public void setCookbook(List<ChefCBCookBook> cookbook) {
        this.cookbook = cookbook;
    }

    public List<ChefCBPic> getPic() {
        return pic;
    }

    public void setPic(List<ChefCBPic> pic) {
        this.pic = pic;
    }
}
