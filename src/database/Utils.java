package database;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Utils {
	
	//데이터베이스에 있는 이미지파일명 가져와서 자르기.
	public static ArrayList<String> getImgNameToken(String str){
		ArrayList<String> images = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(str, "/");
		while(tokenizer.hasMoreTokens()){
			String temp = tokenizer.nextToken();
			if(tokenizer.equals("/")){
				
			}else{
				images.add(temp);
			}
		}
		return images;
	}
	public static String sumImgName(ArrayList<String> images){
		String str_image = "";
		for(int i=0; i<images.size(); i++){
			str_image = str_image+"/"+images.get(i);
		}
		return str_image;
	}
}
