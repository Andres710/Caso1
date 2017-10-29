
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Random;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import Helpers.Ciph;
import Helpers.HexConverter;

public class ClienteCifrado extends Cliente {

	// Constantes
	public final static int PORT = 3000;
	public final static String HOST = "localhost";
	public final static String OK = "OK";
	public final static String ERROR = "ERROR";
	public final static String HOLA = "HOLA";
	public final static String ALGORITMOS = "ALGORITMOS";
	public final static String SEPARADOR = ":";
	public final static String CERTIFICADO_CLIENTE = "CERTCLNT";
	public final static String SEPARADOR_USUARIO = ",";

	// Lectores
	boolean ejecutar = true;
	Socket socket = null;
	PrintWriter escritor = null;
	BufferedReader lector = null;

	// Certificados
	X509Certificate cert = null;
	PublicKey serverPublicKey = null;
	SecretKey llaveSimetrica = null;
	private KeyPair parLlaves;

	// Algoritmos
	private final String[] simetricos = { "DES", "AES", "Blowfish", "RC4"};
	private final String[] asimetricos = { "RSA" };
	private final String[] hmacs = { "HMACMD5", "HMACSHA1", "HMACSHA256" };
	public String simetrico = "";
	public String asimetrico = "";
	public String hmac = "";

	// Constructor
	public ClienteCifrado() {
		realizarConexion();

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String fromServer = "";

		try {	
			comenzarComunicacionEnviarAlgoritmos(stdIn);
			manejarCertificados(fromServer);
			manejarEnvioMensajes(stdIn);
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

	public void comenzarComunicacionEnviarAlgoritmos(BufferedReader cliente) throws Exception {
		escritor.println(HOLA);
		String respuesta = lector.readLine();

		if(!respuesta.equals(OK)) {
			throw new Exception("No se pudo conectar con el servidor");
		}

		// Escoger simetrico
		System.out.println("Escoja el algoritmo simetrico que desea: ");

		for(int i = 0; i < simetricos.length; i++) {
			System.out.println((i+1) +" " +simetricos[i]);
		}

		String ans = cliente.readLine();
		int opcion = Integer.parseInt(ans);
		if(opcion > simetricos.length || opcion  < 1) {
			throw new Exception("Opcion no existe");
		} else {
			simetrico = simetricos[opcion-1];
		}

		// Escoger asimetrico
		System.out.println("Escoja el algoritmo asimetrico que desea: ");

		for(int i = 0; i < asimetricos.length; i++) {
			System.out.println((i+1) +" " +asimetricos[i]);
		}

		ans = cliente.readLine();
		opcion = Integer.parseInt(ans);
		if(opcion > asimetricos.length || opcion  < 1) {
			throw new Exception("Opcion no existe");
		} else {
			asimetrico = asimetricos[opcion-1];
		}

		// Escoger hmac
		System.out.println("Escoja el algoritmo hmac que desea: ");

		for(int i = 0; i < hmacs.length; i++) {
			System.out.println((i+1) +" " +hmacs[i]);
		}

		ans = cliente.readLine();
		opcion = Integer.parseInt(ans);
		if(opcion > hmacs.length || opcion  < 1) {
			throw new Exception("Opcion no existe");
		} else {
			hmac = hmacs[opcion-1];
		}		

		System.out.println("Escogio: " +simetrico +SEPARADOR +asimetrico +SEPARADOR +hmac);
		escritor.println(ALGORITMOS +SEPARADOR +simetrico +SEPARADOR +asimetrico +SEPARADOR +hmac);
	}

	public void manejarCertificados(String fromServer) throws Exception {
		KeyPairGenerator localKeyPairGenerator = KeyPairGenerator.getInstance(asimetrico);
		localKeyPairGenerator.initialize(1024, new SecureRandom());
		parLlaves = localKeyPairGenerator.generateKeyPair();

		Security.addProvider(new BouncyCastleProvider());
		KeyPairGenerator localObject2 = KeyPairGenerator.getInstance(asimetrico, PROVEEDOR);
		localObject2.initialize(1024);
		parLlaves = localObject2.generateKeyPair();

		// Mi certificado
		cert = generarCertificado(parLlaves);
		escritor.println(CERTIFICADO_CLIENTE +SEPARADOR +certificateToString(cert));
		System.out.println("Se envio certificado al servidor");

		// Saltarse linea
		lector.readLine();

		// Leer certificado
		String pem = COMIENZO_CERTIFICADO +System.lineSeparator();
		while((fromServer = lector.readLine()) != null && !fromServer.equalsIgnoreCase(FINAL_CERTIFICADO)) {
			pem += fromServer + System.lineSeparator();
		}
		pem += FINAL_CERTIFICADO +System.lineSeparator();

		X509Certificate certificadoServidor = leerCertificadoDeString(pem);
		serverPublicKey = certificadoServidor.getPublicKey();

		System.out.println("Se acepto certificado");
	}

	public boolean manejarEnvioMensajes(BufferedReader cliente) throws Exception {
		Random rand = new Random();
		// Se usa & Integer.MAX_VALUE para obtener solo numeros positivos
		int numReto = rand.nextInt() & Integer.MAX_VALUE; 
		String reto1 = Integer.toString(numReto);
		byte[] byteReto = Ciph.cifrar(reto1.getBytes(), serverPublicKey, asimetrico);
		String reto1Enviar = HexConverter.toHEX(byteReto);

		escritor.println(reto1Enviar);
		System.out.println("Se envio reto 1 al servidor: " +reto1);

		lector.readLine();
		String mensajeServidor = lector.readLine();

		byte[] byteServidor = HexConverter.fromHEX(mensajeServidor);
		String retoRespuesta = new String(byteServidor);

		if(retoRespuesta.equals(reto1)) {
			escritor.println(OK);
			System.out.println("Se acepto reto 1: " +retoRespuesta);
		} else {
			throw new Exception("No se pudo enviar reto al usuario");
		}

		String servidorLlaveSimetrica = lector.readLine();
		byte[] servidor = HexConverter.fromHEX(servidorLlaveSimetrica);
		byte[] sim = Ciph.descifrar(servidor, parLlaves.getPrivate() , asimetrico);

		SecretKeySpec sk = new SecretKeySpec(sim, simetrico);
		llaveSimetrica = sk;

		// Usuario y clave
		manejarUsuarioyClave(cliente);

		// Mandar cedula
		manejarCedula(cliente);

		// Respuesta al mandar cedula
		String resultadoCedula = lector.readLine();
		byte[] resultadoHexCedula = HexConverter.fromHEX(resultadoCedula);
		byte[] resultadoFCedula = Ciph.descifrar(resultadoHexCedula, llaveSimetrica, simetrico);
		String resultadoStringCedula = new String(resultadoFCedula);

		if(!resultadoStringCedula.equals(OK)) {
			throw new Exception("No se acepto cedula en el servidor");
		} else {
			System.out.println("Se acepto cedula");
			return true;
		}
	}

	public void manejarUsuarioyClave(BufferedReader cliente) throws Exception {
		System.out.println("Ingrese su usuario: ");
		String usuario = cliente.readLine();
		System.out.println("Ingrese su clave: ");
		String clave = cliente.readLine();

		String datos = usuario +SEPARADOR_USUARIO +clave;
		byte[] datosCif = Ciph.cifrar(datos.getBytes(), llaveSimetrica, simetrico);
		String datosHex = HexConverter.toHEX(datosCif);
		escritor.println(datosHex);

		String resultado = lector.readLine();
		byte[] resultadoHex = HexConverter.fromHEX(resultado);
		byte[] resultadoF = Ciph.descifrar(resultadoHex, llaveSimetrica, simetrico);
		String resultadoString = new String(resultadoF);

		if(!resultadoString.equals(OK)) {
			throw new Exception("No se pudo enviar usuario y clave");
		} else {
			System.out.println("Se acepto usuario y clave");
		}
	}

	public void manejarCedula(BufferedReader cliente) throws Exception {
		System.out.println("Ingrese su cedula: ");
		String cc = cliente.readLine();
		byte[] byCedulaCifrada = Ciph.cifrar(cc.getBytes(), llaveSimetrica, simetrico);

		// Digest
		byte[] hashCedula = Ciph.macHash(cc.getBytes(), llaveSimetrica, hmac);
		byte[] chashcedula = Ciph.cifrar(hashCedula, llaveSimetrica, simetrico);

		String ccedula = HexConverter.toHEX(byCedulaCifrada);
		String hcedula = HexConverter.toHEX(chashcedula);
		escritor.println(ccedula +":" +hcedula);
		System.out.println("Se envio cedula");
	}

	// Inner Class Helpers
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
