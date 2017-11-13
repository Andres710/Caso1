package Gload;

import java.io.FileWriter;
import java.io.PrintWriter;

import Clients.ClienteCifrado;
import Clients.ClienteSinCifrado;
import uniandes.gload.core.Task;

public class ClientServerTask extends Task {

	@Override
	public void execute() {		
		try {
			//ClienteCifrado clienteCifrado = new ClienteCifrado();
			ClienteSinCifrado clienteSinCifrado = new ClienteSinCifrado();
			success();
			writeTxT(clienteSinCifrado.getTiempoConsulta()+";"+clienteSinCifrado.getTiempoAutenticacionServ() +";" +clienteSinCifrado.getTiempoAutenticacionCliente());
		} catch(Exception e) {
			fail();
			e.printStackTrace();
		}
	}
	
	public void success() {
		System.out.println(Task.OK_MESSAGE);
	}
	
	public void fail() {	
		System.out.println(Task.MENSAJE_FAIL);
	}
	
	public void writeTxT(String data) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("./datos/datos.txt", true));
			pw.println(data);
			pw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
