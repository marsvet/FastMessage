package edu.cauc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

	public static byte[] calcSha1(byte[] bytes) {
		MessageDigest digest = null;

		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		digest.update(bytes);

		return digest.digest();
	}

	public static byte[] calcSha1(String str) {
		return calcSha1(str.getBytes());
	}

}
