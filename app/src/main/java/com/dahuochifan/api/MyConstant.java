package com.dahuochifan.api;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.dahuochifan.bean.User;

public class MyConstant {
    public static final boolean ISPREPARE = true;
    //    public static final boolean ISLOG = false;
    // public static final String URL_MAIN="http://192.168.34.20:8082";
    public static final boolean NOTTEST = true;// 是否是测试环境
    //     public static final String URL_MAIN = "http://192.168.59.105:8082";
    public static final String URL_VERSION = "v3";
    public static final String PLATFORM_VERSION = "2.2";//检查更新提交的版本号
    public static String APP_VERSION_NAME = "2.2";
//    public static final String URL_MAIN = "http://m.dahuochifan.com";
            public static final String URL_MAIN = "http://mobile.dahuochifan.com:8082";
    public static final String URL_LOGIN = URL_MAIN + "/dh/api/" + URL_VERSION + "/user/login";
    public static final String URL_REGISTER = URL_MAIN + "/dh/api/" + URL_VERSION + "/user/reg";
    public static final String URL_CHANGEPWD = URL_MAIN + "/dh/api/" + URL_VERSION + "/user/changePwd";
    //    public static final String URL_MAINTOPPIC = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/top";// 首界面厨师
    public static final String URL_MAINTOPLIKE = URL_MAIN + "/dh/api/" + URL_VERSION + "/chefLike/like";// 点赞
    public static final String URL_MAINTOPCOLLECT = URL_MAIN + "/dh/api/" + URL_VERSION + "/chefCollect/collect";// 收藏
    public static final String URL_MAINCOOKLIST = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/page"; // 主厨列表
    //    public static final String URL_GETCOOKLIST = URL_MAIN + "/dh/api/" + URL_VERSION + "/cookbook/list"; // 获取主厨菜单（废弃）
//    public static final String URL_GETCUISINE = URL_MAIN + "/dh/api/" + URL_VERSION + "/cookbook/listForOrder"; // 食客获取主厨菜单
    public static final String URL_GETCUISINE=URL_MAIN+ "/dh/api/" + URL_VERSION + "/orderList/showSubmitView";//获取主厨菜单
//    public static final String URL_GETCUISINESELF = URL_MAIN + "/dh/api/" + URL_VERSION + "/cookbook/listForChef"; // 主厨获取主厨菜单
    public static final String URL_GETCOLLECTLIST = URL_MAIN + "/dh/api/" + URL_VERSION + "/chefCollect/page";// 获取收藏列表
    public static final String URL_GETPERSONINFO = URL_MAIN + "/dh/api/" + URL_VERSION + "/userInfo/get"; // 获取个人信息
    public static final String URL_GETSHIKELIST = URL_MAIN + "/dh/api/" + URL_VERSION + "/orderList/dinerpage";// 获取食客列表
    public static final String URL_GETORDERLIST = URL_MAIN + "/dh/api/" + URL_VERSION + "/orderList/mypage";// 获取三类订单列表
    public static final String URL_POSTORDER = URL_MAIN + "/dh/api/" + URL_VERSION + "/orderList/add";// 下订单
    public static final String URL_GETMYADDRESS = URL_MAIN + "/dh/api/" + URL_VERSION + "/deliveryAddr/list";// 获取地址列表
    //    public static final String URL_GETMYADDRESS_DEFAULT = URL_MAIN + "/dh/api/" + URL_VERSION + "/deliveryAddr/getDefOrNum";// 获取默认地址
    public static final String URL_POSTDEFAULTADD = URL_MAIN + "/dh/api/" + URL_VERSION + "/deliveryAddr/def";// 设置默认地址
    public static final String URL_GETYZM = URL_MAIN + "/dh/api/" + URL_VERSION + "/user/sendSMS";
    public static final String URL_USERSMS2 = URL_MAIN + "/dh/api/" + URL_VERSION + "/user/sendSMS2";// 忘记密码的验证码
    public static final String URL_USER_CHECK_CODE = URL_MAIN + "/dh/api/" + URL_VERSION + "/user/checkCode";
    public static final String URL_FEEDBACK = URL_MAIN + "/dh/api/" + URL_VERSION + "/feedback/add";// 提交反馈
    public static final String URL_UPDATE = URL_MAIN + "/dh/api/" + URL_VERSION + "/app/defapk";// 检查更新
    public static final String URL_GETCOMMENTSLIST = URL_MAIN + "/dh/api/" + URL_VERSION + "/cookbookComment/page";// 获取评论列表
    public static final String URL_POSTCOMMENT = URL_MAIN + "/dh/api/" + URL_VERSION + "/cookbookComment/add";
    public static final String URL_ADDADDR = URL_MAIN + "/dh/api/" + URL_VERSION + "/deliveryAddr/add";// 添加地址
    public static final String URL_EDITADDR = URL_MAIN + "/dh/api/" + URL_VERSION + "/deliveryAddr/edit";// 编辑地址
    public static final String URL_FOLLOWORDER = URL_MAIN + "/dh/api/" + URL_VERSION + "/orderList/option";// 订单跟踪操作
    public static final String URL_UPDATEPROV = URL_MAIN + "/dh/api/" + URL_VERSION + "/userInfo/edit";// 修改个人信息
    public static final String URL_SEARCH = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/search";// 搜索主厨
    public static final String URL_MYEATEN = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/eaten";
    public static final String URL_APPLY = URL_MAIN + "/dh/api/" + URL_VERSION + "/chefApply/add";// 申请主厨
    public static final String URL_ADDR_DEL = URL_MAIN + "/dh/api/" + URL_VERSION + "/deliveryAddr/del";// 删除地址
    public static final String URL_CHEFNOTIFY_LIST = URL_MAIN + "/dh/api/" + URL_VERSION + "/chefNotify/list";// 获取公告列表
    public static final String URL_CHEFNOTIFY_ADD = URL_MAIN + "/dh/api/" + URL_VERSION + "/chefNotify/add";// 添加公告
    public static final String URL_CHEFNOTIFY_DEL = URL_MAIN + "/dh/api/" + URL_VERSION + "/chefNotify/del";// 删除公告
    public static final String URL_CBTAG_LIST = URL_MAIN + "/dh/api/" + URL_VERSION + "/cbtag/list";// 获取所有菜系
    public static final String URL_COOKBOOK_EDIT = URL_MAIN + "/dh/api/" + URL_VERSION + "/cookbook/edit";// 更新私房菜
    public static final String URL_COOKBOOK_OPEN = URL_MAIN + "/dh/api/" + URL_VERSION + "/cookbook/open";// 打开/关闭菜谱
    public static final String URL_CHEF_OPEN = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/open";// 打开/关闭店
    public static final String URL_AVATAR = URL_MAIN + "/dh/api/" + URL_VERSION + "/upload/avatar";// 用户上传头像
    public static final String URL_AD = URL_MAIN + "/dh/api/" + URL_VERSION + "/activity/list";// 活动列表
    public static final String URL_MESSAGE = URL_MAIN + "/dh/api/" + URL_VERSION + "/newsCenter/page";// 推送消息列表
    public static final String URL_MESSAGEDETAIL = URL_MAIN + "/dh/api/" + URL_VERSION + "/newsCenter/get";// 获取消息详情
    //    public static final String URL_ACTIVITIES_BEST = URL_MAIN + "/dh/api/" + URL_VERSION + "/activity/best"; // 获取活动信息
    public static final String URL_WECHAT_LOGIN = URL_MAIN + "/dh/api/" + URL_VERSION + "/user/wxlogin";
    public static final String URL_GETPAY = URL_MAIN + "/dh/api/" + URL_VERSION + "/income/get";// 获取收入
    public static final String URL_GETCHEF = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/get";// 获取主厨详情
    public static final String URL_ADDLOG = URL_MAIN + "/dh/api/" + URL_VERSION + "/feedback/addLog";
    public static final String URL_MSG_DEL = URL_MAIN + "/dh/api/" + URL_VERSION + "/newsCenter/del";// 删除推送消息
    public static final String URL_TAGS_ADD = URL_MAIN + "/dh/api/" + URL_VERSION + "/userTag/add";// 修改标签
    //    public static final String URL_CB_LIKE = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/mytagpage";// 喜欢的菜系
    public static final String URL_CHEF_EDIT = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/edit";// 提交主厨故事
    public static final String URL_GET_STORY = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/getStory";// 提交主厨故事
    public static final String APP_DH_WX_URL = URL_MAIN + "/dh/api/" + URL_VERSION + "/wxpay";// 微信的回调接口
    public static final String APP_DH_ALI_URL = URL_MAIN + "/dh/api/" + URL_VERSION + "/alipay";
    public static final String APP_CHEF_MAP_URL = URL_MAIN + "/dh/api/" + URL_VERSION + "/chef/searchMap";//搜索地图上的厨师
    public static final String APP_MAIN_PIC = URL_MAIN + "/dh/api/" + URL_VERSION + "/bootPage/list";//获取首界面pic
    public static final String GET_ORDER_DETAIL = URL_MAIN + "/dh/api/" + URL_VERSION + "/orderList/get";
    public static final String GET_DISCOUNT_ALL_LIST = URL_MAIN + "/dh/api/" + URL_VERSION + "/coupon/user/page";//获取可用不可用的优惠券列表
    public static final String GET_DISCOUNT_ENABLE = URL_MAIN + "/dh/api/" + URL_VERSION + "/coupon/user/listForConsumemin";//获取当前消费可用的优惠券
//    public static final String MARK_MSG = URL_MAIN + "/dh/api/" + URL_VERSION + "/newsCenter/mark";//标记信息已读


    public static final String APP_SPF_NAME = "dahuochifan";
    public static final String APP_SPF_OTHERPROV = "otherprov";
    public static final String APP_SPF_USERID = "userid";
    public static final String APP_SPF_USERTOKEN = "token";
    public static final String APP_SPF_LOGINPHONE = "loginphone";
    public static final String APP_SPF_TEMPUSER = "tempuser";
    public static final String APP_SPF_CHEFIDS = "chefids";
    public static final String APP_SPF_ADDRESSINFO = "addressinfo";
    public static final String APP_SPF_HOMEPROV = "homeprov";
    public static final String APP_SPF_HOMECITY = "homecity";
    public static final String APP_SPF_CURPROV = "curprov";
    public static final String APP_SPF_CURCITY = "curcity";
    public static final String APP_SPF_NICKNAME = "nickname";
    public static final String APP_SPF_STATUS = "status";
    public static final String APP_SPF_IDS = "ids";
    public static final String APP_SPF_BG = "bg";
    public static final String APP_SPF_ROLE = "role";
    public static final String APP_SPF_AVATAR = "avatar";
    public static final String APP_SPF_MOBILE = "mobile";
    public static final String APP_SPF_ISLOGIN = "isloign";
    public static final String APP_SPF_LONGITUDE = "longitude";
    public static final String APP_SPF_LATITUDE = "latitude";
    public static final String APP_SPF_LOCATION = "location";
    public static final String APP_SPF_GDCITY = "gdcity";
    public static final String APP_SPF_GDDISTRICT = "gddistrict";
    public static final String APP_SPF_INSTALL = "install";
    public static final String APP_SPF_AGE = "age";
    public static final String APP_SPF_ISWX = "iswx";
    public static final String APP_SPF_REGISTERID = "jp_regid";
    public static final String APP_SPF_POINAME = "poiName";
    public static final String APP_SPF_PAYTYPE = "paytype";

    public static final String PAY_NO = "pay_no";// 未付款
    public static final String PAY_SUCCESS = "pay_success";// 付款成功
    public static final String REFUND_APPLY = "refund_apply";// 退款中
    public static final String REFUND_SUCCESS = "refund_success";// 退款成功
    public static final String PAY_WATING = "pay_success_waiting";//付款成功待确认

    public static final String ANCHORDERSHOW = "AnchorOrderShow";
    public static final String ANCHORDERHIDE = "AnchorOrderHide";
    public static final String ANCHCHEFSHOW = "AnchorChefShow";
    public static final String ANCHCHEFHIDE = "AnchorChefHide";


    public static final String QUALITY = "65";

    public static final int SDK_PAY_FLAG=101;
    public static final int USER_AGE = 1001;
    public static final int REQUESTCODEQ_TIME_1 = 1006;
    public static final int REQUESTCODEQ_CHOOSE = 1009;

    public static final int REQUESTCODEQ_AGE = 1012;
    public static final int REQUESTCODEQ_MOBI = 1013;

    public static final int MYHANDLER_CODE1 = 2001;
    public static final int MYHANDLER_CODE2 = 2002;
    public static final int MYHANDLER_CODE3 = 2003;
    public static final int MYHANDLER_CODE4 = 2004;
    public static final int MYHANDLER_CODE6 = 2006;
    public static final int MYHANDLER_ERROR=2007;
    public static final int MYHANDLER_WARN=2008;
    public static final int MYHANDLER_SUCCESS=2009;
    public static final int MYHANDLER_WAIT=2010;

    public static final int EVENTBUS_ORDER_MOVEONE = 3010;
    public static final int EVENTBUS_CHEF_MOVEONE = 3011;
    public static final int EVENTBUS_ORDER_ONE = 3004;
    public static final int EVENTBUS_ORDER_TWO = 3005;
    public static final int EVENTBUS_ORDER_THREE = 3006;
    public static final int EVENTBUS_CHEF_ONE = 3007;
    public static final int EVENTBUS_CHEF_TWO = 3008;
    public static final int EVENTBUS_CHEF_THREE = 3009;
    public static final int EVENTBUS_CONFIRM_ORDER = 3010;
    public static final int EVENTBUS_MESSAGE = 3011;
    public static final int EVENTBUS_PAY = 3012;
    public static final int EVENTBUS_ADD_ADDR = 3013;
    public static final int EVENTBUS_DISCOUNT = 3014;
    public static final int EVENTBUS_REFRESH_ALL = 3015;
    public static final int EVENTBUS_CHOOSE_ADDR = 3016;
    public static final int EVENTBUS_WECHAT_PAY = 3017;
    public static final int EVENTBUS_PAY_CANCEL = 3018;
    public static final int EVENTBUS_PAY_ERROR = 3019;

    public static final String TOTAL_TYPE = "TYPE_TOTAL";
    public static final String PRICE_TYPE = "TYPE_PRICE";
    public static final double MPRICE = 0.01;
    public static final int PIC_ENTITY = 75;
    // public static final int EVENTBUSNICKNAME=3001;
    public static User user = null;
    public static SQLiteDatabase db;

    public static final String device = Build.MODEL.replace(" ", "");
    public static final String poiStr = "120201|120100|120302|190301";
//    public static final String MYPLAT = "Android";
//120201|120100|120203|120300|120301|120302|190301|160100
    /**
     * 正在检查是否有新版本
     **/
    public final static int msg_checkingversion = 110;
    /**
     * 发现软件新版本
     **/
    public final static int msg_findnewversion = 111;
    /**
     * 未发现软件新版本
     **/
    public final static int msg_notfindnewversion = 112;
    /**
     * 评论recyclerview高度
     */
    public final static int comment_height = 3018;

    public static String PUCLIC_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOilTXrBT7+n7oud"
            + "M8cQsIcZ66MFjvbHo8i9D04MlyiqoNKuPySXv+cGnojnJBSGVaXeWoqe33g9+8Hb"
            + "QmWoIszUXoD0ETZFL8uATeuVXVI4bKCPf4omrRQ8gO8iWp00GexjSB+vR2SXo40p"
            + "ZqN+OYew/jN8Y+8WBge0AdQ2oppZAgMBAAECgYAEizX4L85e7+i40VkxiiHogZkY"
            + "lgM0zrFkOk0SOSy6npqFguG41T3qRTbTdbA+tmD13GHoUzIKJyvDnYSud5o2XGR4"
            + "p5jjK4t79E0Ao8jYKMxRwxvHX4PWuqLDzZGHlPO1zexOOBjrUg7B/g0e7z7F8pZN"
            + "qiKOAUFK7H4Lf7oQAQJBAP+FBy0SIeYhlJDQtU6ta0VFppKH/Su5VSqVgf5ZAOBD"
            + "WZ0AqtZfyE5R1lfjQhvKcwvFRDeLAO5S0ptpopa73Z0CQQDpFUQp41N8H+otsp+3"
            + "/YNr20Oz29q4tbKTOInfvWFuYTjZDqDERu1KgOd04/u2F6Rfknv7NBoxcMWKADIR"
            + "/TDtAkAl9JW/TS056Q0PgeEcwcfob1Mx+v9RamNLQxAfPwtRRM/f8YRu+aVgdOmY"
            + "/ooIZMc4HWJnub82p5jfOw7KwzdFAkEAsruDBJf0eddDTP2Paph1Qazm0rIBm+iA"
            + "4lHVsANaBjl5TUDhWQGGjr4gPstgRrcEbeE3P18pwsXWRMd3ncHI1QJAQKt8Iyhy"
            + "aflqKk1VFo+A19SluBvse9xdZQkaNkIGZiuPYDtnBEl56Z59/A7DK38sOsN0XzAs"
            + "MX7ZROHYznc44g==";
}
