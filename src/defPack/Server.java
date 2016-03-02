package defPack;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import javax.swing.*;
import javax.imageio.*;
import java.text.SimpleDateFormat;



public class Server extends JFrame{
   private JTextField userText;
   private JTextArea chatWindow;
   private ObjectOutputStream output;
   private ObjectInputStream input;
   private ServerSocket server;
   private Socket connection;
   private JPanel buttonPanel;
   private JButton saveConversation;
   private JButton sendMessage;
   private JButton addName;
   private JButton closeConv;
   private JLabel northLabel;
   private JLabel imgDisplay;
   private JButton smile;
   private JButton sad;
   private JButton kiss;
   private JButton tongue;
   private java.util.Date date;
   private JPanel imagePanel;
   private String strDate;
   private String userName = "SERVER";
   private buttonsListener listener;
   //constructor
   public Server(){
	   super("Instant Messenger");
	   
	   
	    listener = new buttonsListener();
		//first, establishing the image tracker
		imagePanel = new JPanel();
		imagePanel.setLayout(new BoxLayout(imagePanel,BoxLayout.Y_AXIS));
		Icon smileI = new ImageIcon(getClass().getResource("smileEmo.png"));
		Icon kissI = new ImageIcon(getClass().getResource("kissEmo.png"));
		Icon tongueI = new ImageIcon(getClass().getResource("tongueEmo.png"));
		Icon sadI = new ImageIcon(getClass().getResource("sadEmo.png"));
		smile = new JButton(smileI);
		kiss = new JButton(kissI);
		tongue = new JButton(tongueI);
		sad = new JButton(sadI);
		//smile.addMouseListener(listener);
		//kiss.addMouseListener(listener);
		//tongue.addMouseListener(listener);
		//sad.addMouseListener(listener);
		imagePanel.add(smile);
		imagePanel.add(kiss);
		imagePanel.add(tongue);
		imagePanel.add(sad);
		//imagePanel.add(Box.createRigidArea(new Dimension(0,300)));
		imagePanel.setSize(300,600);
		
		// creating north label displaying date
		java.util.Date lm = new java.util.Date();
		strDate = new SimpleDateFormat("yyyy-MM-dd").format(lm);
		northLabel = new JLabel("Instant Messenger    " + strDate );
		northLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		//creating the buttons
		
		Icon save = new ImageIcon(getClass().getResource("save.png"));
		Icon close = new ImageIcon(getClass().getResource("close.png"));
		Icon send = new ImageIcon(getClass().getResource("send.png"));
		Icon person = new ImageIcon(getClass().getResource("person.png"));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
		buttonPanel.add(Box.createGlue());
		addName = new JButton(person);
		sendMessage = new JButton(send);
		saveConversation = new JButton(save);
		closeConv = new JButton(close);
	//	addName.addMouseListener(listener);
		//closeConv.addMouseListener(listener);
		//sendMessage.addMouseListener(listener);
		//saveConversation.addMouseListener(listener);
		closeConv.setToolTipText("End your conversation");
		sendMessage.setToolTipText("Send your message");
		saveConversation.setToolTipText("Save your conversation");
		addName.setToolTipText("Click to enter your name");
		buttonPanel.setSize(150,600);
		addName.setPreferredSize(new Dimension(150,200));
		saveConversation.setPreferredSize(new Dimension(150,200));
		sendMessage.setPreferredSize(new Dimension(150,200));
		closeConv.setPreferredSize(new Dimension(150,200));
		buttonPanel.add(addName);
		buttonPanel.add(sendMessage);
		buttonPanel.add(saveConversation);
		buttonPanel.add(closeConv);
	   
	   
	   
	   
		//creating the field for writing messages
				userText = new JTextField();
				userText.setText("Type your messages in here");
				userText.setEditable(false);
				userText.setFont(new Font("Verdana",Font.BOLD,20));
				userText.addActionListener(
				   new ActionListener(){
					   public void actionPerformed(ActionEvent event){
						   sendMessage(event.getActionCommand());
						   userText.setText("");
					   }
				   }
				   
				);
		        
				//creating field for displaying the chat
				chatWindow = new JTextArea();
				chatWindow.setEditable(false);
				chatWindow.setFont(new Font("Verdana",Font.BOLD,15));
				chatWindow.setBackground(new Color(214,230,236));
				
				//adding all the elements to the frame ( flow layout )
				add(northLabel, BorderLayout.NORTH);
				add(imagePanel,BorderLayout.EAST);
				add(buttonPanel,BorderLayout.WEST);
				add(new JScrollPane(chatWindow), BorderLayout.CENTER);
				add(userText, BorderLayout.SOUTH);
				
				setSize(800,625);
				setResizable(false);
				setVisible(true);
   }
   
   //set up and run the server
   public void startRunning(){
	   try{
		   server = new ServerSocket(6789, 100);
		   while(true){
			   try{
				   //conncet and have conversation
				   waitForConnection();
				   setupStreams();
				   whileChatting();
			   }catch(EOFException eofExc){
				   showMessage("\n Server ended the conncetion! ");
			   }finally{
				   closeConversation();
			   }
		   }
	   }catch(IOException ioException)
	   {
		   ioException.printStackTrace();
	   }
   
   }
   //wait for connection, then display connection info
   private void waitForConnection() throws IOException{
	   showMessage("Waiting for someone to connect... \n");
	   connection = server.accept();
	   showMessage("Now Connected to " + connection.getInetAddress().getHostName()); 
	   smile.addMouseListener(listener);
	   kiss.addMouseListener(listener);
	   tongue.addMouseListener(listener);
	   sad.addMouseListener(listener);
	   addName.addMouseListener(listener);
	   closeConv.addMouseListener(listener);
	   sendMessage.addMouseListener(listener);
	   saveConversation.addMouseListener(listener);
   }
   // get stream to send and receive data
   private void setupStreams() throws IOException{
	   output = new ObjectOutputStream(connection.getOutputStream());
	   output.flush();
	   input = new ObjectInputStream(connection.getInputStream());
	   showMessage("\n Streams are now setup! \n");
   }
   //during the conversation
   private void whileChatting() throws IOException{
	  // String message = "You are now connected! ";
	  // sendMessage(message);
	   String message = "";
	   ableToType(true);
	   do{
		   //have a conversation
		   try{
			   message = (String) input.readObject();
			   showMessage("\n" + message);
		   }catch(ClassNotFoundException cnfe){
			   showMessage("\n Big problem");
		   }
	   } while(!message.contains("END")); 
		   
   }
   
   //close streams and sockets after you are done chatting
   private void closeConversation(){
	   showMessage("\n Closing connections... \n");
	   ableToType(false);
	   try{
		   output.close();
		   input.close();
		   connection.close();
	   }catch(IOException ioException){
		   ioException.printStackTrace();
	   }
   }
   
   private class buttonsListener extends MouseAdapter{
		public void mouseClicked(MouseEvent me){
		    
			if(me.getSource()==addName) addPerson();
			else if(me.getSource()==closeConv) sendMessage("END");
			else if(me.getSource()==sendMessage)
			{
				String toSend = userText.getText();
				userText.setText("");
				sendMessage(toSend);
			}
			else if(me.getSource()==saveConversation) saveIntoDatabase();
			else if(me.getSource()==smile) sendMessage(":)");
			else if(me.getSource()==kiss) sendMessage(":*");
			else if(me.getSource()==sad) sendMessage(":(");
			else if(me.getSource()==tongue) sendMessage(":P");
		}
	}
   
   //send a message to client
   private void sendMessage(String message){
	   try{
		   output.writeObject(userName +" - " +message);
		   output.flush();
		   showMessage("\n" + userName +" - " +message);
	   }catch(IOException ioException){
		   chatWindow.append("\n ERROR, cant send message");
	   }
   }
   
   //updates ChatWindow
   private void showMessage(final String text){
	   SwingUtilities.invokeLater(
		new Runnable(){
			public void run(){
				chatWindow.append(text);
			}
		}
	   );
   }
   //let the user type stuff
   private void ableToType(final boolean tof){
	   SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						userText.setEditable(tof);
					}
				}
			   );
   }
   
   
 //function to add user's name
 	private void addPerson(){
 		userName = userText.getText();
 		userText.setText("");
 		
 	}
 	
 	//function for saving the chat to database
 	private void saveIntoDatabase(){
 	String url = "jdbc:mysql://localhost/savingsystem";
     String user = "root";
     String password = "password";
     String textToSave = chatWindow.getText();
     Random idNum = new Random(); // to save in the column of database
     Connection myConn = null;
     Statement myStmt = null;

     try {
         // 1. Get a connection to database
     	Class.forName("com.mysql.jdbc.Driver");
         myConn = DriverManager.getConnection(url, user, password);

         // 2. Create a statement
         myStmt = myConn.createStatement();

         // 3. Execute SQL query
         String sql = "INSERT INTO savingsystem.historia "
                    + "(Data, Numer, Tekst, Uzytkownik)"
                    + " values ('"+strDate+"',"+ idNum.nextInt(999) + ",'"+ textToSave +"','"+userName+"')";

         myStmt.executeUpdate(sql);

         System.out.println("Insert complete.");
     }
     catch (Exception exc) {
         exc.printStackTrace();
     }
     finally {
         try{
         	if (myStmt != null) 
         	{
             myStmt.close();
         }

         if (myConn != null) {
             myConn.close();
         }
     }catch(SQLException sql)
     {}    }
 	
 	}

}