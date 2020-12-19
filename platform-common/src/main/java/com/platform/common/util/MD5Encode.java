package com.platform.common.util;

import java.security.MessageDigest;

/**
 * @（#）MD5Encode.java
 * 
 * @description: MD5加密-sha1
 * @author: 王佳 2014/03/28
 * @version: Version No.
 * @modify: MODIFIER'S NAME YYYY/MM/DD
 * @Copyright: 王佳
 */
public class MD5Encode {

	/**
	 * 加密字符串
	 * 
	 * @param str
	 *            需加密的字符串
	 * @return 加密后的字符串
	 */
	public static String encode(String str,String encodeType) {

		try {
			MessageDigest md = MessageDigest.getInstance(encodeType);

			md.update(str.getBytes("UTF-8"));

			byte[] hash = md.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				if ((0xff & hash[i]) < 0x10) {
					hexString.append("0"
							+ Integer.toHexString((0xFF & hash[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}
			return hexString.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	public  static void main(String[] args) {
		
		System.out.println(encode("123456","md5"));
	}
	
	
	public static boolean isMatch(String password,String inputString){
		if(password.equals(encode(inputString,"md5"))){
			return true;
		}else {
			return false;
		}
	}
}
