package controller;

import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
public class PathBuilder
{
	public static String buildPath(HttpServletRequest request, String path)
	{
		if(request.getParameter("search") != null)
		{
			try {
				path = path + "&search="+ URLDecoder.decode(request.getParameter("search"),"UTF-8").trim().replaceAll(" +", " ");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("PATH from search: "+path);
		}
		if(request.getParameter("filter") != null)
		{
			path = path + "&filter="+request.getParameter("filter");
			System.out.println("PATH from filter: "+path);
		}
		if(request.getParameter("reverse") != null)
		{
			path = path + "&reverse="+request.getParameter("reverse");
			System.out.println("PATH from reverse: "+path);
		}
		if(request.getParameter("limit") != null)
		{
			try {
				path = path + "&limit="+java.net.URLEncoder.encode(request.getParameter("limit"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("PATH from limit: "+path);
		}
		return path;
	}
}
