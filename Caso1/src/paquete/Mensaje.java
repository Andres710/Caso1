package paquete;

public class Mensaje {
	
	private int mensajeEnviado;
	
	private int respuesta;
	
	
	public Mensaje(int pMensajeEnviado, int pRespuesta){
		
		mensajeEnviado = pMensajeEnviado;
		respuesta = pRespuesta;
	}


	public int getMensajeEnviado() {
		return mensajeEnviado;
	}


	public void setMensajeEnviado(int mensajeEnviado) {
		this.mensajeEnviado = mensajeEnviado;
	}


	public int getRespuesta() {
		return respuesta;
	}


	public void setRespuesta(int respuesta) {
		this.respuesta = respuesta;
	}
	
	
	

}
