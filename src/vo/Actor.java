package vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import vo.Comment;





import org.bson.types.ObjectId;

public class Actor  implements Serializable,Comparable<Actor>
{
	private static final long serialVersionUID = -5767755242954891017L;
	private ObjectId id;
	private String actor;
	private int age;
	private String image_url;
	private String biography;
	private List<Comment> list_comment;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	
	public String getBiography() {
		return biography;
	}
	public void setBiography(String biography) {
		this.biography = biography;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public List<Comment> getList_comment() {
		return list_comment;
	}
	public void setList_comment(List<Comment> list_comment) {
		this.list_comment = list_comment;
	}
	@Override
	public int compareTo(Actor other)
	{
		return actor.compareTo(other.actor);
	}
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	public static Comparator<Actor> COMPARE_BY_COMMENTS = new Comparator<Actor>()
	{
		
		@Override
		public int compare(Actor one, Actor other)
		{
			return Integer.valueOf(one.getList_comment().size()).compareTo(Integer.valueOf(other.getList_comment().size()));
		}
	};
	public static Comparator<Actor> COMPARE_BY_AGE = new Comparator<Actor>()
	{
	
		@Override
		public int compare(Actor one, Actor other)
		{
			return Integer.valueOf(one.getAge()).compareTo(Integer.valueOf(other.getAge()));
		}
	};
}
