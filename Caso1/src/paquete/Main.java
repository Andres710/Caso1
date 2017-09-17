package paquete;
import java.io.*;
import java.util.*;

public class Main {

	public final static String BUFFER_SIZE = "BUFFER_SIZE";
	public final static String CLIENTS = "CLIENTS_ID_AND_NUMBER_OF_QUERIES";
	public final static String SERVERS = "NUMBER_OF_SERVERS";
	public final static String FILE = "./data/datos.txt";


	public static void main(String[] args) throws Exception {

		// Carga el archivo
		Scanner sc = new Scanner(new FileReader(FILE));
		ArrayList<Cliente> clientes = new ArrayList<Cliente>();
		ArrayList<Servidor> servidores = new ArrayList<Servidor>();

		if(sc.nextLine().equals(BUFFER_SIZE)) {
			Buffer buffer = new Buffer(sc.nextInt());

			while(sc.hasNextLine()) {
				String line = sc.nextLine();

				if(line.equals(BUFFER_SIZE)) {
					int size = sc.nextInt();
					buffer = new Buffer(size);
				} else if(line.equals(SERVERS)) {
					int servers = sc.nextInt();
					for(int i = 0; i < servers; i++)
						servidores.add(new Servidor(buffer));
				} else if(!line.equals(CLIENTS) && line.contains(";")) {
					String[] datos = line.trim().split(";");
					int id_client = Integer.parseInt(datos[0]);
					int queries_client = Integer.parseInt(datos[1]);
					clientes.add(new Cliente(buffer, id_client, queries_client));
				}
			}
			
			buffer.setNumeroClientesAtender(clientes.size());
		}
		
		

		// Empieza los clientes y servidores que se leyeron
		for(Cliente c: clientes) {
			c.start();
		}

		for(Servidor s : servidores) {
			s.start();
		}

		sc.close();	
		
	}
}

