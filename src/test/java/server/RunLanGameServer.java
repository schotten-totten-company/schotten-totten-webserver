package server;

import java.io.IOException;

public class RunLanGameServer {

	
	public static void main(String[] args) throws IOException, InterruptedException {
		final LanGameServer lGameServer = new LanGameServer(8080);
		lGameServer.start();
		System.out.println("Server running");
		lGameServer.wait(1000*3600);
		System.out.println("Server stopped");
	}

}
