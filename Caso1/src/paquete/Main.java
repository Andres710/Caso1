package paquete;

public class Main {

	public static void main(String[] args) {
		
		Buffer buffer = new Buffer(4);
		
		Cliente c1 = new Cliente(buffer, 1);
		Cliente c2 = new Cliente(buffer, 2);
//		Cliente c3 = new Cliente(buffer, 3);
//		Cliente c4 = new Cliente(buffer, 4);
//		Cliente c5 = new Cliente(buffer, 5);
//		Cliente c6 = new Cliente(buffer, 6);
//		
		Servidor s1 = new Servidor(buffer);
		//Servidor s2 = new Servidor(buffer);
		
		System.out.println("Hola");
		c1.start();
		c2.start();
//		c3.start();
//		c4.start();
//		c5.start();
//		c6.start();
		s1.start();	
		

	}

}
