package Gload;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {
	
	private LoadGenerator generator;

	public Generator(int numberOfTasks, int gapBetweenTasks) {
		Task work = createTask();
		generator = new LoadGenerator("Client - Server Load Test", numberOfTasks, work, gapBetweenTasks);
		generator.generate();
	}
	
	public Task createTask() {
		return new ClientServerTask();
	}
	
	public static void main(String args[]) {
		Generator gen = new Generator(10, 1000);
	}
}
