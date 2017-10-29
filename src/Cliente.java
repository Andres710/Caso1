import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import Helpers.Seguridad;

public class Cliente {

	public X509Certificate generarCertificado(KeyPair localKeyPair) throws Exception {
		X509Certificate localObject1 = Seguridad.a(localKeyPair);
		return localObject1;
	}

	public X509Certificate leerCertificadoDeString(String certificado) throws Exception {
		StringReader localObject2 = new StringReader(certificado);
		PemReader localObject3 = new PemReader(localObject2);
		PemObject localObject4 = localObject3.readPemObject();
		X509CertificateHolder localObject5 = new X509CertificateHolder(localObject4.getContent());
		X509Certificate localX509Certificate = new JcaX509CertificateConverter().getCertificate((X509CertificateHolder)localObject5);
		localObject3.close();
		return localX509Certificate;
	}
	
	public String certificateToString(X509Certificate certificate) throws Exception {
		StringWriter localObject3 = new StringWriter();
		JcaPEMWriter localObject4 = new JcaPEMWriter(localObject3);
		localObject4.writeObject(certificate);
		localObject4.flush();
		localObject4.close();
		String localObject5 = localObject3.toString();
		return localObject5;
	}

}
