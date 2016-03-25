package com.dahuochifan.utils.newutils;

/**
 * Created by Morel on 2015/12/15.
 * convert utils
 */
public class DhConvertUtils {
    private static DhConvertUtils INSTANCE;

    /**
     * 保证只有一个CrashHandler实例
     */
    private DhConvertUtils() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static DhConvertUtils getInstance() {
        if (INSTANCE == null)
            INSTANCE = new DhConvertUtils();
        return INSTANCE;
    }

    /**
     * @param value        被转换对象
     * @param defaultValue 转换出错给出的默认值
     * @return 对象转换出的int数据
     * 类型安全转换为int
     */
    public final int convertToInt(Object value, int defaultValue) {
        if (value == null || "".equals(value.toString().trim())) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            try {
                return Double.valueOf(value.toString()).intValue();
            } catch (Exception e1) {
                return defaultValue;
            }
        }
    }

    /**
     * @param value        被转换对象
     * @param defaultValue 转换出错给出的默认值
     * @return 对象转换出的数据
     * 类型安全转换为float
     */
    public final float convertToFloat(Object value, float defaultValue) {
        if (value == null || "".equals(value.toString().trim())) {
            return defaultValue;
        }
        try {
            return Float.valueOf(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public final Double convertToDouble(Object value, double defaultValue) {
        if (value == null || "".equals(value.toString().trim())) {
            return defaultValue;
        }
        try {
            return Double.valueOf(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
