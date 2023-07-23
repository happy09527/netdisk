package com.easypan.utils;

import cn.hutool.crypto.digest.DigestUtil;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description:
 */
public class StringUtils {
    /**
     * @date: 2023/7/22 9:00
     * @description: 生成随机验证字符串
     **/
    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }
    /**
     * @date: 2023/7/22 9:02
     * @description: 生成随机验证码
     **/
    public static final String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    public static boolean isEmpty(String str) {

        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static String encodeByMD5(String text) {
        return isEmpty(text)? null : DigestUtil.md5Hex(text);
    }

    public static boolean pathIsOk(String filePath) {
        if(StringUtils.isEmpty(filePath)){
            return true;
        }
        if(filePath.contains("../") || filePath.contains("..\\")){
            return false;
        }
        return true;
    }
}
