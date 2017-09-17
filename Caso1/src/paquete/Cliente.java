package paquete;

public class Cliente extends Thread{

	private static Buffer buffer;

	private int id;

	private int numeroConsultas;

	public Cliente(Buffer pBuffer, int pId, int numeroConsultas){

		buffer = pBuffer;
		id = pId;
		this.numeroConsultas = numeroConsultas;

	}


	public void run(){

		for(int i = 0; i < numeroConsultas; i++){
			enviarMensaje(i+1);
		}



	}



	public void enviarMensaje(int numMensaje){

		//Genera un numero aleatorio entre 1 y 100 para el mensaje
		int numeroAleatorio = (int) (Math.random()*100+1);
		System.out.println("El cliente " + id +" mando el mensaje " + numMensaje +": " + numeroAleatorio);

		Mensaje mensaje = new Mensaje(numeroAleatorio, -1);


		Mensaje nuevoMensaje = buffer.enviarMensaje(mensaje);

		System.out.println("La respuesta recibida por el cliente " + id + " al mensaje " + numMensaje + " es: " + nuevoMensaje.getRespuesta());

	}

}
