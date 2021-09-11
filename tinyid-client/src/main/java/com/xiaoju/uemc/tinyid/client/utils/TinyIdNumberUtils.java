package com.xiaoju.uemc.tinyid.client.utils;

/**
 * @Author du_imba
 *
 * 整型工具
 * 主要用于将字符转成整型
 */
public class TinyIdNumberUtils {

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
