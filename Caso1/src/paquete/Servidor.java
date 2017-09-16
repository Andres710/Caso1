package paquete;

public class Servidor extends Thread{

	private static Buffer buffer;

	public Servidor(Buffer pBuffer){

		buffer = pBuffer;
	}

	public void responderMensaje(){

		buffer.responderMensaje();
	}

	public void run(){

		while(true){
			responderMensaje();
		}

	}

}
