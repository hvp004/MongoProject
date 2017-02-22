package controller;
import static com.mongodb.client.model.Filters.regex;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.AggregateIterable;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import vo.Actor;
import vo.Comment;

public class SearchServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
    public SearchServlet()
    {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session=request.getSession();
		System.out.println("Servlet Called!");
		String flag = request.getParameter("flag");
		if(flag.equals("search"))
		{
			System.out.println("Flag: "+flag);
			String search = request.getParameter("search");
			System.out.println("Search Term: "+search);
			search = search.trim().replaceAll(" +", " ");
			System.out.println("Search for regex: "+search);
			String search_pattern = "\\b" + search + ".*";
			System.out.println("Search regex: "+search_pattern);
			//
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			MongoDatabase database = mongoClient.getDatabase("imdb");
			MongoCollection<Document> collection = database.getCollection("actors");
			//
			String path="flag=search&search="+search;
			FindIterable<Document> iterable = null;
			
			final List<ObjectId> list_objId = new ArrayList<ObjectId>();
			final List<Actor> list_actor = new ArrayList<Actor>();
			//
			Document actor_query = new Document("actor",java.util.regex.Pattern.compile(search_pattern,Pattern.CASE_INSENSITIVE));
			Document biography_query = new Document("biography",java.util.regex.Pattern.compile(search_pattern,Pattern.CASE_INSENSITIVE));
			if(request.getParameter("filter")!=null)
			{
				String filter = request.getParameter("filter");
				System.out.println("Sort Filter: "+filter);
				path = path + "&filter="+URLEncoder.encode(request.getParameter("filter"),"UTF-8");
				String filter_term="";
				int filter_order = 1;
				if(filter.equals("alphabatically"))
				{
					System.out.println("Alpha caught!");
					filter_term = "actor";
				}
				else if(filter.equals("age"))
				{
					System.out.println("age caught!");
					filter_term = "age";
				}
				else if(filter.equals("comments"))
				{
					iterable = collection.find(new Document("$or", asList(actor_query,biography_query)));
				}
				if(request.getParameter("reverse")!=null)
				{
					path = path + "&reverse="+URLEncoder.encode(request.getParameter("reverse"),"UTF-8");
					filter_order = -1;
				}
				if(!filter.equals("comments"))
				{
					iterable = collection.find(new Document("$or", asList(actor_query,biography_query))).sort(actor_query).sort(new Document(filter_term,filter_order));
				}
			}
			else
			{
				System.out.println("No filter!");
				iterable = collection.find(new Document("$or", asList(actor_query,biography_query)));
			}
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
			    	System.out.println("Total Comments: "+list_comment.size());
			    	}
			    	actor.setList_comment(list_comment);
			    	list_actor.add(actor);
			    }
			});
			if(request.getParameter("filter")!=null && request.getParameter("filter").equals("comments"))
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
			mongoClient.close();
			System.out.println("Path: "+path);
			session.setAttribute("list_actor", list_actor);
			session.setAttribute("page_load", true);
			response.sendRedirect("index.jsp?"+path);
		}
		
		else if(flag.equals("byActor"))
		{
			String id_string = request.getParameter("id");
			ObjectId id = new ObjectId(id_string);
			//Mongo Connection
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			MongoDatabase database = mongoClient.getDatabase("imdb");
			MongoCollection<Document> collection = database.getCollection("actors");
			//End Mongo Connection
			//Search
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
		}//end else if

		
		else
		{
			System.out.println("Something went wrong.");
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session=request.getSession();
		System.out.println("Servlet Called!-post");
		String flag = request.getParameter("flag");
		if(flag.equals("insert_comment"))
		{
			String id_string = request.getParameter("id");
			System.out.println("insert comment"+id_string);
			ObjectId id = new ObjectId(id_string);
			String content = request.getParameter("comment");
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			MongoDatabase database = mongoClient.getDatabase("imdb");
			MongoCollection<Document> collection = database.getCollection("actors");
			Document document2 = new Document("comment_id",new ObjectId()).append("comment", content);
			collection.updateOne(new Document("_id",id), new Document("$push",new Document("Comments",document2)));
			mongoClient.close();
			response.sendRedirect("actor.jsp?flag=byActor&id="+id_string);
		}
	}

}
