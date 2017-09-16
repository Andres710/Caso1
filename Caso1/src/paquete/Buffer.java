package paquete;

import java.util.ArrayList;

public class Buffer {

	//Lista de mensajes que llegan 
	private ArrayList<Mensaje> buff;

	//Capacidad del buffer
	private int capacidad;

	private Mensaje mensajeTrabajado;


	public Buffer(int n)
	{
		buff = new ArrayList<Mensaje>();
		capacidad = n;
		mensajeTrabajado = null;
		//clientesAtendidos = 0;
	}


	public synchronized Mensaje enviarMensaje(Mensaje pMensaje)
	{
		while(buff.size() == capacidad){

			Thread.yield();
		}

		buff.add(pMensaje);
		int  posicionMensaje = buff.indexOf(pMensaje);
		System.out.println("Se añadio el mensaje al buffer");
		notify();
		
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return buff.remove(0);

	}

	public synchronized void responderMensaje(){

		while(buff.size() == 0)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		Mensaje mensaje = buff.get(0);

		int respuesta = mensaje.getMensajeEnviado() + 1;

		mensaje.setRespuesta(respuesta);

		notify();


		//buff.get(0).notify();



	}


}
