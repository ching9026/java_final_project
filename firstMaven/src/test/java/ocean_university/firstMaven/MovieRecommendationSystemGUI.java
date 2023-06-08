/*
package ocean_university.firstMaven;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class MovieRecommendationSystemGUI extends JFrame {

    private ArrayList<String> actorList; // ArrayList to store actor names
    private JComboBox<String> countryComboBox;
    private JComboBox<String> genreComboBox;
    private JButton searchButton;
    private JTextArea resultsTextArea;

    private JTextField actorTextField;

    // constructor
    public MovieRecommendationSystemGUI() {
        actorList = new ArrayList<>(); // initialize the ArrayList

        // set frame properties
        setTitle("Movie Recommendation System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center the window
        setLayout(new BorderLayout()); // set layout to border layout

        // create top panel for filtering options and search button
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("國家:"));

        // 发起API请求并获取JSON数据
        String apiKey = "8b8b3232fbf167821df5e4593434bca3";
        String countryApiUrl = "https://api.themoviedb.org/3/configuration/countries?api_key=" + apiKey;
        String genreApiUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey;
        try {
            // 发起API请求并获取JSON数据 - 获取国家列表
            URL countryUrl = new URL(countryApiUrl);
            HttpURLConnection countryConnection = (HttpURLConnection) countryUrl.openConnection();
            countryConnection.setRequestMethod("GET");
            BufferedReader countryReader = new BufferedReader(
                    new InputStreamReader(countryConnection.getInputStream()));
            String countryInputLine;
            StringBuilder countryContent = new StringBuilder();
            while ((countryInputLine = countryReader.readLine()) != null) {
                countryContent.append(countryInputLine);
            }
            countryReader.close();
            countryConnection.disconnect();

            // 解析JSON数据 - 获取国家列表
            Gson gson = new Gson();
            JsonArray countryJsonArray = gson.fromJson(countryContent.toString(), JsonArray.class);

            // 创建国家数组
            DefaultComboBoxModel<String> countryModel = new DefaultComboBoxModel<>();

            // 遍历数组并获取每个国家的英文名称
            for (JsonElement element : countryJsonArray) {
                JsonObject country = element.getAsJsonObject();
                String englishName = country.get("english_name").getAsString();
                countryModel.addElement(englishName);
            }

            // 创建并配置国家JComboBox
            countryComboBox = new JComboBox<>(countryModel);
            topPanel.add(countryComboBox);

            // 发起API请求并获取JSON数据 - 获取电影类型列表
            URL genreUrl = new URL(genreApiUrl);
            HttpURLConnection genreConnection = (HttpURLConnection) genreUrl.openConnection();
            genreConnection.setRequestMethod("GET");
            BufferedReader genreReader = new BufferedReader(new InputStreamReader(genreConnection.getInputStream()));
            String genreInputLine;
            StringBuilder genreContent = new StringBuilder();
            while ((genreInputLine = genreReader.readLine()) != null) {
                genreContent.append(genreInputLine);
            }
            genreReader.close();
            genreConnection.disconnect();

            // 解析JSON数据 - 获取电影类型列表
            JsonObject genreJsonObject = gson.fromJson(genreContent.toString(), JsonObject.class);
            JsonArray genreJsonArray = genreJsonObject.getAsJsonArray("genres");

            // 创建电影类型数组
            DefaultComboBoxModel<String> genreModel = new DefaultComboBoxModel<>();

            // 遍历数组并获取每个电影类型的名称
            for (JsonElement element : genreJsonArray) {
                JsonObject genre = element.getAsJsonObject();
                String name = genre.get("name").getAsString();
                genreModel.addElement(name);
            }

            // 创建并配置电影类型JComboBox
            genreComboBox = new JComboBox<>(genreModel);
            topPanel.add(new JLabel("電影類型:"));
            topPanel.add(genreComboBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
        actorTextField = new JTextField(10);
        topPanel.add(new JLabel("Actor:"));
        topPanel.add(actorTextField);

        // 创建搜索按钮
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // 创建中心面板以顯示結果
        JPanel centerPanel = new JPanel(new BorderLayout());
        resultsTextArea = new JTextArea();
        resultsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsTextArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // show the frame
        setVisible(true);
    }

    // 執行搜尋
    private void performSearch() {
        String country = (String) countryComboBox.getSelectedItem();
        String genre = (String) genreComboBox.getSelectedItem();
        String actor =  actorTextField.getText(); // 目前忽略 actor 欄位

        // 清空結果文本區域
        resultsTextArea.setText("");

        // 使用選擇的條件進行搜尋，這裡只是示範，你需要根據實際需求進行搜尋和顯示結果
        resultsTextArea.append("演員: " + actor + "\n");


        getActorMovies1(country, genre,actor);

    }
    private void getActorMovies1(String country, String genre, String actorName) {
        // 将演员名称转换为URL编码格式，以便在API请求中使用
        String encodedActorName = URLEncoder.encode(actorName, StandardCharsets.UTF_8);

        // 构建API请求URL
        String apiKey = "8b8b3232fbf167821df5e4593434bca3";

        try {
            // 发起API请求并获取JSON数据
            String apiUrl = "https://api.themoviedb.org/3/search/person?query=" + encodedActorName + "&include_adult=false&language=zh-TW&page=1&api_key=" + apiKey;
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

            // 检查是否有结果
            if (results.size() > 0) {
                JsonObject actor = results.get(0).getAsJsonObject();
                int actorId = actor.get("id").getAsInt();
                getFilteredActorMoviesById(actorId, country, genre);
            } else {
                System.out.println("No actor found with the name: " + actorName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFilteredActorMoviesById(int actorId, String country, String genre) {
        String apiKey = "8b8b3232fbf167821df5e4593434bca3";

        try {
            String apiUrl = "https://api.themoviedb.org/3/person/" + actorId + "/movie_credits?api_key=" + apiKey;
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

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);
            JsonArray cast = jsonObject.getAsJsonArray("cast");

            resultsTextArea.append("其演出的電影:\n");

            // Fetch the genre list from TMDB API
            String genreListUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey;
            URL genreUrl = new URL(genreListUrl);
            HttpURLConnection genreConnection = (HttpURLConnection) genreUrl.openConnection();
            genreConnection.setRequestMethod("GET");
            BufferedReader genreReader = new BufferedReader(new InputStreamReader(genreConnection.getInputStream()));
            StringBuilder genreContent = new StringBuilder();
            while ((inputLine = genreReader.readLine()) != null) {
                genreContent.append(inputLine);
            }
            genreReader.close();
            genreConnection.disconnect();

            JsonObject genreListObject = gson.fromJson(genreContent.toString(), JsonObject.class);
            JsonArray genreArray = genreListObject.getAsJsonArray("genres");

            for (JsonElement movie : cast) {
                JsonObject movieObject = movie.getAsJsonObject();
                String title = movieObject.get("title").getAsString();
                JsonArray genreIdsArray = movieObject.getAsJsonArray("genre_ids");

                boolean countryMatched = false;
                boolean genreMatched = false;

                if (!country.equals("All")) {
                    String movieCountry = getMovieCountry(movieObject);
                    if (movieCountry.equals(country)) {
                        countryMatched = true;
                    }
                } else {
                    countryMatched = true;
                }

                if (!genre.equals("All")) {
                    for (JsonElement genreIdElement : genreIdsArray) {
                        int genreId = genreIdElement.getAsInt();
                        String genreName = getGenreName(genreId, genreArray);
                        if (genreName != null && genreName.equals(genre)) {
                            genreMatched = true;
                            break;
                        }
                    }
                } else {
                    genreMatched = true;
                }

                if (countryMatched && genreMatched) {
                    resultsTextArea.append("名稱: " + title + ", 國家: " + country + ", 電影類型: " + genre + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private String getGenreName(int genreId, JsonArray genreArray) {
        for (JsonElement genreElement : genreArray) {
            JsonObject genreObject = genreElement.getAsJsonObject();
            int id = genreObject.get("id").getAsInt();
            if (id == genreId) {
                return genreObject.get("name").getAsString();
            }
        }
        return null;
    }
    private String getMovieCountry(JsonObject movieObject) {
        String apiKey = "8b8b3232fbf167821df5e4593434bca3";
        int movieId = movieObject.get("id").getAsInt();

        try {
            String apiUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
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

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);
            JsonArray productionCountries = jsonObject.getAsJsonArray("production_countries");

            // 如果有多個產地，只返回第一個
            if (productionCountries.size() > 0) {
                JsonObject countryObject = productionCountries.get(0).getAsJsonObject();
                return countryObject.get("name").getAsString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }




    public static void main(String[] args) {
        new MovieRecommendationSystemGUI();
    }
}*/