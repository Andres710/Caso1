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

		try {
			sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while(true){
			responderMensaje();
		}

	}

}
