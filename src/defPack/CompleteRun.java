package defPack;

import javax.swing.JFrame;


public class CompleteRun {
	
  private static class ServerThread implements Runnable{
	  public void run(){
		  Server server = new Server();
	    	server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        server.startRunning();
	  }
  }
  
  private static class ClientThread implements Runnable{
	  public void run(){
		  Client client;  
	        client = new Client("127.0.0.1", "Client");
	        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        client.startRunning();
	  }
  }
	
  public static void main(String[] args) throws InterruptedException{
    	Thread t1 = new Thread(new ServerThread());
    	t1.start();
    	Thread t2 = new Thread(new ClientThread());
    	t2.start();

 }
}
