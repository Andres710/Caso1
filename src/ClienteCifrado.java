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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
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
import Helpers.Seguridad;
import Helpers.HexConverter;

public class ClienteCifrado extends Cliente {
	
	// Constantes
	public final static int PORT = 3000;
	public final static String HOST = "localhost";
	public final static String ENVIAR_CERTIFICADO = "POSI";
	public final static String MANEJAR_RETOS = "POSI2";
	
	// Lectores
	boolean ejecutar = true;
	Socket socket = null;
	PrintWriter escritor = null;
	BufferedReader lector = null;
	
	// Certificados
	X509Certificate cert = null;
	PublicKey serverPublicKey = null;

	private KeyPair parLlaves;
	
	public ClienteCifrado() {
		realizarConexion();
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String fromServer;
		String fromUser;
		
		try {	
			
			while(ejecutar) {
				System.out.println("Escriba el mensaje para enviar:");
				fromUser = stdIn.readLine();
				
				if(fromUser != null && !fromUser.equals(-1)) {
					System.out.println("Cliente: " +fromUser);
					
					// Acaba la comunicacion con el servidor
					if(fromUser.equalsIgnoreCase("TERMINAR")) {
						ejecutar = false;
						continue;
					}
					
					// Manejar comunicacion con el servidor
					if(fromUser.equalsIgnoreCase(ENVIAR_CERTIFICADO)) {

						KeyPairGenerator localKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
						localKeyPairGenerator.initialize(1024, new SecureRandom());
						parLlaves = localKeyPairGenerator.generateKeyPair();

						Security.addProvider(new BouncyCastleProvider());
						KeyPairGenerator localObject2 = KeyPairGenerator.getInstance("RSA", "BC");
						localObject2.initialize(1024);
						parLlaves = localObject2.generateKeyPair();
						
						// Mi certificado
						cert = generarCertificado(parLlaves);
				        escritor.println("CERTCLNT:" +certificateToString(cert));
				        
				        // Saltarse linea
				        lector.readLine();
				        
				        // Leer certificado
						String pem = "-----BEGIN CERTIFICATE-----" +System.lineSeparator();
						while((fromServer = lector.readLine()) != null && !fromServer.equalsIgnoreCase("-----END CERTIFICATE-----")) {
							pem += fromServer + System.lineSeparator();
						}
						pem += "-----END CERTIFICATE-----" +System.lineSeparator();
				        
						X509Certificate certificadoServidor = leerCertificadoDeString(pem);
						serverPublicKey = certificadoServidor.getPublicKey();
					} else if(fromUser.equalsIgnoreCase(MANEJAR_RETOS)) {
						int numReto = 20000000;
						String reto1 = Integer.toString(numReto);
						byte[] byteReto = Seguridad.a(reto1.getBytes(), serverPublicKey, "RSA");
						String reto1Enviar = HexConverter.transformarHEX(byteReto);
						
						escritor.println(reto1Enviar);
						
						lector.readLine();
						String mensajeServidor = lector.readLine();
						
						byte[] byteServidor = HexConverter.destransformarHEX(mensajeServidor);
						String retoRespuesta = new String(byteServidor);

						if(retoRespuesta.equals(reto1)) {
							escritor.println("OK");
						} else {
							escritor.println("ERROR");
						}
					}
					else {
						escritor.println(fromUser);
						leerDelServidor(lector);
					}					
				}				
			}
			
			escritor.close();
			lector.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	// Methods
	
	public void realizarConexion() {
		try {
			socket = new Socket(HOST, PORT);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch(Exception e) {
			System.err.println("Exception: " +e.getMessage());
			System.exit(1);
		}
	}
	
	// Helpers
	
	public String leerDelServidor(BufferedReader lector) {
		String fromServer = "";
		try {
			if((fromServer = lector.readLine()) != null) {
				System.out.println("Servidor: " +fromServer);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return fromServer;
	}
}
