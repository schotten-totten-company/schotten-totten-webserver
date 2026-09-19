package server;

import java.io.IOException;

public class RunLanGameServer {

	
	public static void main(String[] args) throws IOException {
		final LanGameServer lGameServer = new LanGameServer(8080);
		lGameServer.start();
		System.out.println("Server running");
		System.out.println("Server stopped");
	}

}
