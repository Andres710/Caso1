package Helpers;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class Seguridad {
	 public static byte[] a(byte[] paramArrayOfByte, Key paramKey, String paramString) throws Exception
	  {
	    paramString = paramString + ((paramString.equals("DES")) || (paramString.equals("AES")) ? "/ECB/PKCS5Padding" : "");
	    Cipher localCipher = Cipher.getInstance(paramString);
	    localCipher.init(1, paramKey);
	    return localCipher.doFinal(paramArrayOfByte);
	  }
	  
	  public static byte[] b(byte[] paramArrayOfByte, Key paramKey, String paramString) throws Exception
	  {
	    paramString = paramString + ((paramString.equals("DES")) || (paramString.equals("AES")) ? "/ECB/PKCS5Padding" : "");
	    Cipher localCipher = Cipher.getInstance(paramString);
	    localCipher.init(2, paramKey);
	    return localCipher.doFinal(paramArrayOfByte);
	  }
	  
	  public static byte[] c(byte[] paramArrayOfByte, Key paramKey, String paramString) throws Exception
	  {
	    Cipher localCipher = Cipher.getInstance(paramString);
	    localCipher.init(1, paramKey);
	    return localCipher.doFinal(paramArrayOfByte);
	  }
	  
	  public static byte[] d(byte[] paramArrayOfByte, Key paramKey, String paramString) throws Exception
	  {
	    Cipher localCipher = Cipher.getInstance(paramString);
	    localCipher.init(2, paramKey);
	    return localCipher.doFinal(paramArrayOfByte);
	  }
	  
	  public static byte[] e(byte[] paramArrayOfByte, Key paramKey, String paramString) throws Exception
	  {
	    Mac localMac = Mac.getInstance(paramString);
	    localMac.init(paramKey);
	    byte[] arrayOfByte = localMac.doFinal(paramArrayOfByte);
	    return arrayOfByte;
	  }
	  
	  public static boolean a(byte[] paramArrayOfByte1, Key paramKey, String paramString, byte[] paramArrayOfByte2) throws Exception
	  {
	    byte[] arrayOfByte = e(paramArrayOfByte1, paramKey, paramString);
	    if (arrayOfByte.length != paramArrayOfByte2.length)
	    {
	      System.out.println("longitud");
	      return false;
	    }
	    if (!Arrays.equals(arrayOfByte, paramArrayOfByte2))
	    {
	      System.out.println("arrays");
	      return false;
	    }
	    for (int i = 0; i < arrayOfByte.length; i++) {
	      if ((arrayOfByte[i] & paramArrayOfByte2[i]) != paramArrayOfByte2[i])
	      {
	        System.out.println("comp");
	        return false;
	      }
	    }
	    return true;
	  }
	  
	  public static SecretKey a(String paramString) throws Exception
	  {
	    int i = 0;
	    if (paramString.equals("DES")) {
	      i = 64;
	    } else if (paramString.equals("AES")) {
	      i = 128;
	    } else if (paramString.equals("Blowfish")) {
	      i = 128;
	    } else if (paramString.equals("RC4")) {
	      i = 128;
	    }
	    if (i == 0) {
	      throw new NoSuchAlgorithmException();
	    }
	    KeyGenerator localKeyGenerator = KeyGenerator.getInstance(paramString, "BC");
	    localKeyGenerator.init(i);
	    SecretKey localSecretKey = localKeyGenerator.generateKey();
	    return localSecretKey;
	  }
	  
	  public static X509Certificate a(KeyPair paramKeyPair) throws Exception
	  {
	    PublicKey localPublicKey1 = paramKeyPair.getPublic();
	    PrivateKey localPrivateKey = paramKeyPair.getPrivate();
	    PublicKey localPublicKey2 = paramKeyPair.getPublic();
	    JcaX509ExtensionUtils localJcaX509ExtensionUtils = new JcaX509ExtensionUtils();
	    JcaX509v3CertificateBuilder localJcaX509v3CertificateBuilder = new JcaX509v3CertificateBuilder(new X500Name("CN=0.0.0.0, OU=None, O=None, L=None, C=None"), new BigInteger(128, new SecureRandom()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 8640000000L), new X500Name("CN=0.0.0.0, OU=None, O=None, L=None, C=None"), localPublicKey1);
	    localJcaX509v3CertificateBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, localJcaX509ExtensionUtils.createSubjectKeyIdentifier(localPublicKey1));
	    localJcaX509v3CertificateBuilder.addExtension(X509Extension.authorityKeyIdentifier, false, localJcaX509ExtensionUtils.createAuthorityKeyIdentifier(localPublicKey2));
	    return new JcaX509CertificateConverter().setProvider("BC").getCertificate(localJcaX509v3CertificateBuilder.build(new JcaContentSignerBuilder("MD5withRSA").setProvider("BC").build(localPrivateKey)));
	  }
	  
	  public static KeyPair a() throws Exception
	  {
	    KeyPairGenerator localKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
	    localKeyPairGenerator.initialize(1024, new SecureRandom());
	    return localKeyPairGenerator.generateKeyPair();
	  }
}
