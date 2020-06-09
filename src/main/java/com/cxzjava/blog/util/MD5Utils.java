package com.cxzjava.blog.util;

import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils{
    /**
     * MD5加密
     * @param str
     * @return
     */
    public static String code(String str){
        try {
            ///选择加密方式 MD5和SHA-1
            MessageDigest md = MessageDigest.getInstance("MD5");
            //使用指定的字节数组更新摘要
            md.update(str.getBytes());
            //信息摘要对象对字节数组进行摘要,得到摘要字节数组:
            byte[] byteDigest = md.digest();
            int i;
            StringBuffer buffer = new StringBuffer("");
            for(int offset = 0;offset < byteDigest.length;offset++){
                i = byteDigest[offset];
                if(i < 0)
                    i += 256;
                if(i < 16)
                    buffer.append(0);
                buffer.append(Integer.toHexString(i));
            }
            //32位加密
            return buffer.toString();
            //16位加密
//            return buffer.toString().substring(8,24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(code("123456"));
    }
}