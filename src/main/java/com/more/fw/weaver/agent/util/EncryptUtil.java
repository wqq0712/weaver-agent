package com.more.fw.weaver.agent.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class EncryptUtil {
	public static boolean checkPassword(String encryptString, String paramString2) throws IOException, NoSuchAlgorithmException {
		boolean bool = false;
		BASE64Decoder localBASE64Decoder = new BASE64Decoder();
		byte[] arrayOfByte1 = localBASE64Decoder.decodeBuffer(encryptString);
		byte[] arrayOfByte2 = new byte[12];
		System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, 12);
		MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
		localMessageDigest.update(arrayOfByte2);
		localMessageDigest.update(paramString2.getBytes("UTF8"));
		byte[] arrayOfByte3 = localMessageDigest.digest();
		byte[] arrayOfByte4 = new byte[arrayOfByte1.length - 12];
		System.arraycopy(arrayOfByte1, 12, arrayOfByte4, 0,
				arrayOfByte1.length - 12);
		if (Arrays.equals(arrayOfByte3, arrayOfByte4))
			bool = true;
		else
			bool = false;
		
		return bool;
	}

	public static String encodePassword(String paramString) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String str = null;
		
		SecureRandom localSecureRandom = new SecureRandom();
		byte[] arrayOfByte1 = new byte[12];
		localSecureRandom.nextBytes(arrayOfByte1);
		MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
		localMessageDigest.update(arrayOfByte1);
		localMessageDigest.update(paramString.getBytes("UTF8"));
		byte[] arrayOfByte2 = localMessageDigest.digest();
		byte[] arrayOfByte3 = new byte[arrayOfByte1.length
				+ arrayOfByte2.length];
		System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0,
				arrayOfByte1.length);
		System.arraycopy(arrayOfByte2, 0, arrayOfByte3,
				arrayOfByte1.length, arrayOfByte2.length);
		BASE64Encoder localBASE64Encoder = new BASE64Encoder();
		str = localBASE64Encoder.encode(arrayOfByte3);
		
		return str;
	}

	public static String encodeMD5(String paramString) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return encodePassword(paramString);
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		//0d066df2-9d07-4a4e-8550-f02ac4d13358
		//System.out.println(UUID.randomUUID().toString());
		
		String str = encodePassword("0d066df2-9d07-4a4e-8550-f02ac4d13358" + "0361b653334f4b21806f72ce7aa26599");
		System.out.println("pwd = " + str);
		boolean bool = checkPassword(str, "0d066df2-9d07-4a4e-8550-f02ac4d13358" + "0361b653334f4b21806f72ce7aa26599");
		System.out.println("good = " + ((bool) ? "yes" : "no"));
		
		//MM3D3GBgXG+g93TK5HDS15JCLobI9w6GjhlgKA==
		
		//0d066df2-9d07-4a4e-8550-f02ac4d133580361b653334f4b21806f72ce7aa26599
		//MM3D3GBgXG+g93TK5HDS15JCLobI9w6GjhlgKA==
		
	}
}