package paquete;

import java.util.ArrayList;

public class Buffer {

	//Lista de mensajes que llegan 
	private ArrayList<Mensaje> buff;

	//Capacidad del buffer
	private int capacidad;

	private Object vacio;

	//private Object lleno;


	public Buffer(int n)
	{
		buff = new ArrayList<Mensaje>();
		capacidad = n;
		vacio = new Object();
		//lleno = new Object();
	}


	public Mensaje enviarMensaje(Mensaje pMensaje)
	{
		
		while(buff.size() == capacidad){
			//System.out.println(pMensaje.getMensajeEnviado());
			Thread.yield();
		}

		synchronized(this){ buff.add(pMensaje);}
		//System.out.println("Se añadio el mensaje al buffer: " + pMensaje.getMensajeEnviado());
		synchronized(vacio){vacio.notify();}

		try {
			synchronized(pMensaje){ pMensaje.wait();}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Mensajes en cola: " + buff.size());
		return pMensaje;

		//return pMensaje;
	}




	public void responderMensaje(){

		//System.out.println("Voy a sacar del buffer");
		synchronized(vacio){
			while(buff.size() == 0)
			{
				try {
					vacio.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		Mensaje mensaje = null;
		synchronized(this){
			if(buff.size() > 0){mensaje = buff.remove(0);}}

		if(mensaje != null){
			synchronized(mensaje){

				int respuesta = mensaje.getMensajeEnviado() + 1;
				mensaje.setRespuesta(respuesta);
				mensaje.notify();


			}
		}

		//synchronized(lleno){ lleno.notify();}


	}


}
