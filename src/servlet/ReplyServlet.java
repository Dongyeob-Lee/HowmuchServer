package servlet;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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

import database.Dao;
import database.PostDao;
import database.ReplyDao;
import database.Utils;

/**
 * Servlet implementation class ReplyServlet
 */
@WebServlet("/ReplyServlet")
public class ReplyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String CONSUMER_POST = "consumer_post";
	public static final String CONSUMER_POST_FILE = "consumer_post_file";
	public static final String SELLER_RECEIVE_POST = "seller_receive_post";
	public static final String API_KEY = "AIzaSyCY9IpwghSoNI0KfSwuHtWOOdO9nurLCiA";
	String action;

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
		System.out.println("action:"+action);
		if (action == null) {
			// 멀티파트 엔티티
			int postMaxSize = 10 * 1024 * 1024;
			ServletContext context = getServletContext();
			realFolder = context.getRealPath(saveFolder);
			MultipartRequest mRequest = null;
			mRequest = new MultipartRequest(request, realFolder, postMaxSize,
					encType, new DefaultFileRenamePolicy());

			action = mRequest.getParameter("action");// 넘어 오는 액션에 따라 처리
			// 구매자가 판매자에게 post 요청
			if (action.equals(Seller.SELLER_SEND_REPLY)) {
				// 날짜, 구매자 이름, 카테고리 번호, 글 내용, 사진
				saveReply(mRequest, response, "seller_reply");
			} else if (action.equals(Consumer.CONSUMER_SEND_REPLY)) {
				saveReply(mRequest, response, "consumer_reply");
			}
		} else if (action.equals(Consumer.CONSUMER_REQUEST_REPLY)) {
			consumerRequestSellerReply(request, response);
		} else if (action.equals(Seller.SELLER_REQUEST_REPLY)) {
			sellerRequestConsumerReply(request, response);
		}
		destroy();
	}

	public void consumerRequestSellerReply(HttpServletRequest request,
			HttpServletResponse response) {
		String reply_id = request.getParameter("reply_id");
		ReplyDao replyDao = ReplyDao.getInstance();

		Reply reply = replyDao.getReply(
				Integer.parseInt(reply_id),
				"seller_reply");

		String seller_num = replyDao.getSeller_idFromReply(reply_id);
		Dao dao = Dao.getInstance();
		Seller seller = dao.getSellerFromNum(seller_num);
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlConsumerRequestReply(reply,seller));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sellerRequestConsumerReply(HttpServletRequest request,
			HttpServletResponse response) {
		ReplyDao replyDao = ReplyDao.getInstance();

		Reply reply = replyDao.getReply(
				Integer.parseInt(request.getParameter("reply_id")),
				"consumer_reply");

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlRequestReply(reply));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveReply(MultipartRequest mRequest,
			HttpServletResponse response, String tableName) {

		Reply reply = new Reply();
		ReplyDao replyDao = ReplyDao.getInstance();
		String seller_name=null;
		String seller_num = null;
		try {
			reply.setPflag(Integer.parseInt(mRequest.getParameter("pflag")));
			reply.setParent(Integer.parseInt(mRequest.getParameter("parent")));
			seller_name = URLDecoder.decode(
					mRequest.getParameter("seller_name"), encType);
			reply.setPost_id(Integer.parseInt(mRequest.getParameter("post_id")));
			reply.setDate(mRequest.getParameter("date"));
			reply.setSeller_name(seller_name);
			seller_num = mRequest.getParameter("seller_num");
			reply.setSeller_num(seller_num);
			reply.setSeller_phone(mRequest.getParameter("seller_phone"));
			reply.setConsumer_name(URLDecoder.decode(
					mRequest.getParameter("consumer_name"), encType));
			reply.setContents(URLDecoder.decode(
					mRequest.getParameter("contents"), encType));
			Enumeration files = mRequest.getFileNames();
			int i = 0;

			while (files.hasMoreElements()) {
				String name = (String) files.nextElement();

				String fileName = URLDecoder.decode(
						mRequest.getParameter("imagename" + String.valueOf(0)),
						encType);

				File file = mRequest.getFile(name);

				if (file.length() != Integer.parseInt(mRequest
						.getParameter("imagesize" + String.valueOf(i)))) {
					// 파일 사이즈가 다름.
					System.out.println("사진 파일 사이즈가 다름");
				}
				i++;
				reply.getReply_images_name().add(fileName);
			}
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int reply_id = replyDao.saveReply(reply,tableName);
		
		
		////send message
		boolean paramresult=false;
		String user_gcm  = null;
		if(tableName.equals("seller_reply")){
			try {
				user_gcm = getConsumerGCMByConsumername(URLDecoder.decode(
						mRequest.getParameter("consumer_name"), encType));
				if(user_gcm!=null){
					paramresult= sendMessage(
							Seller.SELLER_SEND_REPLY,
							reply_id,reply.getPost_id(),user_gcm);
					if(paramresult){
						Dao dao = Dao.getInstance();
						dao.sellerReducePlane(seller_num);
					}
				}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			try {
				
				if (reply_id > 0) {
					if(paramresult){
						response.getWriter().write(
								makeXmlResponse("true", reply_id, action));
					}else if(user_gcm==null){
						response.getWriter().write(
								makeXmlResponse("null", reply_id, action));
					}else{
						response.getWriter().write(
								makeXmlResponse("false", reply_id, action));
					}
				}else{
					response.getWriter().write(
							makeXmlResponse("false", reply_id, action));
				}
				response.getWriter().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				user_gcm = getSellerGCMBySellerNum(URLDecoder.decode(
						mRequest.getParameter("seller_num"), encType));
				if (user_gcm != null && !user_gcm.equals("logout")) {
					paramresult = sendMessage(Consumer.CONSUMER_SEND_REPLY,
							reply_id, reply.getPost_id(), user_gcm);
				}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			try {
				
				if (reply_id > 0) {
					if(paramresult){
						response.getWriter().write(
								makeXmlResponse("true", reply_id, action));
					}else if(user_gcm==null){
						response.getWriter().write(
								makeXmlResponse("null", reply_id, action));
					}else if(user_gcm.equals("logout")){
						response.getWriter().write(
								makeXmlResponse("logout", reply_id, action));
					}else{
						response.getWriter().write(
								makeXmlResponse("false", reply_id, action));
					}
				}else{
					response.getWriter().write(
							makeXmlResponse("false", reply_id, action));
				}
				response.getWriter().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// public void sendGcmToSeller(String city, String category){
	// //판매자 리스트를 구성하고 각 판매자에게 gcm을 보낸다.
	// ArrayList<String> sellers = new ArrayList<String>();
	// }
	public String getConsumerGCMByConsumername(String consumer_name) {
		ReplyDao replyDao = ReplyDao.getInstance();

		return replyDao.getConsumer_gcm(consumer_name);
	}

	public String getSellerGCMBySellerNum(String seller_num) {
		ReplyDao replyDao = ReplyDao.getInstance();

		return replyDao.getSeller_gcm(seller_num);
	}

	private boolean sendMessage(String action, int replyId,int post_id, String user) {
		Sender sender = new Sender(API_KEY);
		boolean returnResult=false;
		if(user.equals("logout")){
			return true;
		}
		ArrayList<String> userId = new ArrayList<String>();
		userId.add(user);
		// GCM onMessage 에서는 intent getString extra..
		Message.Builder builder = new Builder();
		builder.addData("action", action);
		builder.addData("post_id", String.valueOf(post_id));
		builder.addData("reply_id", String.valueOf(replyId));
		Message message = builder.build();

		MulticastResult multiResult;
		try {
			multiResult = sender.send(message, userId, 5);
			if (multiResult != null) {
				List<Result> resultList = multiResult.getResults();
				if(resultList.size()>0){
					returnResult=true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnResult;
	}

	private String makeXmlResponse(String paramresult, int reply_id,
			String title) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<items>");
		stringBuffer.append("<item>");
		stringBuffer.append("<title>" + title + "</title>");
		stringBuffer.append("\t<result>" + paramresult + "</result>");
		stringBuffer.append("\t<reply_id>" + reply_id + "</reply_id>");

		stringBuffer.append("</item>");
		stringBuffer.append("</items>");
		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}
	private String makeXmlRequestReply(Reply reply) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<items>");
		stringBuffer.append("<item>");
		stringBuffer.append("<action>" + action + "</action>");
		stringBuffer.append("\t<reply_id>" + reply.getId() + "</reply_id>");
		stringBuffer.append("\t<post_id>" + reply.getPost_id() + "</post_id>");
		stringBuffer.append("\t<date>" + reply.getDate() + "</date>");
		stringBuffer.append("\t<consumer_name>" + reply.getConsumer_name()
				+ "</consumer_name>");
		stringBuffer.append("\t<seller_name>" + reply.getSeller_name()
				+ "</seller_name>");
		stringBuffer.append("\t<seller_num>"+reply.getSeller_num()+"</seller_num>");
		stringBuffer.append("\t<seller_phone>"+reply.getSeller_phone()+"</seller_phone>");
		stringBuffer.append("\t<contents>" + reply.getContents()
				+ "</contents>");
		stringBuffer.append("\t<image>" + Utils.sumImgName(reply.getReply_images_name())
				+ "</image>");
		stringBuffer.append("\t<pflag>"+ reply.getPflag() +"</pflag>");
		stringBuffer.append("\t<parent>"+reply.getParent()+"</parent>");
		stringBuffer.append("</item>");
		stringBuffer.append("</items>");
		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}
	private String makeXmlConsumerRequestReply(Reply reply,Seller seller) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<items>");
		stringBuffer.append("<item>");
		stringBuffer.append("<action>" + action + "</action>");
		stringBuffer.append("\t<reply_id>" + reply.getId() + "</reply_id>");
		stringBuffer.append("\t<post_id>" + reply.getPost_id() + "</post_id>");
		stringBuffer.append("\t<date>" + reply.getDate() + "</date>");
		stringBuffer.append("\t<consumer_name>" + reply.getConsumer_name()
				+ "</consumer_name>");
		stringBuffer.append("\t<seller_name>" + reply.getSeller_name()
				+ "</seller_name>");
		stringBuffer.append("\t<seller_num>"+seller.getSeller_num()+"</seller_num>");
		stringBuffer.append("\t<seller_phone>"+seller.getSeller_phone()+"</seller_phone>");
		stringBuffer.append("\t<contents>" + reply.getContents()
				+ "</contents>");
		stringBuffer.append("\t<image>" + Utils.sumImgName(reply.getReply_images_name())
				+ "</image>");
		stringBuffer.append("\t<pflag>"+ reply.getPflag() +"</pflag>");
		stringBuffer.append("\t<parent>"+reply.getParent()+"</parent>");
		stringBuffer.append("</item>");
		stringBuffer.append("</items>");
		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}
}
