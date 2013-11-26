package servlet;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Message.Builder;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import database.DBCPUtils;
import database.PostDao;

public class PostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String CONSUMER_POST = "consumer_post";
	public static final String CONSUMER_POST_FILE = "consumer_post_file";
	public static final String SELLER_RECEIVE_POST = "seller_receive_post";
	public static final String API_KEY = "AIzaSyCY9IpwghSoNI0KfSwuHtWOOdO9nurLCiA";
	String action;
	Consumer consumer;
	ConsumerPost consumerPost;
	Seller seller;

	String realFolder = "";
	String saveFolder = "image";
	String encType = "EUC-KR";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("EUC-KR");
		action = request.getParameter("action");
		if(action==null){
			//멀티파트 엔티티
			int postMaxSize = 10 * 1024 * 1024;
//			ServletContext context = getServletContext();
			realFolder = request.getRealPath(saveFolder);
			MultipartRequest mRequest = null;
			mRequest = new MultipartRequest(request, realFolder, postMaxSize,
					encType, new DefaultFileRenamePolicy());
			consumer = new Consumer();
			consumerPost = new ConsumerPost();
			seller = new Seller();

			action = mRequest.getParameter("action");// 넘어 오는 액션에 따라 처리
			// 구매자가 판매자에게 post 요청
			if (action.equals(CONSUMER_POST)) {
				// 날짜, 구매자 이름, 카테고리 번호, 글 내용, 사진
				savePost(mRequest, response);
				// 각 판매자에게 푸쉬
			}
		}else if(action.equals(Seller.SELLER_REQUEST_POST)){
			requestPost(request, response);
		}
		destroy();
	}

	public void requestPost(HttpServletRequest request, HttpServletResponse response){
		PostDao postDao = PostDao.getInstance();
		
		ConsumerPost consumerPost = postDao.getPost(Integer.parseInt(request.getParameter("post_id")));

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(
					makeXmlRequestPost(consumerPost));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void savePost(MultipartRequest mRequest, HttpServletResponse response) {

		ConsumerPost consumerPost = new ConsumerPost();
		PostDao postDao = PostDao.getInstance();
		try {
			consumerPost.setDate(mRequest.getParameter("date"));
			consumerPost.setConsumer_name(URLDecoder.decode(
					mRequest.getParameter("consumer_name"), encType));
			consumerPost.setTitle(URLDecoder.decode(
					mRequest.getParameter("title"), encType));
			consumerPost.setCategory(mRequest.getParameter("category"));
			consumerPost.setCity(URLDecoder.decode(
					mRequest.getParameter("city"), encType));
			consumerPost.setContents(URLDecoder.decode(
					mRequest.getParameter("contents"), encType));

			Enumeration files = mRequest.getFileNames();
			int i = 0;

			while (files.hasMoreElements()) {
				String name = (String) files.nextElement();
				
				String fileName = URLDecoder.decode(
						mRequest.getParameter("imagename" + String.valueOf(i)),
						encType);

				File file = mRequest.getFile(name);

				if (file.length() != Integer.parseInt(mRequest
						.getParameter("imagesize" + String.valueOf(i)))) {
					// 파일 사이즈가 다름.
					System.out.println("사진 파일 사이즈가 다름");
				}
				i++;
				consumerPost.getPost_images_name().add(fileName);
			}
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int post_id = postDao.savePost(consumerPost);
		String paramresult="false";
		if(post_id>0){
			try {
				paramresult = sendMessage(
						SELLER_RECEIVE_POST,
						post_id,
						getSellersByCityAndCategory(URLDecoder.decode(
								mRequest.getParameter("city"), encType),
								mRequest.getParameter("category")));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(
					makeXmlResponse(paramresult, post_id, action));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public void sendGcmToSeller(String city, String category){
	// //판매자 리스트를 구성하고 각 판매자에게 gcm을 보낸다.
	// ArrayList<String> sellers = new ArrayList<String>();
	// }
	public ArrayList<String> getSellersByCityAndCategory(String city,
			String category) {
		PostDao postDao = PostDao.getInstance();
		
		return postDao.sellers_gcm(city, category);
	}

	private String sendMessage(String action, int postId,
			ArrayList<String> sellers) {
		Sender sender = new Sender(API_KEY);
		String returnResult="false";
		if(sellers.size()==0){
			return "null";
		}
		// GCM onMessage 에서는 intent getString extra..
		Message.Builder builder = new Builder();
		builder.addData("action", action);
		builder.addData("post_id", String.valueOf(postId));
		Message message = builder.build();
		
		MulticastResult multiResult;
		try {
			multiResult = sender.send(message, sellers, 5);
			if (multiResult != null) {
				List<Result> resultList = multiResult.getResults();
				if(resultList.size()>0)	{
					returnResult="true";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnResult;
	}

	private String makeXmlResponse(boolean paramresult, String title) {
		String message = null;
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<items>");
		stringBuffer.append("<item>");
		stringBuffer.append("<title>" + title + "</title>");
		if (paramresult) {
			message = "true";
		} else {
			message = "false";
		}

		stringBuffer.append("\t<result>" + message + "</result>");
		// stringBuffer.append("\t<filemessage>" + filemessage +
		// "</filemessage>");

		stringBuffer.append("</item>");
		stringBuffer.append("</items>");
		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}

	private String makeXmlResponse(String paramresult, int post_id,
			String title) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<items>");
		stringBuffer.append("<item>");
		stringBuffer.append("<title>" + title + "</title>");
		stringBuffer.append("\t<result>" + paramresult + "</result>");
		stringBuffer.append("\t<post_id>" + post_id + "</post_id>");
		// stringBuffer.append("\t<filemessage>" + filemessage +
		// "</filemessage>");

		stringBuffer.append("</item>");
		stringBuffer.append("</items>");
		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}
	private String makeXmlRequestPost(ConsumerPost consumerPost){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<items>");
		stringBuffer.append("<item>");
		stringBuffer.append("<action>" + action + "</action>");
		stringBuffer.append("\t<post_id>" + consumerPost.getId() + "</post_id>");
		stringBuffer.append("\t<date>"+ consumerPost.getDate()+"</date>");
		stringBuffer.append("\t<consumer_name>"+consumerPost.getConsumer_name()+"</consumer_name>");
		stringBuffer.append("\t<title>"+consumerPost.getTitle()+"</title>");
		stringBuffer.append("\t<category>"+consumerPost.getCategory()+"</category>");
		stringBuffer.append("\t<city>"+consumerPost.getCity()+"</city>");
		stringBuffer.append("\t<contents>"+consumerPost.getContents()+"</contents>");
		for(int i=0; i<consumerPost.getPost_images_name().size(); i++){
			stringBuffer.append("\t<image>"+consumerPost.getPost_images_name().get(i)+"</image>");
		}
		stringBuffer.append("</item>");
		stringBuffer.append("</items>");
		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}
}
