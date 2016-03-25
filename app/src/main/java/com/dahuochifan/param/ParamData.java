package com.dahuochifan.param;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.MyApplication;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.ToolDes;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.utils.L;
import com.payment.alipay.demo.SignUtils;

import java.io.Serializable;
import java.net.URLEncoder;

import cn.jpush.android.api.JPushInterface;

public class ParamData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5212361185234933147L;
    private String mids;
    private String token;
    private String platform;// 平台
    private String platform_version;

    private String userids;// 用户ids
    private String chefids;// 主厨ids
    private String cbids;// 菜谱ids
    private String status;
    private String homeprov;// 家乡所在省份
    private String homecity;// 家乡所在的市或区
    private String curprov;// 当前所在省份
    private String curcity;// 当前所在的市或区
    private String count;
    private String w;
    private String h;
    private String q;
    private String pageIndex;
    private String pageSize;

    // userinfo
    private String username;
    private String pwd;
    private String userinfoids;
    private String role;
    private String nickname;
    private String address;
    private String addressinfo;
    private String nativeland;
    private String mobile;
    private String idcard;
    private String name;
    private String otherprovs;
    private String email;
    private String longitude;// 经度
    private String latitude;// 纬度

    private String price;

    private String loc_detail;
    private String daids;// 地址号
    private String content;// 内容
    private String smscode;// 验证码
    private String versioncode;
    private String versionname;
    private String score;

    private String prov, city, town, addrdetail;
    private String isdefault;
    private String olids;
    private String search;
    private String code;
    private String tag;
    private String cnids;
    private String age;
    private String version;

    private String open;
    private String openid;
    private String access_token;
    private String wxuserid;
    private String gender;
    private String imei;
    private String ncids;
    private String avatar;
    private String type;
    private String tagids;
    private String story;

    private String distance;
    private String loc_simple;
    private String consumemin;
    private String showtopbar;
    private String coordinate;
    private String device_model;
    private String mark;
    private String invitecode;
    private String whenindex;

    public static ParamData data = new ParamData();

    public static ParamData getInstance() {
        data.init();
        return data;
    }

    private ParamData() {
    }

    public RequestParams desParam(ParamData data) {
        RequestParams params = new RequestParams();
        try {
            Gson gson = new Gson();
            String param = gson.toJson(data, ParamData.class);
            String sign_json = param;
//            L.e("param===" + param);
            if (MyConstant.NOTTEST) {
                ToolDes des;
                des = new ToolDes(MyConstant.user.getToken());
                param = URLEncoder.encode(param, "UTF-8");
                String json = des.encrypt(param);
                json = URLEncoder.encode(json, "UTF-8");
                params.put("param", json);
                params.put("sign", URLEncoder.encode(SignUtils.sign(sign_json, MyConstant.PUCLIC_KEY), "UTF-8"));
            } else {
                params.put("param", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public RequestParams desParam(String json) {
        RequestParams params = new RequestParams();
        String sign_json = json;
        try {

            if (MyConstant.NOTTEST) {
                ToolDes des;
                des = new ToolDes(MyConstant.user.getToken());
                json = URLEncoder.encode(json, "UTF-8");
                json = des.encrypt(json);
                json = URLEncoder.encode(json.replace("\n", "").replace("\r", ""), "UTF-8");
                params.put("param", json);
                params.put("sign", URLEncoder.encode(SignUtils.sign(sign_json, MyConstant.PUCLIC_KEY), "UTF-8"));
            } else {
                params.put("param", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public void doPrepare() {
        if (MyConstant.ISPREPARE) {
            data.setDevice_model(MyConstant.device);
            if (TextUtils.isEmpty(MyConstant.user.getUserids())) {
                data.setMids("搭伙吃饭");
            } else {
                data.setMids(MyConstant.user.getUserids());
            }
            if (TextUtils.isEmpty(MyConstant.user.getToken())) {
                data.setToken("搭伙吃饭");
            } else {
                data.setToken(MyConstant.user.getToken());
            }

            data.setPlatform("Android");
            if (TextUtils.isEmpty(MyConstant.PLATFORM_VERSION)) {
                data.setPlatform_version("2.2");
            } else {
                data.setPlatform_version(MyConstant.PLATFORM_VERSION);
            }
            SharedPreferences spf = SharedPreferenceUtil.initSharedPerence().init(MyApplication.getInstance(), MyConstant.APP_SPF_NAME);
            if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getRegisterId(spf))) {
                data.setImei(SharedPreferenceUtil.initSharedPerence().getRegisterId(spf));
            } else {
                data.setImei(JPushInterface.getRegistrationID(MyApplication.getInstance()));
            }
        }
    }

    public RequestParams getDiscountAllObj(String status, String pageIndex, String pageSize) {
        doPrepare();
        data.setStatus(status);
        data.setPageIndex(pageIndex);
        data.setPageSize(pageSize);
        return desParam(data);
    }

    public RequestParams getDiscountEnableObj(String consumemin) {
        doPrepare();
        data.setConsumemin(consumemin);
        return desParam(data);
    }

    public RequestParams getPicObj() {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        RequestParams params = new RequestParams();
        Gson gson = new Gson();
        String param = gson.toJson(data, ParamData.class);
        L.e("params===" + param);
        params.put("param", param);
        return params;
    }

    public RequestParams getOrderDetail(String olids, String status) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setStatus(status);
        data.setOlids(olids);
        return desParam(data);
    }

    public RequestParams getChefFromMap(String search, String latitude, String longitude, String distance) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setSearch(search);
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        data.setDistance(distance);
        data.setW("200");
        data.setH("200");
        data.setQ("75");
        data.setCount("1");
        return desParam(data);
    }

    public RequestParams getStoryContentObj() {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);
    }

    public RequestParams getStoryObj(String story) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setStory(story);
        return desParam(data);
    }

    public RequestParams getMsgDelObj(String ncids) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setNcids(ncids);
        return desParam(data);
    }

    public RequestParams getChefTopObj(Activity activity, int count, String longitude, String latitude, String chefids) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        int iw = MainTools.getwidth(activity) / 5 * 4;
        int ih = iw / 5 * 3;
        data.setW(iw + "");
        data.setH(ih + "");
        data.setQ(MyConstant.PIC_ENTITY + "");
        data.setCount(count + "");
        data.setLatitude(latitude + "");
        data.setLongitude(longitude + "");
        data.setChefids(chefids);

        return desParam(data);
    }

    public RequestParams getNormalObj() {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);
    }

    public ParamData getWechatObj(String access_token, String openid, String coordinate, String registrationID) {
        data.setPlatform("Android");
        data.setDevice_model(MyConstant.device);
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setAccess_token(access_token);
        data.setCoordinate(coordinate);
        data.setOpenid(openid);
        data.setImei(registrationID);
        return data;
    }

    public RequestParams getTagAddObj(String tagids) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setTagids(tagids);
        return desParam(data);
    }

    public RequestParams getMsgDetObj(String ncids) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setNcids(ncids);
        return desParam(data);
    }

    public RequestParams getMessageObj(int pageIndex, int pageSize, String type) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setPageIndex(pageIndex + "");
        data.setPageSize(pageSize + "");
        data.setType(type);
        return desParam(data);
    }

    public RequestParams getAdObj() {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);

    }

    public RequestParams avatarObj() {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);
    }

    /**
     * @param cbids 菜谱id
     * @param open  关闭状态
     * @return 打开关闭菜谱
     */
    public RequestParams toggleObj(String cbids, boolean open) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setCbids(cbids);
        data.setOpen(open + "");
        return desParam(data);
    }

    /**
     * @param chefids 菜谱id
     * @param open    关闭状态
     * @return 打开关闭店铺
     */
    public RequestParams toggleObj2(String chefids, boolean open) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setChefids(chefids);
        data.setOpen(open + "");
        return desParam(data);
    }

    public RequestParams postTagObj(String status) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setStatus(status);
        return desParam(data);
    }

    public RequestParams postTagObj_new() {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);
    }

    public RequestParams addNofifyObj(String content) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setContent(content);
        return desParam(data);
    }

    public RequestParams delNotifyObj(String cnids) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setCnids(cnids);
        return desParam(data);
    }

    public RequestParams getNofifyObj() {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);
    }

    public RequestParams deleteAddrObj(String daids) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setDaids(daids);
        return desParam(data);
    }

    public ParamData indentifyObj(String code) {
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setCode(code);
        return data;
    }

    public RequestParams searchcookObj(Activity activity, String search, int pageIndex, int pageSize, int count) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setSearch(search);
        data.setPageIndex(pageIndex + "");
        data.setPageSize(pageSize + "");
        int iw = MainTools.getwidth(activity) / 5 * 4;
        int ih = iw / 5 * 3;
        data.setW(iw + "");
        data.setH(ih + "");
        data.setQ(MyConstant.PIC_ENTITY + "");
        data.setCount(count + "");
        return desParam(data);
    }

    public RequestParams applyNetObj(String mobile, String name, String age, String gender, String address) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setMobile(mobile);
        data.setName(name);
        data.setAge(age);
        data.setGender(gender);
        data.setAddress(address);
        return desParam(data);
    }

    // public ParamData updateAgeObj(String mids, String token, String platform, String nicsdfkname, ParamData data) {
    // data.setMids(mids);
    // data.setToken(token);
    // data.setPlatform(platform);
    // data.setsdfNickname(nickname);
    // return data;
    // }
    public RequestParams updateNickNameObj(String nickname) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setNickname(nickname);
        return desParam(data);
    }

    public RequestParams updateHomeProvObj(String homeprov, String homecity) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setHomeprov(homeprov);
        data.setHomecity(homecity);
        return desParam(data);
    }

    public RequestParams updateCurProvObj(String curprov, String curcity) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setCurprov(curprov);
        data.setCurcity(curcity);
        return desParam(data);
    }

    public RequestParams updateAgeObj(String age) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setAge(age);
        return desParam(data);
    }

    public RequestParams updateMobileObj(String mobile) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setMobile(mobile);
        return desParam(data);
    }

    public RequestParams updateProvObj(String otherprovs) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setOtherprovs(otherprovs);
        return desParam(data);
    }

    public RequestParams postStatusObj(String olids, String status, long olversion) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setOlids(olids);
        data.setStatus(status);
        data.setVersion(olversion + "");
        return desParam(data);
    }

    public RequestParams addAdrObj(String name, String mobile, String loc_detail, String loc_simple, String addrdetail, boolean isdefault, String latitude, String longitude) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setName(name);
        data.setMobile(mobile);
        data.setLoc_detail(loc_detail);
        data.setAddrdetail(addrdetail);
        data.setLongitude(longitude);
        data.setLatitude(latitude);
        data.setIsdefault(isdefault + "");
        data.setLoc_simple(loc_simple);
        return desParam(data);
    }

    public RequestParams editAdrObj(String daids, String name, String mobile, String loc_detail, String loc_simple, String addrdetail,
                                    boolean isdefault, String latitude, String longitude) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setName(name);
        data.setMobile(mobile);
        data.setLoc_detail(loc_detail);
        data.setAddrdetail(addrdetail);
        data.setIsdefault(isdefault + "");
        data.setDaids(daids);
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        data.setLoc_simple(loc_simple);
        return desParam(data);
    }

    public RequestParams postCommentObj(String cbids, String content, String score, String olids) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setCbids(cbids);
        data.setContent(content);
        data.setScore(score);
        data.setOlids(olids);
        return desParam(data);
    }

    public RequestParams getCommentObj(String cbids, int pageindex, int pagesize, String chefids) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setCbids(cbids);
        data.setChefids(chefids);
        data.setPageIndex(pageindex + "");
        data.setPageSize(pagesize + "");
        return desParam(data);
    }

    /*
     * public ParamData getUpdateObj(String mids, String token, String platform, String versioncode, String versionname) { data.setMids(mids);
     * data.setToken(token); data.setPlatform(platform); data.setVersioncode(versioncode); data.setVersionname(versionname); return data; }
     */
    public RequestParams getFeedbackObj(String content) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setContent(content);
        return desParam(data);
    }

    public ParamData getYzmObj(String platform, String username) {
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setPlatform(platform);
        data.setUsername(username);
        return data;
    }

    public ParamData getYzmObj2(String mid, String token, String platform) {
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setPlatform(platform);
        data.setMids(mid);
        data.setToken(token);
        return data;
    }

    public RequestParams getDefaultAddrObj(String daids) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setDaids(daids);
        return desParam(data);

    }

    /**
     * 获取默认地址
     *
     * @param
     * @return
     */
    public RequestParams getAddressObj() {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);
    }

    public RequestParams getShikeObj(int pageindex, int pagesize, String status) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setPageIndex(pageindex + "");
        data.setPageSize(pagesize + "");
        data.setStatus(status);
        return desParam(data);
    }

    /**
     * 获取个人信息
     *
     * @param role
     * @return data
     */
    public RequestParams getPersonObj(int role) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setRole(role + "");
        return desParam(data);
    }

    public ParamData getLoginObj(String username, String pwd, String imei, String coordinate) {
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.init();
        data.setDevice_model(MyConstant.device);
        data.setPlatform("Android");
        data.setCoordinate(coordinate);
        data.setUsername(username);
        data.setPwd(pwd);
        if (!TextUtils.isEmpty(imei)) {
            data.setImei(imei);
        } else {
            data.setImei("");
        }
        return data;
    }

    public ParamData getRegisterObj(String platform, String username, String pwd, String code, String imei, String coordinate, String invitecode) {
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setPlatform(platform);
        data.setUsername(username);
        data.setCoordinate(coordinate);
        data.setPwd(pwd);
        data.setCode(code);
        data.setImei(imei);
        data.setInvitecode(invitecode);
        return data;
    }

    public ParamData changePwdObj(String username, String code, String pwd) {
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setUsername(username);
        data.setPwd(pwd);
        data.setCode(code);
        return data;
    }


    /**
     * 收藏列表
     *
     * @param activity
     * @param pageIndex
     * @param pageSize
     * @param count
     * @param longitude
     * @param latitude
     * @return data
     */
    public RequestParams getCollectListObj(Activity activity, int pageIndex, int pageSize, int count,
                                           double longitude, double latitude) {
        data.setPageIndex(pageIndex + "");
        data.setPageSize(pageSize + "");
        int iw = MainTools.getwidth(activity) / 5 * 4;
        int ih = iw / 5 * 3;
        data.setW(iw + "");
        data.setH(ih + "");
        data.setQ(MyConstant.PIC_ENTITY + "");
        data.setCount(count + "");
        data.setLongitude(longitude + "");
        data.setLatitude(latitude + "");
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);

    }

    public RequestParams getChefListObj(Activity activity, int pageIndex, int pageSize,
                                        int count, String longitude, String latitude, String tag, String showtopbar) {
        data.setPageIndex(pageIndex + "");
        data.setPageSize(pageSize + "");
        int iw = MainTools.getwidth(activity) / 5 * 4;
        int ih = iw / 3 * 2;
        data.setW(iw + "");
        data.setH(ih + "");
        data.setQ(MyConstant.PIC_ENTITY + "");
        data.setCount(count + "");
        data.setPlatform(platform);
        data.setLongitude(longitude + "");
        data.setLatitude(latitude + "");
        data.setTag(tag);
        data.setShowtopbar(showtopbar);
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);
    }

    public RequestParams getChefListLikeObj(Activity activity, String homeProv, String curProv, int pageIndex, int pageSize,
                                            int count, String longitude, String latitude) {
        data.setHomeprov(homeProv);
        data.setCurprov(curProv);
        data.setPageIndex(pageIndex + "");
        data.setPageSize(pageSize + "");
        int iw = MainTools.getwidth(activity) / 5 * 4;
        int ih = iw / 3 * 2;
        data.setW(iw + "");
        data.setH(ih + "");
        data.setQ(MyConstant.PIC_ENTITY + "");
        data.setCount(count + "");
        data.setPlatform(platform);
        data.setLongitude(longitude + "");
        data.setLatitude(latitude + "");
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);
    }

    public RequestParams getReadObj(String ncid, String mark) {
        data.doPrepare();
        data.setMark(mark);
        data.setNcids(ncid);
        return desParam(data);
    }

    public RequestParams getChefListLikeMostObj(Activity activity, String curProv, int pageIndex, int pageSize,
                                                int count, String longitude, String latitude) {
        data.setCurprov(curProv);
        data.setPageIndex(pageIndex + "");
        data.setPageSize(pageSize + "");
        int iw = MainTools.getwidth(activity) / 5 * 4;
        int ih = iw / 3 * 2;
        data.setW(iw + "");
        data.setH(ih + "");
        data.setQ(MyConstant.PIC_ENTITY + "");
        data.setCount(count + "");
        data.setPlatform(platform);
        data.setLongitude(longitude + "");
        data.setLatitude(latitude + "");
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        return desParam(data);
    }

    /**
     * 点赞x
     *
     * @param chefids hehe
     * @param status  explain
     * @return data
     */
    public RequestParams getLikeObj(String chefids, String status) {

        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setChefids(chefids);
        data.setStatus(status);
        return desParam(data);
    }

    public RequestParams getCookBookListObj(Activity activity, String chefids, String tag, String whenIndex) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setChefids(chefids);
        int iw = MainTools.getwidth(activity) / 5 * 4;
        int ih = iw / 5 * 3;
        data.setW(iw + "");
        data.setH(ih + "");
        data.setQ(MyConstant.PIC_ENTITY + "");
        data.setTag(tag);
        data.setWhenindex(whenIndex);
        return desParam(data);
    }

    public RequestParams getCookBookListObjPart(String chefids, String tag) {
        doPrepare();
        data.setVersionname(MyConstant.APP_VERSION_NAME);
        data.setChefids(chefids);
        data.setW(100 + "");
        data.setH(100 + "");
        data.setQ(65 + "");
        data.setTag(tag);
        return desParam(data);
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getShowtopbar() {
        return showtopbar;
    }

    public void setShowtopbar(String showtopbar) {
        this.showtopbar = showtopbar;
    }

    public String getConsumemin() {
        return consumemin;
    }

    public void setConsumemin(String consumemin) {
        this.consumemin = consumemin;
    }

    public String getMids() {
        return mids;
    }

    public void setMids(String mids) {
        this.mids = mids;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUserids() {
        return userids;
    }

    public void setUserids(String userids) {
        this.userids = userids;
    }

    public String getChefids() {
        return chefids;
    }

    public void setChefids(String chefids) {
        this.chefids = chefids;
    }

    public String getCbids() {
        return cbids;
    }

    public void setCbids(String cbids) {
        this.cbids = cbids;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHomeprov() {
        return homeprov;
    }

    public void setHomeprov(String homeprov) {
        this.homeprov = homeprov;
    }

    public String getHomecity() {
        return homecity;
    }

    public void setHomecity(String homecity) {
        this.homecity = homecity;
    }

    public String getCurprov() {
        return curprov;
    }

    public void setCurprov(String curprov) {
        this.curprov = curprov;
    }

    public String getCurcity() {
        return curcity;
    }

    public void setCurcity(String curcity) {
        this.curcity = curcity;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUserinfoids() {
        return userinfoids;
    }

    public void setUserinfoids(String userinfoids) {
        this.userinfoids = userinfoids;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressinfo() {
        return addressinfo;
    }

    public void setAddressinfo(String addressinfo) {
        this.addressinfo = addressinfo;
    }

    public String getNativeland() {
        return nativeland;
    }

    public void setNativeland(String nativeland) {
        this.nativeland = nativeland;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherprovs() {
        return otherprovs;
    }

    public void setOtherprovs(String otherprovs) {
        this.otherprovs = otherprovs;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDaids() {
        return daids;
    }

    public void setDaids(String daids) {
        this.daids = daids;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public String getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddrdetail() {
        return addrdetail;
    }

    public void setAddrdetail(String addrdetail) {
        this.addrdetail = addrdetail;
    }

    public String getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault;
    }

    public String getOlids() {
        return olids;
    }

    public void setOlids(String olids) {
        this.olids = olids;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCnids() {
        return cnids;
    }

    public void setCnids(String cnids) {
        this.cnids = cnids;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getWxuserid() {
        return wxuserid;
    }

    public void setWxuserid(String wxuserid) {
        this.wxuserid = wxuserid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getNcids() {
        return ncids;
    }

    public void setNcids(String ncids) {
        this.ncids = ncids;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTagids() {
        return tagids;
    }

    public void setTagids(String tagids) {
        this.tagids = tagids;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getPlatform_version() {
        return platform_version;
    }

    public void setPlatform_version(String platform_version) {
        this.platform_version = platform_version;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLoc_detail() {
        return loc_detail;
    }

    public void setLoc_detail(String loc_detail) {
        this.loc_detail = loc_detail;
    }

    public String getLoc_simple() {
        return loc_simple;
    }

    public void setLoc_simple(String loc_simple) {
        this.loc_simple = loc_simple;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getInvitecode() {
        return invitecode;
    }

    public void setInvitecode(String invitecode) {
        this.invitecode = invitecode;
    }

    public String getWhenindex() {
        return whenindex;
    }

    public void setWhenindex(String whenindex) {
        this.whenindex = whenindex;
    }

    private void init() {
        mids = null;
        token = null;
        platform = null;// 平台

        userids = null;// 用户ids
        chefids = null;// 主厨ids
        cbids = null;// 菜谱ids
        status = null;
        homeprov = null;// 家乡所在省份
        homecity = null;// 家乡所在的市或区
        curprov = null;// 当前所在省份
        curcity = null;// 当前所在的市或区
        count = null;
        w = null;
        h = null;
        q = null;
        pageIndex = null;
        pageSize = null;

        // userinfo
        username = null;
        pwd = null;
        userinfoids = null;
        role = null;
        nickname = null;
        address = null;
        addressinfo = null;
        nativeland = null;
        mobile = null;
        idcard = null;
        name = null;
        otherprovs = null;
        email = null;
        longitude = null;// 经度
        latitude = null;// 纬度

        price = null;

        daids = null;// 地址号
        content = null;// 内容
        smscode = null;// 验证码
        versioncode = null;
        versionname = null;
        score = null;

        prov = null;
        city = null;
        town = null;
        addrdetail = null;
        isdefault = null;
        olids = null;
        search = null;
        code = null;
        tag = null;
        cnids = null;
        age = null;
        version = null;
        open = null;
        openid = null;
        access_token = null;
        wxuserid = null;
        imei = null;
        ncids = null;
        gender = null;
        avatar = null;
        versionname = null;
        type = null;
        tagids = null;
        story = null;
        platform_version = null;
        loc_detail = null;
        distance = null;
        loc_simple = null;
        consumemin = null;
        showtopbar = null;
        coordinate = null;
        device_model = null;
        mark = null;
        invitecode = null;
        whenindex = null;
    }


}
