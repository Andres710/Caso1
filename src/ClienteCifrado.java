import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteCifrado {
	boolean ejecutar = true;
	Socket socket = null;
	PrintWriter escritor = null;
	BufferedReader lector = null;
	
	public ClienteCifrado() {
		try {
			socket = new Socket("localhost", 3000);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch(Exception e) {
			System.err.println("Exception: " +e.getMessage());
			System.exit(1);
		}
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String fromServer;
		String fromUser;
		
		try {
			while(ejecutar) {
				System.out.println("Escriba el mensaje para enviar:");
				fromUser = stdIn.readLine();
				
				if(fromUser != null && !fromUser.equals(-1)) {
					System.out.println("Cliente: " +fromUser);
					if(fromUser.equalsIgnoreCase("OK"))
						ejecutar = false;
					
					escritor.println(fromUser);
				}
				
				if((fromServer = lector.readLine()) != null) {
					System.out.println("Servidor: " +fromServer);
				}
			}
			
			escritor.close();
			lector.close();
		} catch(Exception e) {
			System.err.println("Exception: " +e.getMessage());
			System.exit(1);
		}
		
	}
}
