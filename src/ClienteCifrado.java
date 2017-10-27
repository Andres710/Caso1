import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class ClienteCifrado {
	boolean ejecutar = true;
	Socket socket = null;
	PrintWriter escritor = null;
	BufferedReader lector = null;
	
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private KeyPair parLlaves;
	
	public ClienteCifrado() {
		try {
			socket = new Socket("localhost", 3000);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			generarLlaves();
		} catch(Exception e) {
			System.err.println("Exception: " +e.getMessage());
			System.exit(1);
		}
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String fromServer;
		String fromUser;
		
		try {	
			
			while(ejecutar) {
				System.out.println("Escriba el mensaje para enviar:");
				fromUser = stdIn.readLine();
				
				if(fromUser != null && !fromUser.equals(-1)) {
					System.out.println("Cliente: " +fromUser);
					if(fromUser.equalsIgnoreCase("OK"))
						ejecutar = false;
					
					if(fromUser.equalsIgnoreCase("POSI")) {
						X509Certificate cert = generarCertificadoDigital();
						
						escritor.println("CERTCLNT:"+convertToBase64PEMString(cert));	
						

						while((fromServer = lector.readLine()) != null && !fromServer.equalsIgnoreCase("-----END CERTIFICATE-----")) {
							System.out.println("Servidor: " +fromServer);
						}
						
						Integer entero = 241619;
						String hex = Integer.toHexString(entero);
						String hexConPadding = "";
						
						if((hex.length()%2) != 0 ){
							hex ="0"+hex;
						}
						
						System.out.println(hex);
						
						escritor.println(hex);
						
					} else {
						escritor.println(fromUser);
						
						if((fromServer = lector.readLine()) != null) {
							System.out.println("Servidor: " +fromServer);
						}
					}					
				}				
			}
			
			escritor.close();
			lector.close();
		} catch(Exception e) {
			System.err.println("Exception: " +e.getMessage());
			System.exit(1);
		}
		
	}
	
	public void generarLlaves() throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, SignatureException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		keygen.initialize(1024);
		parLlaves = keygen.generateKeyPair();
		publicKey = parLlaves.getPublic();
		privateKey = parLlaves.getPrivate();
	}
	
	private X509Certificate generarCertificadoDigital() throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException
	{
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		X500Principal nombre = new X500Principal("CN=Test V3 Certificate");
		BigInteger serialAleatorio = new BigInteger( 10, new Random() );
		
		//Configuración fecha actual
		Date fechaActual = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaActual);
		calendar.add(Calendar.YEAR, 1);
		
		//Configuración del generador del certificado
		certGen.setSerialNumber(serialAleatorio);
		certGen.setIssuerDN(nombre);
		certGen.setSubjectDN(nombre);
		certGen.setNotBefore(fechaActual);
		certGen.setNotAfter(calendar.getTime());
		certGen.setPublicKey(publicKey);
		certGen.setSignatureAlgorithm("SHA1withRSA");

		X509Certificate cert = certGen.generate(privateKey);

		return cert;
	}
	
	// Helpers
		
	public String convertToBase64PEMString(Certificate x509Cert) throws IOException {
	    StringWriter sw = new StringWriter();
	    try {
	    	PEMWriter pw = new PEMWriter(sw);
	        pw.writeObject(x509Cert);
	        pw.close();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return sw.toString();
	}

}
