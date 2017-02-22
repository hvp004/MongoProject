package vo;
import static com.mongodb.client.model.Filters.regex;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;

import com.mongodb.Block;
//Mongo Imports	
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
//Log message control imports
//Java imports

public class AccessMongo2 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7396859630988700751L;
	JTextField input;
	JTextArea output;
	
	MongoDatabase sampleDB = null;
   MongoClient client = null;
	MongoCollection<Document> collection = null;
	MongoCursor<Document> cursor = null;
   WindowListener exitListener = null;
	
	public AccessMongo2()
    {
		setSize(600, 200);
		setLocation(400, 500);
		setTitle("Access MongoDB");
		
		Container cont = getContentPane();
		cont.setLayout(new BorderLayout() );
		
		JButton search = new JButton("Search");
		JButton connect = new JButton("Connect");
		JButton clear = new JButton("Clear");
		
		input = new JTextField(20);
		
		output = new JTextArea(10, 30);
		JScrollPane spOutput = new JScrollPane(output);
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout());
		northPanel.add(connect);
		northPanel.add(input);
		northPanel.add(search);
		northPanel.add(clear);
		
		cont.add(northPanel, BorderLayout.NORTH);
				
		cont.add(spOutput, BorderLayout.CENTER);
		
		connect.addActionListener(new ConnectMongo());
		search.addActionListener(new GetMongo());
		clear.addActionListener(new ClearMongo());
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      
      exitListener = new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent e) {
            int confirm = JOptionPane.showOptionDialog(
               null, "Are You Sure to Close Application?", 
               "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
               JOptionPane.QUESTION_MESSAGE, null, null, null);
            
            if (confirm == 0) {
               // Close the Mongo Client
               client.close();
               
               System.exit(0);
            }
         }
      };
      
      addWindowListener(exitListener);
		setVisible(true);
	} //AccessMongo
	
	public static void main (String [] args) 
   {
     new AccessMongo2();
	}//main
	
	class ConnectMongo implements ActionListener
    {
		public void actionPerformed (ActionEvent event)
       {
		//in this section open the connection to MongoDB. 
		//You should enter the code to connect to the database here
		//Remember to connect to MongoDB, connect to the database and connect to the 
		//    desired collection
		//EXAM STUFF
		try
		{
         client = new MongoClient("localhost", 27017);
        	output.append("Connection to server completed\n");
		}	
		catch (Exception e)
       {
         output.append("ERROR connecting to server\n");
			System.out.println("Error on connection");
			System.out.println(e.getMessage());
			System.out.println(e.toString());
			e.printStackTrace();
		}
		
		//access the database
      sampleDB = client.getDatabase("imdb");        
		output.append("Connection to database completed\n");
				
		//get the collection
		try
      {
         collection = sampleDB.getCollection("actors");        
			output.append("Collection obtained\n");
	   }
		catch (MongoException e)
      {
			System.out.println("Error on collection");
			System.out.println(e.getMessage());
			System.out.println(e.toString());
			e.printStackTrace();
		}
		}//actionPerformed
	}//class ConnectMongo
	
	class GetMongo implements ActionListener
    {
		public void actionPerformed (ActionEvent event)
      {
		// In this section you should retrieve the data from the collection
		// and use a cursor to list the data in the output JTextArea
      System.out.println("\n\nHERE\n\n");  
      //Normal Find regex

      String searchText = input.getText();
                  
         String regexPattern = "\\b" + searchText + ".*";
         
         final List<Document> list = new ArrayList<Document>();
         FindIterable<Document> iterable = collection.find(regex("actor", regexPattern, "i")).sort(new Document("age",-1));
         try
         {
		   iterable.forEach(new Block<Document>()
          {
		    @Override
		    public void apply(final Document document) 
		    {
		    	//lhs_objId.add((ObjectId)document.get("_id"));
		    	System.out.println(document);
            list.add(document);
		    }
		});
      output.append("Size: "+list.size()+"\n");
      for(int i=0;i<list.size();i++)
      {
    	  output.append(list.get(i).getString("actor")+"   "+list.get(i).get("age")+"\n");
    	  /*int age = (int) list.get(i).get("age") - 10;*/
    	  collection.updateOne(list.get(i), new Document("$inc",new Document("age",20) ) );
      }
      	Document add = new Document("actor","Tome hector");
     
      	collection.deleteMany(add);
      	//collection.insertOne(add);
      	
      }
         //cursor = collection.find(regex("content:encoded", regexPattern, "i")).iterator();
         /*try 
         {
            while (cursor.hasNext())
             {
               Document d = cursor.next();
               output.append(d.getString("content:encoded"));
               output.append(cursor.next().toJson());
               output.append("\n");
               cnt++;
            }
         }*/
         catch (MongoException e)
         {
				System.out.println("Error on next()");
				System.out.println(e.getMessage());
				System.out.println(e.toString());
				e.printStackTrace();
         }
               	
	     	}//actionPerformed
	}//class GetMongo
	
	class ClearMongo implements ActionListener
    {
		public void actionPerformed (ActionEvent event)
      {
		//in this section open the connection. Should be able to see if it is not null
		// to see if ti is already open
			output.setText("");
         client.close();
		
		}//actionPerformed
	
	
	}//class ClearMongo


} //class