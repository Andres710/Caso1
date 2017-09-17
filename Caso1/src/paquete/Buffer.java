package paquete;

import java.util.ArrayList;

public class Buffer {

	//Lista de mensajes que llegan 
	private ArrayList<Mensaje> buff;

	//Capacidad del buffer
	private int capacidad;

	//Objeto auxiliar sincronizacion
	private Object vacio;

	//Número de clientes que se atenderán
	private int clientes;


	public Buffer(int n)
	{
		buff = new ArrayList<Mensaje>();
		capacidad = n;
		vacio = new Object();
		clientes = 0;

	}


	public Mensaje enviarMensaje(Mensaje pMensaje)
	{

		while(buff.size() == capacidad){

			Thread.yield();
		}

		synchronized(this){ buff.add(pMensaje);}

		synchronized(vacio){vacio.notify();}

		try {
			synchronized(pMensaje){ pMensaje.wait();}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return pMensaje;
	}




	public boolean responderMensaje(){

		synchronized(vacio){
			while(buff.size() == 0 && clientes > 0)
			{
				try {
					vacio.wait(1);
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


		boolean terminamos = false;

		synchronized(this){
			if(clientes < 1){ terminamos = true;}
		}

		if(terminamos){
			synchronized(vacio){vacio.notifyAll();}
			return true;
		}
		else{
			synchronized(vacio){vacio.notifyAll();}
			return false;
		}

	}


	public void setNumeroClientesAtender(int pNumClientes){
		this.clientes = pNumClientes;
	}

	public int avisarFin()
	{
		synchronized(vacio){vacio.notifyAll();}

		synchronized(this){
			return clientes--;
		}
	}


}
