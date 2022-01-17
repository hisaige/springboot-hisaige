package com.hisaige.core.util;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author chenyj
 * 2020/1/27 - 15:41.
 **/
public class Md5Utils {

    /**
     * 获取文件MD5值
     * @param file 文件
     * @return MD5值
     * @throws NoSuchAlgorithmException 异常抛出
     * @throws IOException 异常抛出
     */
    public static String getMD5ByFile(File file) throws NoSuchAlgorithmException, IOException {

        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest())).toUpperCase();
        }
    }

    /**
     * 获取文件MD5值
     * @param multipartFile 文件
     * @return MD5值
     * @throws NoSuchAlgorithmException 异常抛出
     * @throws IOException 异常抛出
     */
    public static String getMD5ByMultipartFile (MultipartFile multipartFile) throws NoSuchAlgorithmException, IOException {

        byte[] uploadBytes = multipartFile.getBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(uploadBytes);
        return new String(Hex.encodeHex(digest)).toUpperCase();
    }

    /**
     * 求一个字符串的md5值
     * @param target 字符串
     * @return md5 value
     */
    public static String getMD5ByStr(String target) {
        return DigestUtils.md5Hex(target).toUpperCase();
    }

}

