package paquete;

public class Cliente extends Thread{
	
	private static Buffer buffer;
	
	private int id;
	
	public Cliente(Buffer pBuffer, int pId){
		
		buffer = pBuffer;
		id = pId;
		
	}
	
	
	public void run(){
		
		enviarMensaje();
		
		
		
	}
	
	
	
	public void enviarMensaje(){
		
		//Genera un numero aleatorio entre 1 y 100 para el mensaje
		int numeroAleatorio = (int) (Math.random()*100+1);
		System.out.println("El cliente " + id +" mando el mensaje 1: " + numeroAleatorio);
		
		Mensaje mensaje = new Mensaje(numeroAleatorio, -1);
		
		Mensaje nuevoMensaje = buffer.enviarMensaje(mensaje);
		
		System.out.println("La respuesta recibida por el cliente " + id + " al mensaje 1 es: " + nuevoMensaje.getRespuesta());
		
	}

}
