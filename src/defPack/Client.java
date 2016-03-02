package defPack;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import javax.swing.*;
import javax.imageio.*;
import java.text.SimpleDateFormat;
public class Client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message="";
	private String serverIP;
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
	private String userName;
	private BufferedImage myPicture;
	private boolean endConv = false ;
	public Client(String host, String userName){
		super("Client messenger");
		
		
		
		serverIP= host;
		this.userName = userName;
		buttonsListener listener = new buttonsListener();
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
		smile.addMouseListener(listener);
		kiss.addMouseListener(listener);
		tongue.addMouseListener(listener);
		sad.addMouseListener(listener);
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
		addName.addMouseListener(listener);
		closeConv.addMouseListener(listener);
		sendMessage.addMouseListener(listener);
		saveConversation.addMouseListener(listener);
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
	}// end of initializing GUI
	
	// the listener for buttons
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
	
	//connect to server
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofe){
			showMessage("\n " + userName + " terminated the connection");
		}catch(IOException ioExc){
			ioExc.printStackTrace();
		}finally{
			closeCrap();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Attempting connection....\n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//setup streams
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nThe streams are setup\n________________________________\n");
		showMessage("Enter your name");
		//userName = userText.getText();
	}
	//while chatting with server
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String)input.readObject();
				showMessage("\n " + message );
			}catch(ClassNotFoundException cnte){
				showMessage("\n I dont know that object type");
			}
		}while(!message.contains("END") );
	}
	
	//close the streams and sockets
	private void closeCrap(){
		showMessage("\n Closing  down... \n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ecx){
			ecx.printStackTrace();
		}
	}
	
	//send Messages
	private void sendMessage(String message){
		try{
			output.writeObject(userName + " - " + message);
			output.flush();
			if(endConv == false)
			showMessage("\n " + userName + " - " + message);
		}catch(IOException exc){
		   chatWindow.append("\n error \n");
		}
	}
	//change / update chatWindow
	private void showMessage(final String m){
		SwingUtilities.invokeLater(
		   new Runnable(){
			   public void run(){
				   chatWindow.append(m);
			   }
		   }
				
		);
	}
	
	//gives user permission to type
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
    Random idNum = new Random();
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
                   + " values ('"+strDate+"',"+ idNum.nextInt(999) + ",'"+ textToSave +"','"+ userName +"')";

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
