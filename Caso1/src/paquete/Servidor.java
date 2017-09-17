package paquete;

public class Servidor extends Thread{

	private static Buffer buffer;
	
	private static boolean terminamos;

	public Servidor(Buffer pBuffer){

		buffer = pBuffer;
		terminamos = false;
	}

	public void responderMensaje(){

		terminamos = buffer.responderMensaje();
		//buffer.responderMensaje();
	}

	public void run(){

		try {
			sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		while(!terminamos){
			responderMensaje();
		}

	}

}
