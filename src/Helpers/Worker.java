package Helpers;

import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.X509Certificate;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class Worker
{
  private static void a(Exception paramException)
  {
    System.out.println(paramException.getMessage());
    paramException.printStackTrace();
  }
  
  private static String a(BufferedReader paramBufferedReader) throws Exception 
  {
    String str = paramBufferedReader.readLine();
    System.out.println("<<CLNT: " + str);
    return str;
  }
  
  private static void a(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println(paramString);
    System.out.println(">>SERV: " + paramString);
  }
  
  public static void a(Socket paramSocket)
  {
    try
    {
      PrintWriter localPrintWriter = new PrintWriter(paramSocket.getOutputStream(), true);
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramSocket.getInputStream()));
      String str1 = a(localBufferedReader);
      if (!str1.equals("HOLA"))
      {
        a(localPrintWriter, "Error en el formato. Cerrando conexion");
        throw new FontFormatException(str1);
      }
      a(localPrintWriter, "OK");
      str1 = a(localBufferedReader);
      if ((!str1.contains(":")) || (!str1.split(":")[0].equals("ALGORITMOS")))
      {
        a(localPrintWriter, "Error en el formato. Cerrando conexion");
        throw new FontFormatException(str1);
      }
      String[] arrayOfString1 = str1.split(":");
      if ((!arrayOfString1[1].equals("Blowfish")) && (!arrayOfString1[1].equals("AES")) && (!arrayOfString1[1].equals("DES")) && (!arrayOfString1[1].equals("RC4")))
      {
        a(localPrintWriter, "ERROR");
        throw new NoSuchAlgorithmException();
      }
      if (!arrayOfString1[2].equals("RSA"))
      {
        a(localPrintWriter, "ERROR");
        throw new NoSuchAlgorithmException();
      }
      if ((!arrayOfString1[3].equals("HMACMD5")) && (!arrayOfString1[3].equals("HMACSHA1")) && (!arrayOfString1[3].equals("HMACSHA256")))
      {
        a(localPrintWriter, "ERROR");
        throw new NoSuchAlgorithmException();
      }
      a(localPrintWriter, "OK");
      
      
      Object localObject1;
      Object localObject2;
      X509Certificate localX509Certificate;
      try
      {
        str1 = a(localBufferedReader);
        System.out.println("hola men " + str1);
        String[] arrayOfString2 = str1.split(":");
        if (!arrayOfString2[0].equals("CERTCLNT"))
        {
          a(localPrintWriter, "Error en el formato. Cerrando conexion");
          throw new FontFormatException(str1);
        }
        localObject1 = "";
        localObject1 = localObject1 + arrayOfString2[1] + "\n";
        for (str1 = a(localBufferedReader); !str1.equals("-----END CERTIFICATE-----"); str1 = a(localBufferedReader)) {
          localObject1 = localObject1 + str1 + "\n";
        }
        localObject1 = localObject1 + str1;
        // Cogio el certificado
        
        localObject2 = new StringReader((String)localObject1);
        Object localObject3 = new PemReader((Reader)localObject2);
        Object localObject4 = ((PemReader)localObject3).readPemObject();
        Object localObject5 = new X509CertificateHolder(((PemObject)localObject4).getContent());
        localX509Certificate = new JcaX509CertificateConverter().getCertificate((X509CertificateHolder)localObject5);
        ((PemReader)localObject3).close();
      }
      catch (Exception localException2)
      {
        a(localPrintWriter, "ERROR");
        a(localPrintWriter, localException2.getMessage());
        localException2.printStackTrace();
        throw new FontFormatException("Error en el certificado recibido, no se puede decodificar");
      }
      KeyPair localKeyPair = Seguridad.a();
      try
      {
        Security.addProvider(new BouncyCastleProvider());
        localObject2 = KeyPairGenerator.getInstance("RSA", "BC");
        ((KeyPairGenerator)localObject2).initialize(1024);
        localKeyPair = ((KeyPairGenerator)localObject2).generateKeyPair();
        localObject1 = Seguridad.a(localKeyPair);
        Object localObject3 = new StringWriter();
        Object localObject4 = new JcaPEMWriter((Writer)localObject3);
        ((JcaPEMWriter)localObject4).writeObject(localObject1);
        ((JcaPEMWriter)localObject4).flush();
        ((JcaPEMWriter)localObject4).close();
        Object localObject5 = ((StringWriter)localObject3).toString();
        a(localPrintWriter, "CERTSRV:" + (String)localObject5);
      }
      catch (Exception localException3)
      {
        localException3.printStackTrace();
      }
      str1 = a(localBufferedReader);
      str1 = a(localBufferedReader);
      byte[] arrayOfByte1 = HexConverter.destransformarHEX(str1);
      Object localObject3 = Seguridad.d(arrayOfByte1, localKeyPair.getPrivate(), arrayOfString1[2]);
      Object localObject4 = HexConverter.transformarHEX((byte[])localObject3);
      a(localPrintWriter, (String)localObject4);
      str1 = a(localBufferedReader);
      if (!str1.equalsIgnoreCase("OK"))
      {
        a(localPrintWriter, "ERROR");
        throw new FontFormatException("Error, no se paso el reto 1.");
      }
      Object localObject5 = Seguridad.a(arrayOfString1[1]);
      byte[] arrayOfByte2 = Seguridad.c(((SecretKey)localObject5).getEncoded(), localX509Certificate.getPublicKey(), arrayOfString1[2]);
      String str2 = HexConverter.transformarHEX(arrayOfByte2);
      a(localPrintWriter, str2);
      str1 = a(localBufferedReader);
      byte[] arrayOfByte3 = HexConverter.destransformarHEX(str1);
      byte[] arrayOfByte4 = Seguridad.b(arrayOfByte3, (Key)localObject5, arrayOfString1[1]);
      String str3 = new String(arrayOfByte4);
      String[] arrayOfString3 = str3.split(",");
      try
      {
        String str4 = arrayOfString3[0];
        Object localObject6 = arrayOfString3[1];
        if ((str4 == "") || (localObject6 == "")) {
          throw new Exception("El usuario y la clave no pueden ser vacios.");
        }
      }
      catch (Exception localException4)
      {
        Object localObject6 = "ERROR";
        byte[] arrayOfByte5 = ((String)localObject6).getBytes();
        Object localObject7 = Seguridad.a(arrayOfByte5, (Key)localObject5, arrayOfString1[1]);
        Object localObject8 = HexConverter.transformarHEX((byte[])localObject7);
        a(localPrintWriter, (String)localObject8);
        throw new FontFormatException("Error: no se introdujo el usuario y la clave de manera adecuada");
      }
      String str5 = "OK";
      Object localObject6 = str5.getBytes();
      byte[] arrayOfByte5 = Seguridad.a((byte[])localObject6, (Key)localObject5, arrayOfString1[1]);
      Object localObject7 = HexConverter.transformarHEX(arrayOfByte5);
      a(localPrintWriter, (String)localObject7);
      str1 = a(localBufferedReader);
      String[] localObject8 = str1.split(":");
      byte[] arrayOfByte6 = HexConverter.destransformarHEX(localObject8[0]);
      byte[] arrayOfByte7 = HexConverter.destransformarHEX(localObject8[1]);
      byte[] arrayOfByte8 = Seguridad.b(arrayOfByte6, (Key)localObject5, arrayOfString1[1]);
      byte[] arrayOfByte9 = Seguridad.b(arrayOfByte7, (Key)localObject5, arrayOfString1[1]);
      boolean bool = Seguridad.a(arrayOfByte8, (Key)localObject5, arrayOfString1[3], arrayOfByte9);
      if (bool)
      {
        a(localPrintWriter, (String)localObject7);
      }
      else
      {
        String str6 = "ERROR";
        byte[] arrayOfByte10 = str6.getBytes();
        byte[] arrayOfByte11 = Seguridad.a(arrayOfByte10, (Key)localObject5, arrayOfString1[1]);
        String str7 = HexConverter.transformarHEX(arrayOfByte11);
        a(localPrintWriter, str7);
        throw new FontFormatException("Error, no se cumple integridad en la consulta.");
      }
    }
    catch (NullPointerException localNullPointerException)
    {
      a(localNullPointerException);
    }
    catch (IOException localIOException)
    {
      a(localIOException);
    }
    catch (FontFormatException localFontFormatException)
    {
      a(localFontFormatException);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      a(localNoSuchAlgorithmException);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      a(localInvalidKeyException);
    }
    catch (IllegalStateException localIllegalStateException)
    {
      a(localIllegalStateException);
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
      localNoSuchPaddingException.printStackTrace();
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      localIllegalBlockSizeException.printStackTrace();
    }
    catch (BadPaddingException localBadPaddingException)
    {
      localBadPaddingException.printStackTrace();
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
    }
    finally
    {
      try
      {
        paramSocket.close();
      }
      catch (Exception localException15) {}
    }
    try
    {
      paramSocket.close();
    }
    catch (Exception localException16) {}
  }
}
