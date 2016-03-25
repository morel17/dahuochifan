# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\sdk\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#指定代码的压缩级别
-optimizationpasses 5
#包名不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化  不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v7.*
#忽略警告
-ignorewarning
#####################记录生成的日志数据,gradle build时在本项目根目录输出################
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
#####################记录生成的日志数据，gradle build时 在本项目根目录输出-end################
################混淆保护自己项目的部分代码以及引用的第三方jar包library#########################

-libraryjars libs/alipaySDK-20150818.jar
-libraryjars libs/AMap_2DMap_v2.6.0_20150916.jar
-libraryjars libs/AMap_Location_v1.4.1_20150917.jar
-libraryjars libs/AMap_Search_v2.6.0_20150914.jar
-libraryjars libs/jpush-sdk-release1.8.1.jar
-libraryjars libs/libammsdk.jar
-libraryjars libs/MobLogCollector.jar
-libraryjars libs/MobTools.jar
-libraryjars libs/renderscript-v8.jar
-libraryjars libs/ShareSDK-Core-2.6.2.jar
-libraryjars libs/ShareSDK-Wechat-2.6.2.jar
-libraryjars libs/ShareSDK-Wechat-Core-2.6.2.jar
-libraryjars libs/ShareSDK-Wechat-Favorite-2.6.2.jar
-libraryjars libs/ShareSDK-Wechat-Moments-2.6.2.jar
-libraryjars libs/sun.misc.BASE64Decoder.jar


#支付宝
-keep class com.alipay.android.app.IAliPay{*;}
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.lib.ResourceMap{*;}
#极光推送
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
# universal-image-loader 混淆
-dontwarn com.nostra13.universalimageloader.**
-keep class com.nostra13.universalimageloader.** { *; }
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.dahuochifan.bean.** { *; }
-keep class com.dahuochifan.core.** { *; }
-keep class com.dahuochifan.corenew.** { *; }
-keep class com.dahuochifan.model.** { *; }
#ButterKnife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}
## ----------------------------------
##      sharesdk
## ----------------------------------
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
## ----------------------------------
##      v8
## ----------------------------------
-keep class android.support.v8.renderscript.**{*;}
## ----------------------------------
##      Decoder
## ----------------------------------
-keep class Decoder.**{*;}
## ----------------------------------
##      eventbus
## ----------------------------------

# # -------------------------------------------
# #  ######## greenDao混淆  ##########
# # -------------------------------------------
-libraryjars libs/greendao-2.0.0.jar
-keep class de.greenrobot.dao.** {*;}
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

#如果有其它包有warning，在报出warning的包加入下面类似的-dontwarn 报名
-dontwarn com.amap.api.**
-dontwarn com.aps.**
#Location
-keep   class com.amap.api.location.**{*;}
-keep   class com.aps.**{*;}




