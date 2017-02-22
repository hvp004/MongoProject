package controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;











import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;
import org.bson.types.ObjectId;

import vo.Actor;
import vo.Comment;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import controller.PathBuilder;
import static com.mongodb.client.model.Filters.regex;

/**
 * Servlet implementation class Search
 */

public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search()
    {
        super();
        
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession session=request.getSession();
		System.out.println("Servlet Called!~Search");
		String flag = request.getParameter("flag");
		MongoClient mongoClient = null;
		MongoDatabase database;
		MongoCollection<Document> collection;
		if(flag.equals("search"))
		{
		try
		{
		mongoClient = new MongoClient( "localhost" , 27017 );
		System.out.println("Connected to the server.");
		database = mongoClient.getDatabase("imdb");
		System.out.println("Connected to the database.");
		collection = database.getCollection("actors");
		System.out.println("Collection obtained.");
		final List<ObjectId> list_objId = new ArrayList<ObjectId>();
		final List<Actor> list_actor = new ArrayList<Actor>();	
		final LinkedHashSet<ObjectId> lhs_objId = new LinkedHashSet<ObjectId>();
		String search = request.getParameter("search");
		search = search.trim().replaceAll(" +", " ");
		String search_pattern = "\\b" + search + ".*";
		FindIterable<Document> iterable = collection.find(regex("actor", search_pattern, "i"));
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) 
		    {
		    	lhs_objId.add((ObjectId)document.get("_id"));
		    	
		    }
		});
		//System.out.println("ObjectId list size: "+list_objId.size());
		iterable = collection.find(regex("biography", search_pattern, "i"));
	
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) 
		    {
		    	lhs_objId.add((ObjectId)document.get("_id"));
		    }
		});
		
		//lhs_objId.addAll(list_objId);
		//list_objId.clear();
		list_objId.addAll(lhs_objId);
		for(int i=0;i<lhs_objId.size();i++)
		{
			//iterable = collection.find(new Document("_id",new Document("$in",list_objId) ) ).sort(new Document("",1));
			iterable = collection.find(new Document("_id",list_objId.get(i) ) );
			iterable.forEach(new Block<Document>()
			{
			    @Override
			    public void apply(final Document document) 
			    {
			    	Actor actor = new Actor();
			    	actor.setId((ObjectId) document.get("_id"));
			    	actor.setActor(document.getString("actor"));
			    	actor.setImage_url(document.getString("image_url"));
			    	actor.setAge((int) document.get("age"));
			    	actor.setBiography(document.getString("biography"));
			    	@SuppressWarnings("unchecked")
					List<Document> list = ((List<Document>) document.get("Comments"));
			    	List<Comment> list_comment = new ArrayList<Comment>();
			    	if(list!=null)
			    	{
			    	for(int i = 0; i<list.size(); i++)
			    	{
			    		Comment comment = new Comment();
			    		comment.setId((ObjectId)list.get(i).get("comment_id"));
			    		comment.setComment_content((String)list.get(i).get("comment"));
			    		list_comment.add(comment);
			    	}
			    	//System.out.println("Total Comments: "+list_comment.size());
			    	}
			    	actor.setList_comment(list_comment);
			    	list_actor.add(actor);
			    }
			});
		}	
		//mongoClient.close();
		if(request.getParameter("filter")!=null)
		{
			String filter = request.getParameter("filter");
			System.out.println("Sort Filter: "+filter);
			if(filter.equals("alphabatically"))
			{
				System.out.println("Alpha caught!");
				if(request.getParameter("reverse")!=null)
				{
					Collections.sort(list_actor,Collections.reverseOrder());
				}
				else
				{
					Collections.sort(list_actor);
				}
			}
			else if(filter.equals("comments"))
			{
				System.out.println("comments caught!");
				if(request.getParameter("reverse")!=null)
				{
					Collections.sort(list_actor,Actor.COMPARE_BY_COMMENTS);
				}
				else
				{
					Collections.sort(list_actor,Actor.COMPARE_BY_COMMENTS.reversed());
				}
			}
			else if(filter.equals("age"))
			{
				System.out.println("age caught!");
				if(request.getParameter("reverse")!=null)
				{
					Collections.sort(list_actor,Actor.COMPARE_BY_AGE);
				}
				else
				{
					Collections.sort(list_actor,Actor.COMPARE_BY_AGE.reversed());
				}
			}
		}
		String path = "flag=search";
		path = PathBuilder.buildPath(request, path);
		session.setAttribute("list_actor", list_actor);
		session.setAttribute("page_load", true);
		response.sendRedirect("index.jsp?"+path);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			System.out.println("Closing the connection..");
			mongoClient.close();
		}
		}//end if
		else if(flag.equals("byActor"))
		{
			try
			{
			String id_string = request.getParameter("id");
			ObjectId id = new ObjectId(id_string);
			mongoClient = new MongoClient( "localhost" , 27017 );
			System.out.println("Connected to the server.");
			database = mongoClient.getDatabase("imdb");
			System.out.println("Connected to the Database.");
			collection = database.getCollection("actors");
			System.out.println("Connected to the collection.");
			final List<Comment> list_comment = new ArrayList<Comment>();
 			final Actor actor  = new Actor();
			FindIterable<Document> iterable = collection.find(new Document("_id", id));
			iterable.forEach(new Block<Document>() {
			    @Override
			    public void apply(final Document document) 
			    {
			    	actor.setId((ObjectId) document.get("_id"));
			    	actor.setActor(document.getString("actor"));
			    	actor.setImage_url(document.getString("image_url"));
			    	actor.setBiography(document.getString("biography"));
			    	actor.setAge((int) document.get("age"));
			    	List<Document> list = (List<Document>) document.get("Comments");
			    	if(list!=null)
			    	{
			    	for(int i = 0; i<list.size(); i++)
			    	{
			    		Comment comment = new Comment();
			    		comment.setId((ObjectId)list.get(i).get("comment_id"));
			    		comment.setComment_content((String)list.get(i).get("comment"));
			    		list_comment.add(comment);
			    	}
			    	actor.setList_comment(list_comment);
			    	System.out.println(list_comment.size());
			    	}
			        System.out.println("Results: "+document);
			    }
			});
			mongoClient.close();
			session.setAttribute("actor", actor);
			session.setAttribute("page_load", true);
			response.sendRedirect("actor.jsp?flag=byActor&id="+id_string);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			finally
			{
				System.out.println("Closing the connection..");
				mongoClient.close();
			}
		}//end else if
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		System.out.println("Servlet Called!-post");
		String flag = request.getParameter("flag");
		MongoClient mongoClient =null;
		MongoDatabase database;
		MongoCollection<Document> collection;
		if(flag.equals("insert_comment"))
		{
			try
			{
			String id_string = request.getParameter("id");
			System.out.println("insert comment"+id_string);
			ObjectId id = new ObjectId(id_string);
			String content = request.getParameter("comment");
			mongoClient = new MongoClient( "localhost" , 27017 );
			System.out.println("Connected to server.");
			database = mongoClient.getDatabase("imdb");
			System.out.println("Connected to database");
			collection = database.getCollection("actors");
			System.out.println("Collection obtained.");
			Document document2 = new Document("comment_id",new ObjectId()).append("comment", content);
			collection.updateOne(new Document("_id",id), new Document("$push",new Document("Comments",document2)));
			//mongoClient.close();
			response.sendRedirect("actor.jsp?flag=byActor&id="+id_string);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			finally
			{
				mongoClient.close();
			}
		}
	}
}
