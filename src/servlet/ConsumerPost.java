package servlet;


import java.util.ArrayList;


public class ConsumerPost {
	private int id;
	private String date;
	private String consumer_name;
	private String title;
	private String category;
	private String city;
	private String contents;
	private ArrayList<String> post_images_name = new ArrayList<String>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<String> getPost_images_name() {
		return post_images_name;
	}
	public void setPost_images_name(ArrayList<String> post_images_name) {
		this.post_images_name = post_images_name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getConsumer_name() {
		return consumer_name;
	}
	public void setConsumer_name(String consumer_name) {
		this.consumer_name = consumer_name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
}
