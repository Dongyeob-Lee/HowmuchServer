package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import servlet.Reply;

public class ReplyDao {
	private static ReplyDao instance = new ReplyDao();

	private ReplyDao() {

	}

	public static ReplyDao getInstance() {

		return instance;
	}

	public Reply getReply(int reply_id, String tableName) {

		Reply reply = new Reply();

		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		String sql = "SELECT * FROM "+tableName+" where id='"
				+ String.valueOf(reply_id) + "'";

		try {
			conn = mysql.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);

			while (resultSet.next()) {
				reply.setId(resultSet.getInt("id"));
				reply.setPost_id(resultSet.getInt("post_id"));
				reply.setDate(resultSet.getString("date"));
				reply.setConsumer_name(resultSet
						.getString("consumer_name"));
				reply.setSeller_name(resultSet.getString("seller_name"));
				reply.setSeller_num(resultSet.getString("seller_num"));
				reply.setSeller_phone(resultSet.getString("seller_phone"));
				reply.setContents(resultSet.getString("contents"));
				if(resultSet.getString("image")!=null){
					reply.setReply_images_name(Utils.getImgNameToken(resultSet.getString("image")));
				}
				reply.setPflag(resultSet.getInt("pflag"));
				reply.setParent(resultSet.getInt("parent"));
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

		return reply;
	}

	

	public int saveReply(Reply reply, String tableName) {
		int reply_id = -1;
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			DBCPUtils mysql = DBCPUtils.getInstance();
			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체 // 얻어온다.
			String sql = "insert into "+tableName+"(post_id,seller_name,seller_num,seller_phone,consumer_name,date,contents,image,pflag,parent) values(?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.

			pstmt.setInt(1, reply.getPost_id());
			pstmt.setString(2, reply.getSeller_name());
			pstmt.setString(3, reply.getSeller_num());
			pstmt.setString(4, reply.getSeller_phone());
			pstmt.setString(5, reply.getConsumer_name());
			pstmt.setString(6, reply.getDate());
			pstmt.setString(7, reply.getContents());
			if(reply.getReply_images_name().size()!=0){
				pstmt.setString(8, Utils.sumImgName(reply.getReply_images_name()));
			}else{
				pstmt.setString(8, null);
			}
			pstmt.setInt(9, reply.getPflag());
			pstmt.setInt(10, reply.getParent());
			
			int result = pstmt.executeUpdate();
			if (result > 0) {
				String sql2 = "SELECT id FROM "+tableName+" where post_id='"
						+ reply.getPost_id() + "' and seller_num='"
						+ reply.getSeller_num() + "' and consumer_name='"
						+ reply.getConsumer_name() + "' and date='"
						+ reply.getDate() + "' and contents='"
						+ reply.getContents() + "'";
				statement = conn.createStatement();
				resultSet = statement.executeQuery(sql2);
				if (resultSet.next()) {
					reply_id = resultSet.getInt("id");
				} else {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				resultSet.close();
				pstmt.close();
				statement.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return reply_id;
	}
	public String getSeller_idFromReply(String seller_reply_id) {
		String seller_num=null;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		String sql = "SELECT seller_num FROM seller_reply where id='"
				+ seller_reply_id + "'";

		try {
			conn = mysql.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				seller_num = resultSet.getString("seller_num");
			}
			if (seller_num==null) {
				System.out.println("실패");
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
		return seller_num;
	}
	public String getSeller_gcm(String seller_num) {
		String sellerId=null;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		String sql = "SELECT gcm_id FROM seller where seller_num='"
				+ seller_num + "'";

		try {
			conn = mysql.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				sellerId = resultSet.getString("gcm_id");
			}
			if (sellerId==null) {
				System.out.println("실패");
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
		return sellerId;
	}
	public String getConsumer_gcm(String consumer_name) {
		String consumerId=null;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		String sql = "SELECT gcm_id FROM consumer where consumer_name='"
				+ consumer_name + "'";

		try {
			conn = mysql.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				consumerId = resultSet.getString("gcm_id");
			}
			if (consumerId==null) {
				return null;
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

		return consumerId;
	}
}
