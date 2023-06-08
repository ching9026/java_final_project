/*package ocean_university.firstMaven;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.EventObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MovieRecommendationSystem extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> countryComboBox;
    private JComboBox<String> genreComboBox;
    private JButton searchButton;
    private JTable resultsTable;
    private JTextField actorTextField;

    // constructor
    public MovieRecommendationSystem() {
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
        String countryApiUrl = "https://api.themoviedb.org/3/configuration/countries?language=zh-TW&api_key=" + apiKey;
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
                String genreName = genre.get("name").getAsString();
                genreModel.addElement(genreName);
            }

            // 创建并配置电影类型JComboBox
            genreComboBox = new JComboBox<>(genreModel);
            topPanel.add(new JLabel("電影類型:"));
            topPanel.add(genreComboBox);

            // 创建搜索按钮
            searchButton = new JButton("搜尋");
            searchButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    performSearch();
                }
            });
            topPanel.add(searchButton);

            // 添加topPanel到frame的北部
            add(topPanel, BorderLayout.NORTH);

            // 创建结果表格
            resultsTable = new JTable();
            JScrollPane scrollPane = new JScrollPane(resultsTable);
            add(scrollPane, BorderLayout.CENTER);

            // 创建底部面板用于演员输入
            JPanel bottomPanel = new JPanel(new FlowLayout());
            bottomPanel.add(new JLabel("演員:"));

            // 创建演员输入框
            actorTextField = new JTextField(20);
            bottomPanel.add(actorTextField);

            // 添加底部面板到frame的南部
            add(bottomPanel, BorderLayout.SOUTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 执行搜索操作
    private void performSearch() {
        // 获取选择的国家和类型
        String selectedCountry = (String) countryComboBox.getSelectedItem();
        String selectedGenre = (String) genreComboBox.getSelectedItem();
        String actorName = actorTextField.getText();

        // 发起API请求并获取JSON数据 - 获取电影列表
        String apiKey = "8b8b3232fbf167821df5e4593434bca3";
        String searchApiUrl = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey
                + "&language=zh-TW&query=%s&region=%s&with_genres=%s";

        try {
            String encodedQuery = java.net.URLEncoder.encode(actorName, StandardCharsets.UTF_8.toString());
            String formattedUrl = String.format(searchApiUrl, encodedQuery, selectedCountry, selectedGenre);
            URL searchUrl = new URL(formattedUrl);
            HttpURLConnection searchConnection = (HttpURLConnection) searchUrl.openConnection();
            searchConnection.setRequestMethod("GET");
            BufferedReader searchReader = new BufferedReader(new InputStreamReader(searchConnection.getInputStream()));
            String searchInputLine;
            StringBuilder searchContent = new StringBuilder();
            while ((searchInputLine = searchReader.readLine()) != null) {
                searchContent.append(searchInputLine);
            }
            searchReader.close();
            searchConnection.disconnect();

            // 解析JSON数据 - 获取电影列表
            JsonObject searchJsonObject = gson.fromJson(searchContent.toString(), JsonObject.class);
            JsonArray resultsJsonArray = searchJsonObject.getAsJsonArray("results");

            // 创建结果数据模型
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("電影名稱");
            model.addColumn("評分");

            // 遍历结果数组并提取电影名称和评分
            for (JsonElement element : resultsJsonArray) {
                JsonObject movie = element.getAsJsonObject();
                String movieTitle = movie.get("title").getAsString();
                double movieRating = movie.get("vote_average").getAsDouble();
                model.addRow(new Object[]{movieTitle, movieRating});
            }

            // 将模型设置到结果表格
            resultsTable.setModel(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 创建并显示GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MovieRecommendationSystem().setVisible(true);
            }
        });
    }
}
*/