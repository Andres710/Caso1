package Gload;

import Clients.ClienteCifrado;
import uniandes.gload.core.Task;

public class ClientServerTask extends Task {

	@Override
	public void execute() {		
		ClienteCifrado clienteCifrado = new ClienteCifrado();
	}
	
	public void success() {
		System.out.println(Task.MENSAJE_FAIL);
	}
	
	public void fail() {	
		System.out.println(Task.OK_MESSAGE);
	}
}
