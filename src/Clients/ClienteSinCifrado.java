package Clients;

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

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import Helpers.HexConverter;

public class ClienteSinCifrado extends Cliente {

	// Constantes
	public final static int PORT = 3000;
	public final static String HOST = "192.168.0.28";
	public final static String OK = "OK";
	public final static String ERROR = "ERROR";
	public final static String HOLA = "HOLA";
	public final static String LLAVE = "LLAVE";
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
	
	// Tiempos
	long tiempoConsulta;
	long tiempoAutenticacionServ;
	long tiempoAutenticacionoCliente;

	// Constructor
	public ClienteSinCifrado() throws Exception {
		realizarConexion();

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String fromServer = "";

		comenzarComunicacionEnviarAlgoritmos(stdIn);
		manejarCertificados(fromServer);
		manejarEnvioMensajes(stdIn);
		escritor.close();
		lector.close();
	}

	// Methods
	public void realizarConexion() throws Exception {
		socket = new Socket(HOST, PORT);
		escritor = new PrintWriter(socket.getOutputStream(), true);
		lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void comenzarComunicacionEnviarAlgoritmos(BufferedReader cliente) throws Exception {
		escritor.println(HOLA);
		//Medición tiempo de respuesta a una consulta (3)
		long tInicioConsulta = System.currentTimeMillis();
		String respuesta = lector.readLine();
		
		long tFinConsulta = System.currentTimeMillis();
		long tiempoConsulta = tFinConsulta - tInicioConsulta;
		
		System.out.println("El tiempo de respuesta a una consulta es: " + tiempoConsulta);

		if(!respuesta.equals(OK)) {
			throw new Exception("No se pudo conectar con el servidor");
		}

		// Escoger simetrico
		simetrico = simetricos[0];

		// Escoger asimetrico
		asimetrico = asimetricos[0];

		// Escoger hmac
		hmac = hmacs[0];

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
		String reto1Enviar = HexConverter.toHEX(reto1.getBytes());

		//Medición tiempo de respuesta para autenticación del servidor (1)
		long tInicioAutenticacionServ = System.currentTimeMillis();
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
		//Medición tiempo consulta (3)
		long tInicioConsulta = System.currentTimeMillis();
		
		long tFinAutenticacionServ = System.currentTimeMillis();
		tiempoAutenticacionServ = tFinAutenticacionServ - tInicioAutenticacionServ;
		
		System.out.println("El tiempo de autenticación del servidor fue: " + tiempoAutenticacionServ);

		String servidorLlave = lector.readLine();
		long tFinConsulta = System.currentTimeMillis();
		tiempoConsulta = tFinConsulta - tInicioConsulta;
		
		System.out.println("El tiempo de una consulta fue: " + tiempoConsulta);
		
		if(!servidorLlave.equals(LLAVE)) {
			throw new Exception("No envio LLAVE el servidor");
		} else {
			System.out.println("Llego LLAVE desde el servidor");
		}
		
		//Medición tiempo de respuesta para autenticación del cliente (2)
		long tInicioAutenticacionCliente = System.currentTimeMillis();
	
		// Usuario y clave
		manejarUsuarioyClave(cliente);
		long tFinAutenticacionCliente = System.currentTimeMillis();
		tiempoAutenticacionoCliente = tFinAutenticacionCliente - tInicioAutenticacionCliente;
		
		System.out.println("El tiempo de autenticación del cliente fue: " + tiempoAutenticacionoCliente);

		// Mandar cedula
		manejarCedula(cliente);

		// Respuesta al mandar cedula
		String resultadoCedula = lector.readLine();

		if(!resultadoCedula.equals(OK)) {
			throw new Exception("No se acepto cedula en el servidor");
		} else {
			System.out.println("Se acepto cedula");
			return true;
		}
	}

	public void manejarUsuarioyClave(BufferedReader cliente) throws Exception {
		String usuario = "usuario";
		String clave = "clave";

		String datos = usuario +SEPARADOR_USUARIO +clave;
		escritor.println(datos);

		String resultado = lector.readLine();

		if(!resultado.equals(OK)) {
			throw new Exception("No se pudo enviar usuario y clave");
		} else {
			System.out.println("Se acepto usuario y clave");
		}
	}

	public void manejarCedula(BufferedReader cliente) throws Exception {
		String cc = "0123456789";

		escritor.println(cc +":" +cc);
		System.out.println("Se envio cedula");
	}

	// Inner Class Helpers
	public String leerDelServidor(BufferedReader lector) throws Exception {
		String fromServer = "";
		
		if((fromServer = lector.readLine()) != null)
			System.out.println("Servidor: " +fromServer);
				
		return fromServer;
	}
	
	
	// Getters
	
	public long getTiempoConsulta() {
		return tiempoConsulta;
	}
	
	public long getTiempoAutenticacionServ() {
		return tiempoAutenticacionServ;
	}
	
	public long getTiempoAutenticacionCliente() {
		return tiempoAutenticacionoCliente;
	}
}
