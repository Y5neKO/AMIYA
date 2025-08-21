package com.y5neko.amiya.util;

import java.util.List;

/**
 *  misc工具类
 */
public class MiscUtils {
    /**
     * 生成随机字符串
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String getRamdomStr(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * str.length());
            sb.append(str.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 转换List<String>为String[]
     * @param list 列表
     * @return 数组
     */
    public static String[] convertListToArray(List<String> list) {
        return list == null ? null : list.toArray(new String[0]);
    }
}
