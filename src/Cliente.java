import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class Cliente {

	public X509Certificate generarCertificadoDigital(PublicKey publicKey, PrivateKey privateKey) throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException
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
	
	public X509Certificate convertirStringACertificador(String certificado) {
		X509Certificate cert = null;
		try {
			Security.addProvider(new BouncyCastleProvider());
	        StringReader reader = new StringReader(certificado);
	        PEMReader pr = new PEMReader(reader);
	        cert = (X509Certificate)pr.readObject();
	        pr.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return cert;
	}

}
