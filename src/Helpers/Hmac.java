package Helpers;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class Hmac {
	public static byte[] getKeyedDigest(byte[] cedula, SecretKey key, String algoritmo) {
		try {
	        Mac mac = Mac.getInstance(algoritmo);
	        mac.init(key);
	        byte[] bytes = mac.doFinal(cedula);
	        return bytes;
		} catch (Exception e) {
			return null;
		}
	}
}
