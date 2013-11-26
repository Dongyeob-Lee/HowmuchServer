package servlet;



import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.DBCPUtils;
import database.Dao;

/**
 * Servlet implementation class DiaryServlet
 */
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String CONSUMER_REGISTER = "consumer_register";
	public static final String CONSUMER_CHECKNAME = "consumer_checkname";
	public static final String CONSUMER_UPDATE_GCM = "consumer_update_gcm";
	public static final String CONSUMER_LOGOUT = "consumer_logout";
	public static final String SELLER_REGISTER = "seller_register";
	public static final String SELLER_CHECKNAME = "seller_checkname";
	public static final String SELLER_CHARGE_PLANE = "seller_charge_plane";
	public static final String SELLER_ADD_CATEGORY = "seller_add_category";
	public static final String SELLER_REMOVE_CATEGORY = "seller_remove_category";
	public static final String SELLER_UPDATE_GCM = "seller_update_gcm";
	public static final String SELLER_UPDATE = "seller_update";
	public static final String SELLER_LOGOUT = "seller_logout";
	public static final String SELLER_DELETE = "seller_delete";
	public static final String SELLER_LOGIN = "seller_login";
	public static final String API_KEY = "AIzaSyCY9IpwghSoNI0KfSwuHtWOOdO9nurLCiA";
	String action;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.service(arg0, arg1);
		System.out.println("service 호출 서블릿 생성");
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("on destroy 호출 서블릭 종료");
		super.destroy();
	}

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
		request.setCharacterEncoding("euc-kr");
		
		 action = request.getParameter("action"); // 넘어 오는 액션에 따라 처리
		 System.out.println("userServlet action:"+action);
		 if(action.equals(CONSUMER_CHECKNAME)) { 
			 //구매자 등록 중복 체크
			 userNameCheck(request, response); 
		} else if(action.equals(SELLER_CHECKNAME)) 
		{ //판매자 중복체크
			userNameCheck(request,response);
		} else if(action.equals(CONSUMER_REGISTER)){
			userJoin(request, response);
		} else if(action.equals(SELLER_UPDATE)){
			
		}else if(action.equals(SELLER_REGISTER)){
			userJoin(request, response);
		} else if(action.equals(SELLER_ADD_CATEGORY)){
			sellerAddCategory(request,response);
		} else if(action.equals(SELLER_REMOVE_CATEGORY)){
			sellerRemoveCategory(request, response);
		} else if(action.equals(CONSUMER_UPDATE_GCM)){
			updateConsumerGCM(request, response);
		} else if(action.equals(SELLER_UPDATE_GCM)){
			updateSellerGCM(request, response);
		} else if(action.equals(SELLER_CHARGE_PLANE)){
			sellerChargePlane(request, response);
		}else if(action.equals(CONSUMER_LOGOUT)){
			consumerLogout(request, response);
		}else if(action.equals(SELLER_LOGOUT)){
			sellerLogout(request, response);
		}else if(action.equals(SELLER_DELETE)){
			sellerDelete(request, response);
		}else if(action.equals(SELLER_LOGIN)){
			sellerLogin(request,response);
		}
		destroy();
	}
	private void sellerLogin(HttpServletRequest request,
			HttpServletResponse response) {
		boolean result = false;
		Dao dao = Dao.getInstance();
		
		
		Seller seller = new Seller();
		seller.setSeller_num(request.getParameter("seller_num"));
		seller.setGcm_id(request.getParameter("gcm_id"));
		seller.setSeller_pw(request.getParameter("seller_pw"));
		dao.deleteSellerCategory(seller.getSeller_num());
		//first login
		Seller sellerInfo = dao.getSellerFromNum(seller.getSeller_num());
		
		if(sellerInfo==null){
			//존재하지 않는 아이디
			result = false;
		}else if(sellerInfo.getSeller_pw().equals(seller.getSeller_pw())){
			result = true;
		}else{
			result = false;
		}
		
		if(result){
			result = dao.sellerGCMUpdate(seller);
		}
		
		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeLoginXmlResponse(result, action,sellerInfo));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sellerDelete(HttpServletRequest request,
			HttpServletResponse response) {
		boolean result=false;
		Dao dao = Dao.getInstance();
		String seller_num = request.getParameter("seller_num");
		result = dao.sellerDelete(seller_num);
		dao.deleteSellerCategory(seller_num);
		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlResponse(result, action));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sellerLogout(HttpServletRequest request,
			HttpServletResponse response) {
		boolean result=false;
		Dao dao = Dao.getInstance();
		String seller_num = request.getParameter("seller_num");
		result = dao.sellerLogout(seller_num);
		dao.deleteSellerCategory(seller_num);
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlResponse(result, action));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void consumerLogout(HttpServletRequest request,
			HttpServletResponse response) {
		boolean result=false;
		Dao dao = Dao.getInstance();
		result = dao.consumerLogout(request.getParameter("consumer_name"));
		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlResponse(result, action));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sellerUpdate(HttpServletRequest request, HttpServletResponse response){
		boolean result=false;
		Dao dao = Dao.getInstance();
	
			Seller seller = new Seller();
			seller.setSeller_name(request.getParameter("seller_name"));
			seller.setSeller_num(request.getParameter("seller_num"));
			seller.setSeller_city(request.getParameter("seller_city"));
			seller.setSeller_address(request.getParameter("seller_address"));
			seller.setSeller_phone(request.getParameter("seller_phone"));
			result = dao.sellerUpdate(seller);
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlResponse(result,SELLER_UPDATE));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sellerChargePlane(HttpServletRequest request, HttpServletResponse response){
		boolean result=false;
		Dao dao = Dao.getInstance();
		
		Seller seller = new Seller();
		int plane;
		seller.setSeller_num(request.getParameter("seller_num"));
		seller.setPlane(Integer.parseInt(request.getParameter("plane")));
		plane = dao.sellerChargePlane(seller.getSeller_num(), seller.getPlane());
		response.setContentType("text/xml");
		if(plane>0){
			result =true;
		}else {
			result = false;
		}
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makePlaneXmlResponse(result,SELLER_CHARGE_PLANE,plane));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void updateSellerGCM(HttpServletRequest request, HttpServletResponse response){
		boolean result=false;
		Dao dao = Dao.getInstance();
	
			Seller seller = new Seller();
			seller.setSeller_num(request.getParameter("seller_num"));
			seller.setGcm_id(request.getParameter("gcm_id"));
			result = dao.sellerGCMUpdate(seller);
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlResponse(result,SELLER_UPDATE_GCM));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public void updateConsumerGCM(HttpServletRequest request, HttpServletResponse response){
		boolean result=false;
		Dao dao = Dao.getInstance();
			Consumer consumer = new Consumer();
			consumer.setConsumer_name(request.getParameter("consumer_name"));
			consumer.setGcm_id(request.getParameter("gcm_id"));
			result = dao.consumerGCMUpdate(consumer);
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlResponse(result,CONSUMER_UPDATE_GCM));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public String getUserNameByNum(String num) {
		String name = null;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		String sql = "SELECT username FROM ptp_user where phonenumber='" + num
				+ "'";

		try {
			conn = mysql.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				name = resultSet.getString("username");
				// System.out.println(resultSet.getString("reg_id"));
			} else {
				System.out.println("실패");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	// 유저 이름으로 유저의 등록아이디를 받아온다.
	public String getUserRegIdByName(String name) {
		String regId = null;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		String sql = "SELECT reg_id FROM ptp_user where username='" + name
				+ "'";

		try {
			conn = mysql.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				regId = resultSet.getString("reg_id");
				System.out.println(resultSet.getString("reg_id"));
			} else {
				System.out.println("실패");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return regId;
	}
	private void sellerAddCategory(HttpServletRequest request,HttpServletResponse response){
		boolean result = false;
		Dao dao = Dao.getInstance();
		if(dao.sellerAddCategory(request.getParameter("seller_num"), request.getParameter("category"))){
			result = true;
		}
		response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");
		try {
			response.getWriter().write(makeXmlResponse(result,action));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void sellerRemoveCategory(HttpServletRequest request, HttpServletResponse response){
		boolean result = false;
		Dao dao = Dao.getInstance();
		if(dao.sellerRemoveCategory(request.getParameter("seller_num"), request.getParameter("category"))){
			result = true;
		}
		response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");
		try {
			response.getWriter().write(makeXmlResponse(result,action));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void userNameCheck(HttpServletRequest request, HttpServletResponse response){
		boolean result=false;
		Dao dao = Dao.getInstance();
		if(action.equals(CONSUMER_CHECKNAME)){
			result = dao.userNameCheck(request.getParameter("consumer_name"), "consumer");
		}else{
			result = dao.userNameCheck(request.getParameter("seller_num"), "seller");
		}
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlResponse(result, action));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void userJoin(HttpServletRequest request, HttpServletResponse response) {
		boolean result=false;
		Dao dao = Dao.getInstance();
		if(action.equals(CONSUMER_REGISTER)){
			Consumer consumer = new Consumer();
			consumer.setConsumer_name(request.getParameter("consumer_name"));
			consumer.setGcm_id(request.getParameter("gcm_id"));
			result = dao.userConsumerJoin(consumer);
		}else {
			Seller seller = new Seller();
			seller.setSeller_name(request.getParameter("seller_name"));
			seller.setSeller_num(request.getParameter("seller_num"));
			seller.setSeller_pw(request.getParameter("seller_pw"));
			seller.setSeller_city(request.getParameter("seller_city"));
			seller.setSeller_address(request.getParameter("seller_address"));
			seller.setSeller_phone(request.getParameter("seller_phone"));
			seller.setGcm_id(request.getParameter("gcm_id"));
			seller.setPlane(10);
			result = dao.userSellerJoin(seller);
		}
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(makeXmlResponse(result,action));
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String makePlaneXmlResponse(boolean paramresult,String title,int plane) {
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
		stringBuffer.append("\t<plane>" +String.valueOf(plane)+"</plane>");
		stringBuffer.append("</item>");
		stringBuffer.append("</items>");
		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	} 
	private String makeXmlResponse(boolean paramresult,String title) {
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
	private String makeLoginXmlResponse(boolean paramresult,String title, Seller seller) {
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

		stringBuffer.append("\t<seller_name>"+seller.getSeller_name()+"</seller_name>");
		stringBuffer.append("\t<seller_city>"+seller.getSeller_city()+"</seller_city>");
		stringBuffer.append("\t<seller_address>"+seller.getSeller_address()+"</seller_address>");
		stringBuffer.append("\t<seller_phone>"+seller.getSeller_phone()+"</seller_phone>");
		stringBuffer.append("\t<plane>"+seller.getPlane()+"</plane>");
		
		stringBuffer.append("</item>");
		stringBuffer.append("</items>");
		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}
}
