//package ocean_university.firstMaven;
/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class App {

    public static void main(String[] args) {
        String apiKey = "8b8b3232fbf167821df5e4593434bca3";
        //String apiUrl = "https://api.themoviedb.org/3/discover/movie?language=zh-TW&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&year=2023&with_watch_monetization_types=flatrate&api_key=" + apiKey;
        
        String apiUrl = "https://api.themoviedb.org/3/discover/movie?api_key=8b8b3232fbf167821df5e4593434bca3&language=en-US&sort_by=popularity.desc&certification_country=China&include_adult=false&include_video=false&page=1&with_watch_monetization_types=flatrate";
        
        try {
            // 发起API请求并获取JSON数据
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                content.append(inputLine);
            }
            reader.close();
            connection.disconnect();
            
            // 解析JSON数据
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);
            JsonArray results = jsonObject.getAsJsonArray("results");

            // 遍历结果数组并获取每个电影的标题
            for (JsonElement result : results) {
                JsonObject movie = result.getAsJsonObject();
                String title = movie.get("title").getAsString();
                System.out.println("Title: " + title);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/
