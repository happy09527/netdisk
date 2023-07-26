package com.easypan.utils;

import cn.hutool.crypto.digest.DigestUtil;
import com.easypan.entity.constants.Constants;
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
    /**
     * @date: 2023/7/25 10:14
     * 字符串是否为空
     **/
    public static boolean isEmpty(String str) {

        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }
    /**
     * @date: 2023/7/25 10:14
     * md5加密字符串
     **/
    public static String encodeByMD5(String text) {
        return isEmpty(text)? null : DigestUtil.md5Hex(text);
    }
    /**
     * @date: 2023/7/25 10:14
     * 判断路径是否正确。不含有../ ..\
     **/
    public static boolean pathIsOk(String filePath) {
        if(StringUtils.isEmpty(filePath)){
            return true;
        }
        if(filePath.contains("../") || filePath.contains("..\\")){
            return false;
        }
        return true;
    }
    /**
     * @date: 2023/7/25 10:15
     * 文件重命名
     **/
    public static String rename(String fileName) {
        // 获取不加后缀的文件名
        String fileNameReal = getFileNameNoSuffix(fileName);
        // 获取文件的后缀名
        String suffix = getFileSuffix(fileName);
        return fileNameReal + "_" + getRandomString(Constants.LENGTH_5) + suffix;
    }
    /**
     * @date: 2023/7/25 10:15
     * 获取文件后缀名
     **/
    public static String getFileSuffix(String fileName) {
        Integer index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        String suffix = fileName.substring(index);
        return suffix;
    }

    /**
     * @date: 2023/7/25 10:18
     * 获取不加后缀的文件名
     **/
    public static String getFileNameNoSuffix(String fileName) {
        Integer index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        fileName = fileName.substring(0, index);
        return fileName;
    }

}
