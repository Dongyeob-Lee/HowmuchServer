package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import servlet.Consumer;
import servlet.Seller;

public class Dao {
	private static Dao instance = new Dao();

	private Dao() {

	}

	public static Dao getInstance() {

		return instance;
	}
	
	public boolean consumerLogout(String consumer_name){
		boolean paramresult=false;
		Connection conn = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
			PreparedStatement pstmt = null;
			try {
				String sql = "delete from consumer where consumer_name=?";

				conn = mysql.getConnection();// 드라이브매니저객체를
												// 이용하여
												// 커넥션객체 // 얻어온다.
				pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

				pstmt.setString(1, consumer_name);

				int result = pstmt.executeUpdate();

				if (result > 0) {
					paramresult = true;
					// response.sendRedirect("viewtest.jsp");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {// 문제가 있든 없든 연결을 끊음
				try {
					pstmt.close();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return paramresult;
	}
	public boolean sellerDelete(String seller_num){
		boolean paramresult=false;
		Connection conn = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
			PreparedStatement pstmt = null;
			try {
				String sql = "delete from seller where seller_num=?";

				conn = mysql.getConnection();// 드라이브매니저객체를
												// 이용하여
												// 커넥션객체 // 얻어온다.
				pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

				pstmt.setString(1, seller_num);

				int result = pstmt.executeUpdate();

				if (result > 0) {
					paramresult = true;
					// response.sendRedirect("viewtest.jsp");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {// 문제가 있든 없든 연결을 끊음
				try {
					pstmt.close();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return paramresult;
	}
	public boolean deleteSellerCategory(String seller_num){
		boolean paramresult=false;
		Connection conn = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
			PreparedStatement pstmt = null;
			try {
				String sql = "delete from seller_category where seller_num=?";

				conn = mysql.getConnection();// 드라이브매니저객체를
												// 이용하여
												// 커넥션객체 // 얻어온다.
				pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

				pstmt.setString(1, seller_num);

				int result = pstmt.executeUpdate();

				if (result > 0) {
					paramresult = true;
					// response.sendRedirect("viewtest.jsp");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {// 문제가 있든 없든 연결을 끊음
				try {
					pstmt.close();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return paramresult;
	}
	public boolean sellerLogout(String seller_num){
		boolean paramresult=false;
		Connection conn = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
			PreparedStatement pstmt = null;
			try {
				String sql = "update seller set gcm_id='logout' where seller_num=?";

				conn = mysql.getConnection();// 드라이브매니저객체를
												// 이용하여
												// 커넥션객체 // 얻어온다.
				pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

				pstmt.setString(1, seller_num);

				int result = pstmt.executeUpdate();

				if (result > 0) {
					paramresult = true;
					// response.sendRedirect("viewtest.jsp");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {// 문제가 있든 없든 연결을 끊음
				try {
					pstmt.close();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return paramresult;
	}
	public boolean sellerReducePlane(String seller_num){
		int resultPlane=0;
		boolean paramresult=false;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		try {

			String sql = null;
			sql = "SELECT plane FROM seller where seller_num='" + seller_num
					+ "'";

			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체를
											// 얻어온다.
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);

			if (resultSet.next()) {
				resultPlane = resultSet.getInt("plane");
			} else {
				resultPlane = -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				resultSet.close();
				statement.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(resultPlane == -1){
			paramresult=false;
		}else{
			PreparedStatement pstmt = null;
			try {
				String sql = "update seller set plane=? where seller_num=?";

				conn = mysql.getConnection();// 드라이브매니저객체를
												// 이용하여
												// 커넥션객체 // 얻어온다.
				pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

				pstmt.setInt(1, resultPlane-1);
				pstmt.setString(2, seller_num);

				int result = pstmt.executeUpdate();

				if (result > 0) {
					paramresult = true;
					// response.sendRedirect("viewtest.jsp");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {// 문제가 있든 없든 연결을 끊음
				try {
					pstmt.close();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return paramresult;
	}
	public int sellerChargePlane(String seller_num, int plane) {
		int resultPlane=0;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		System.out.println("seller num :"+seller_num+", plane :"+plane);
		try {

			String sql = null;
			sql = "SELECT plane FROM seller where seller_num='" + seller_num
					+ "'";

			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체를
											// 얻어온다.
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);

			if (resultSet.next()) {
				resultPlane = resultSet.getInt("plane");
			} else {
				resultPlane = -1;
			}
			System.out.println("resultPlane = "+resultPlane);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				resultSet.close();
				statement.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(resultPlane == -1){
			return -1;
		}else{
			PreparedStatement pstmt = null;
			try {
				String sql = "update seller set plane=? where seller_num=?";

				conn = mysql.getConnection();// 드라이브매니저객체를
												// 이용하여
												// 커넥션객체 // 얻어온다.
				pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

				pstmt.setInt(1, resultPlane+plane);
				pstmt.setString(2, seller_num);

				int result = pstmt.executeUpdate();

				if (result > 0) {
					resultPlane = resultPlane+plane;
					// response.sendRedirect("viewtest.jsp");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {// 문제가 있든 없든 연결을 끊음
				try {
					pstmt.close();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return resultPlane;
		}
	}
	public boolean userNameCheck(String user_name, String who) {
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		boolean paramresult = false;
		DBCPUtils mysql = DBCPUtils.getInstance();
		try {

			// Class.forName("com.mysql.jdbc.Driver");
			String sql = null;
			if (who.equals("consumer")) {
				sql = "SELECT * FROM consumer where consumer_name='"
						+ user_name + "'";
			} else {
				sql = "SELECT * FROM seller where seller_num='" + user_name
						+ "'";
			}

			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체를
											// 얻어온다.
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);

			if (!resultSet.next()) {
				paramresult = true;
			} else {
				paramresult = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				resultSet.close();
				statement.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paramresult;
	}

	public boolean consumerGCMUpdate(Consumer consumer) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean paramresult = false;
		try {
			String sql = "update consumer set gcm_id=? where consumer_name=?";

			DBCPUtils mysql = DBCPUtils.getInstance();

			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체 // 얻어온다.
			pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

			pstmt.setString(1, consumer.getGcm_id());
			pstmt.setString(2, consumer.getConsumer_name());

			int result = pstmt.executeUpdate();

			if (result > 0) {
				paramresult = true;
				// response.sendRedirect("viewtest.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paramresult;
	}
	public boolean sellerUpdate(Seller seller) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean paramresult = false;
		try {
			String sql = "update seller set seller_city=?,seller_address=?,seller_phone=? where seller_num=?";

			DBCPUtils mysql = DBCPUtils.getInstance();

			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체 // 얻어온다.
			pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

			pstmt.setString(1, seller.getSeller_city());
			pstmt.setString(2, seller.getSeller_address());
			pstmt.setString(3, seller.getSeller_phone());
			pstmt.setString(4, seller.getSeller_num());

			int result = pstmt.executeUpdate();

			if (result > 0) {
				paramresult = true;
				// response.sendRedirect("viewtest.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paramresult;
	}
	public boolean sellerGCMUpdate(Seller seller) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean paramresult = false;
		try {
			String sql = "update seller set gcm_id=? where seller_num=?";

			DBCPUtils mysql = DBCPUtils.getInstance();

			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체 // 얻어온다.
			pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

			pstmt.setString(1, seller.getGcm_id());
			pstmt.setString(2, seller.getSeller_num());

			int result = pstmt.executeUpdate();

			if (result > 0) {
				paramresult = true;
				// response.sendRedirect("viewtest.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paramresult;
	}

	public boolean userConsumerJoin(Consumer consumer) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean paramresult = false;
		try {
			String sql = "insert into consumer(consumer_name,gcm_id) values(?,?)";

			DBCPUtils mysql = DBCPUtils.getInstance();

			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체 // 얻어온다.
			pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

			pstmt.setString(1, consumer.getConsumer_name());
			pstmt.setString(2, consumer.getGcm_id());

			int result = pstmt.executeUpdate();

			if (result > 0) {
				paramresult = true;
				// response.sendRedirect("viewtest.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paramresult;
	}

	public boolean userSellerJoin(Seller seller) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean paramresult = false;
		try {
			String sql = "insert into seller(seller_name,seller_num,seller_pw,seller_city,seller_address,seller_phone,gcm_id,plane) values(?,?,?,?,?,?,?,?)";

			DBCPUtils mysql = DBCPUtils.getInstance();

			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체 // 얻어온다.
			pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

			pstmt.setString(1, seller.getSeller_name());
			pstmt.setString(2, seller.getSeller_num());
			pstmt.setString(3, seller.getSeller_pw());
			pstmt.setString(4, seller.getSeller_city());
			pstmt.setString(5, seller.getSeller_address());
			pstmt.setString(6, seller.getSeller_phone());
			pstmt.setString(7, seller.getGcm_id());
			pstmt.setInt(8, seller.getPlane());
			int result = pstmt.executeUpdate();

			if (result > 0) {
				paramresult = true;
				// response.sendRedirect("viewtest.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paramresult;
	}

	public boolean sellerAddCategory(String seller_num, String category) {
		boolean paramresult = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			DBCPUtils mysql = DBCPUtils.getInstance();
			String sql = "insert into seller_category(seller_num,category_num) values(?,?)";
			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체 // 얻어온다.
			pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

			pstmt.setString(1, seller_num);
			pstmt.setString(2, category);

			int result = pstmt.executeUpdate();

			if (result > 0) {
				paramresult = true;
				// response.sendRedirect("viewtest.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paramresult;
	}

	public boolean sellerRemoveCategory(String seller_num, String category) {
		boolean paramresult = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			DBCPUtils mysql = DBCPUtils.getInstance();
			String sql = "delete from seller_category where (seller_num=? and category_num=?)";
			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체 // 얻어온다.
			pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

			pstmt.setString(1, seller_num);
			pstmt.setString(2, category);

			int result = pstmt.executeUpdate();

			if (result > 0) {
				paramresult = true;
				// response.sendRedirect("viewtest.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paramresult;
	}
	public Seller getSellerFromNum(String seller_num){
		Seller seller = new Seller();
		boolean valid=false;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		String sql = "SELECT * FROM seller where seller_num='"
				+ seller_num + "'";
		
		try {
			conn = mysql.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
			
			while(resultSet.next()){
				valid = true;
				seller.setSeller_name(resultSet.getString("seller_name"));
				seller.setSeller_num(seller_num);
				seller.setSeller_phone(resultSet.getString("seller_phone"));
				seller.setSeller_address(resultSet.getString("seller_address"));
				seller.setSeller_city(resultSet.getString("seller_city"));
				seller.setSeller_pw(resultSet.getString("seller_pw"));
				seller.setPlane(resultSet.getInt("plane"));
			}
			if(!valid){
				seller = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				resultSet.close();
				statement.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return seller;
	}
}
