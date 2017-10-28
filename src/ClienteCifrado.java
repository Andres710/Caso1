import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.openssl.PEMReader;

import Helpers.CifradoAsimetrico;
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
	
	// Llaves
	private PublicKey publicKey;
	private PublicKey serverPublicKey;
	private PrivateKey privateKey;
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
						
						// Mandar nuestro certificado
						X509Certificate cert = generarCertificadoDigital(publicKey, privateKey);
						escritor.println("CERTCLNT:"+convertToBase64PEMString(cert));	
						
						// Recibir certificado del servidor y saltarse primera linea que contiene CERTSRV:-----BEGIN CERTIFICATE-----
						lector.readLine();
						
						String pem = "-----BEGIN CERTIFICATE-----" +System.lineSeparator();
						while((fromServer = lector.readLine()) != null && !fromServer.equalsIgnoreCase("-----END CERTIFICATE-----")) {
							pem += fromServer + System.lineSeparator();
						}
						pem += "-----END CERTIFICATE-----" +System.lineSeparator();
						
						X509Certificate certificadoServidor = convertirStringACertificador(pem);
						serverPublicKey = certificadoServidor.getPublicKey();
					} else if(fromUser.equalsIgnoreCase(MANEJAR_RETOS)) {
						int numReto = 20000000;
						String reto1 = Integer.toString(numReto);
						byte[] cifradoReto1 = CifradoAsimetrico.cifrar(reto1, serverPublicKey);
						String reto1Enviar = HexConverter.transformarHEX(cifradoReto1);
						escritor.println(reto1Enviar);
						
						// Linea que sobra se ignora
						lector.readLine();
						
						
						// Se maneja RETO 2
						
						String x = leerDelServidor(lector);
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
			generarLlaves();
		} catch(Exception e) {
			System.err.println("Exception: " +e.getMessage());
			System.exit(1);
		}
	}
	
	// Helpers
	
	public void generarLlaves() throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, SignatureException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		keygen.initialize(1024);
		parLlaves = keygen.generateKeyPair();
		publicKey = parLlaves.getPublic();
		privateKey = parLlaves.getPrivate();
	}
	
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
