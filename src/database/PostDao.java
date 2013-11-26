package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import servlet.ConsumerPost;

public class PostDao {
	private static PostDao instance = new PostDao();

	private PostDao() {

	}

	public static PostDao getInstance() {

		return instance;
	}
	
	public ConsumerPost getPost(int post_id){
		
		ConsumerPost consumerPost = new ConsumerPost();
		
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		String sql = "SELECT * FROM consumer_post where id='"
				+ String.valueOf(post_id) + "'";
		
		try {
			conn = mysql.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
			
			while(resultSet.next()){
				consumerPost.setId(resultSet.getInt("id"));
				consumerPost.setDate(resultSet.getString("date"));
				consumerPost.setConsumer_name(resultSet.getString("consumer_name"));
				consumerPost.setTitle(resultSet.getString("title"));
				consumerPost.setCategory(resultSet.getString("category"));
				consumerPost.setCity(resultSet.getString("city"));
				consumerPost.setContents(resultSet.getString("contents"));
				String image = resultSet.getString("image");
				if(image!=null){
					consumerPost.setPost_images_name(Utils.getImgNameToken(image));
				}
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
		
		return consumerPost;
	}
	
	
	public ArrayList<String> sellers_gcm(String city, String category){
		ArrayList<String> sellers = new ArrayList<String>();
		ArrayList<String> sellersId = new ArrayList<String>();
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		DBCPUtils mysql = DBCPUtils.getInstance();
		String sql = "SELECT seller_num FROM seller_category where category_num='"
				+ category + "'";

		try {
			conn = mysql.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				sellers.add(resultSet.getString("seller_num"));
			}
			if (sellers.size() == 0) {
				System.out.println("해당 판매자 없음");
			}
			if (sellers.size() > 0) {
				if (city.equals("전국")) {
					String where = "seller_num='" + sellers.get(0) + "'";
					for (int i = 1; i < sellers.size(); i++) {
						where = where + " or seller_num='" + sellers.get(i)
								+ "'";
					}
					sql = "SELECT gcm_id FROM seller where " + where;
				} else {
					String where = "(seller_num='" + sellers.get(0)
							+ "' and seller_city='" + city + "')";
					for (int i = 1; i < sellers.size(); i++) {
						where = where + " or (seller_num'" + sellers.get(i)
								+ "' and seller_city='" + city + "')";
					}
					sql = "SELECT gcm_id FROM seller where " + where;
				}
				resultSet = statement.executeQuery(sql);
				while (resultSet.next()) {
					System.out.println("seller gcm _id:"+resultSet.getString("gcm_id"));
					sellersId.add(resultSet.getString("gcm_id"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				resultSet.close();
				statement.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sellersId;
	}
	public int savePost(ConsumerPost consumerPost){
		int post_id=-1;
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			System.out.println("savePost");
			DBCPUtils mysql = DBCPUtils.getInstance();

			conn = mysql.getConnection();// 드라이브매니저객체를
											// 이용하여
											// 커넥션객체 // 얻어온다.
			String sql = "insert into consumer_post(date,consumer_name,title,category,city,contents,image) values(?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);// 미완성된 문장 상태로 얻어올수 있다.
			
			pstmt.setString(1, consumerPost.getDate());
			pstmt.setString(2, consumerPost.getConsumer_name());
			pstmt.setString(3, consumerPost.getTitle());
			pstmt.setString(4, consumerPost.getCategory());
			pstmt.setString(5, consumerPost.getCity());
			pstmt.setString(6, consumerPost.getContents());
			if(consumerPost.getPost_images_name().size()!=0){
				pstmt.setString(7, Utils.sumImgName(consumerPost.getPost_images_name()));
			}else{
				pstmt.setString(7, null);
			}
			
			int result = pstmt.executeUpdate();
			if (result > 0) {
				System.out.println("result > 0");
				String sql2 = "SELECT id FROM consumer_post where date='"+consumerPost.getDate()+"' and consumer_name='"+consumerPost.getConsumer_name()+"' and title='"+consumerPost.getTitle()+"' and category='"+consumerPost.getCategory()+"' and contents='"+consumerPost.getContents()+"'";
				// response.sendRedirect("viewtest.jsp");
				statement = conn.createStatement();
				resultSet = statement.executeQuery(sql2);
				if(resultSet.next()){
					post_id = resultSet.getInt("id");
				}else{
					System.out.println("대실패");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {// 문제가 있든 없든 연결을 끊음
			try {
				resultSet.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return post_id;
	}
}
