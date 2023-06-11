package com.runescape.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class EncryptUtils {
	
	private static final String DEFAULT_ENCODING = "UTF-8";

	public static String base64encode(String text) {
		try {
			return Base64.getEncoder().encodeToString(text.getBytes(DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static String base64decode(String text) {
		try {
			return new String(Base64.getDecoder().decode(text), DEFAULT_ENCODING);
		} catch (IOException e) {
			return null;
		}
	}

	public static String xorMessage(String message, String key) {
		try {
			if (message == null || key == null)
				return null;

			char[] keys = key.toCharArray();
			char[] mesg = message.toCharArray();

			int ml = mesg.length;
			int kl = keys.length;
			char[] newmsg = new char[ml];

			for (int i = 0; i < ml; i++) {
				newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
			}

			return new String(newmsg);
		} catch (Exception e) {
			return null;
		}
	}

}
