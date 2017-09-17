package paquete;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		Buffer buffer = new Buffer(3);
		
		Cliente c1 = new Cliente(buffer, 1, 1);
		Cliente c2 = new Cliente(buffer, 2, 2);
		Cliente c3 = new Cliente(buffer, 3, 3);
		Cliente c4 = new Cliente(buffer, 4, 4);
		Cliente c5 = new Cliente(buffer, 5, 5);
		Cliente c6 = new Cliente(buffer, 6, 6);
		
		Servidor s1 = new Servidor(buffer);
		Servidor s2 = new Servidor(buffer);
		Servidor s3 = new Servidor(buffer);
		
		c1.start();

		c2.start();

		c3.start();

		c4.start();

		c5.start();

//		c6.start();

		
		s1.start();
		s2.start();
		s3.start();
		

	}

}
