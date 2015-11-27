package com.example.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 加密
 */
public class MD5Utils {
	public static String getPwdMd5(String password){
		try {
			//得到一个信息管理器
			MessageDigest messageDigest = MessageDigest.getInstance("md5");
			//将需要转换的字符串转换成一个字节数组
			byte[] bytes = messageDigest.digest(password.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : bytes){
				//与8进制位 做  与  运算
				int number = b & 0xff;
				String str = Integer.toHexString(number);
				if(str.length()==1){
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
}
