package ocean_university.firstMaven;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * 大學 Java 期末專題：電影推薦與個人片單管理系統。
 *
 * <p>敏感資訊不再寫死於原始碼中，執行前請設定：</p>
 * <ul>
 *   <li>TMDB_API_KEY：The Movie Database API Key</li>
 *   <li>MONGODB_URI：MongoDB Atlas / MongoDB 連線字串</li>
 * </ul>
 */
public class MovieRecommendationSystem extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String TMDB_API_KEY = System.getenv("TMDB_API_KEY");
    private static final String MONGODB_URI = System.getenv("MONGODB_URI");

    private String userName;
    private JComboBox<String> countryComboBox;
    private JComboBox<String> genreComboBox;
    private JTable resultsTable;
    private JTextField userTextField;
    private JTextField actorTextField;

    public MovieRecommendationSystem() {
        setTitle("Movie Recommendation System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());

        topPanel.add(new JLabel("國家:"));
        countryComboBox = new JComboBox<>();
        topPanel.add(countryComboBox);

        topPanel.add(new JLabel("電影類型:"));
        genreComboBox = new JComboBox<>();
        topPanel.add(genreComboBox);

        actorTextField = new JTextField(5);
        topPanel.add(new JLabel("Actor:"));
        topPanel.add(actorTextField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        topPanel.add(searchButton);

        topPanel.add(new JLabel("Enter your User's name: (necessary)"));
        userTextField = new JTextField(5);
        topPanel.add(userTextField);

        JButton userButton = new JButton("Login");
        userButton.addActionListener(e -> checkUser());
        topPanel.add(userButton);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());

        resultsTable = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("名稱");
        tableModel.addColumn("國家");
        tableModel.addColumn("電影類型");
        resultsTable.setModel(tableModel);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JButton viewButton = new JButton("View your movie list");
        viewButton.addActionListener(e -> performView());
        centerPanel.add(viewButton, BorderLayout.EAST);

        JButton deleteButton = new JButton("Delete your movie list");
        deleteButton.addActionListener(e -> performDelete());
        centerPanel.add(deleteButton, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        loadFilterOptions();
        setVisible(true);
    }

    private boolean hasTmdbApiKey() {
        if (TMDB_API_KEY == null || TMDB_API_KEY.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "找不到環境變數 TMDB_API_KEY。\n請先設定自己的 TMDB API Key 再重新啟動程式。",
                    "Missing TMDB API Key",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        return true;
    }

    private boolean hasMongoDbUri() {
        if (MONGODB_URI == null || MONGODB_URI.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "找不到環境變數 MONGODB_URI。\n請先設定自己的 MongoDB 連線字串再重新啟動程式。",
                    "Missing MongoDB URI",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        return true;
    }

    private void loadFilterOptions() {
        if (!hasTmdbApiKey()) {
            return;
        }

        try {
            Gson gson = new Gson();

            String countryApiUrl =
                    "https://api.themoviedb.org/3/configuration/countries?language=zh-TW&api_key="
                            + TMDB_API_KEY;
            JsonArray countries = gson.fromJson(request(countryApiUrl), JsonArray.class);

            DefaultComboBoxModel<String> countryModel = new DefaultComboBoxModel<>();
            for (JsonElement element : countries) {
                JsonObject country = element.getAsJsonObject();
                countryModel.addElement(country.get("english_name").getAsString());
            }
            countryComboBox.setModel(countryModel);

            String genreApiUrl =
                    "https://api.themoviedb.org/3/genre/movie/list?language=zh-TW&api_key="
                            + TMDB_API_KEY;
            JsonObject genreResponse = gson.fromJson(request(genreApiUrl), JsonObject.class);
            JsonArray genres = genreResponse.getAsJsonArray("genres");

            DefaultComboBoxModel<String> genreModel = new DefaultComboBoxModel<>();
            for (JsonElement element : genres) {
                JsonObject genre = element.getAsJsonObject();
                genreModel.addElement(genre.get("name").getAsString());
            }
            genreComboBox.setModel(genreModel);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "載入 TMDB 國家／類型資料失敗：" + e.getMessage(),
                    "TMDB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String request(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        } finally {
            connection.disconnect();
        }
    }

    private void checkUser() {
        String inputName = userTextField.getText().trim();
        if (inputName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "請輸入使用者名稱 !!");
            return;
        }

        userName = inputName;
        JOptionPane.showMessageDialog(this, "Welcome " + userName);
    }

    private String getUsername() {
        return userName;
    }

    private boolean hasUser() {
        if (userName == null || userName.isBlank()) {
            JOptionPane.showMessageDialog(this, "請先輸入使用者名稱並按 Login !!");
            return false;
        }
        return true;
    }

    private void performSearch() {
        if (!hasUser() || !hasTmdbApiKey()) {
            return;
        }

        String country = (String) countryComboBox.getSelectedItem();
        String genre = (String) genreComboBox.getSelectedItem();
        String actor = actorTextField.getText().trim();

        if (actor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "請輸入演員名稱 !!");
            return;
        }

        getActorMovies(country, genre, actor);
    }

    private void performDelete() {
        if (!hasUser() || !hasMongoDbUri()) {
            return;
        }

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGODB_URI))) {
            MongoDatabase database = mongoClient.getDatabase("mydb");
            MongoCollection<Document> collection = database.getCollection("customers");
            collection.deleteMany(new Document("User_name", getUsername()));
            JOptionPane.showMessageDialog(this, "已刪除 " + getUsername() + " 的電影片單");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "刪除片單失敗：" + e.getMessage(),
                    "MongoDB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void performView() {
        if (!hasUser() || !hasMongoDbUri()) {
            return;
        }

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGODB_URI))) {
            MongoDatabase database = mongoClient.getDatabase("mydb");
            MongoCollection<Document> collection = database.getCollection("customers");

            Document projection = new Document("MovieTitle", 1)
                    .append("Country", 1)
                    .append("Genre", 1)
                    .append("Overview", 1)
                    .append("User_name", 1)
                    .append("_id", 0);

            FindIterable<Document> documents = collection
                    .find(new Document("User_name", getUsername()))
                    .projection(projection);

            JFrame frame = new JFrame("Movie List");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            for (Document document : documents) {
                String username = document.getString("User_name");
                String movieTitle = document.getString("MovieTitle");
                String overview = document.getString("Overview");
                String country = document.getString("Country");
                String genre = document.getString("Genre");

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setText(
                        "使用者 : " + username + "\n"
                                + "MovieTitle: " + movieTitle
                                + " Country: " + country
                                + " Genre: " + genre
                );

                JButton button = new JButton("More Details");
                button.addActionListener(e -> showOverview(frame, overview));

                JPanel moviePanel = new JPanel(new BorderLayout());
                moviePanel.add(textArea, BorderLayout.CENTER);
                moviePanel.add(button, BorderLayout.EAST);
                panel.add(moviePanel);
            }

            frame.getContentPane().add(new JScrollPane(panel));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "讀取片單失敗：" + e.getMessage(),
                    "MongoDB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showOverview(JFrame parent, String overview) {
        JTextArea overviewTextArea = new JTextArea(overview == null ? "" : overview);
        overviewTextArea.setEditable(false);
        overviewTextArea.setLineWrap(true);
        overviewTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(overviewTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        JOptionPane.showMessageDialog(
                parent,
                scrollPane,
                "Movie Overview",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void getActorMovies(String country, String genre, String actorName) {
        String encodedActorName = URLEncoder.encode(actorName, StandardCharsets.UTF_8);

        try {
            String apiUrl =
                    "https://api.themoviedb.org/3/search/person?query="
                            + encodedActorName
                            + "&include_adult=false&language=zh-TW&page=1&api_key="
                            + TMDB_API_KEY;

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(request(apiUrl), JsonObject.class);
            JsonArray results = jsonObject.getAsJsonArray("results");

            if (results != null && results.size() > 0) {
                JsonObject actor = results.get(0).getAsJsonObject();
                int actorId = actor.get("id").getAsInt();
                getFilteredActorMoviesById(actorId, country, genre);
            } else {
                JOptionPane.showMessageDialog(this, "找不到演員：" + actorName);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "搜尋演員失敗：" + e.getMessage(),
                    "TMDB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void getFilteredActorMoviesById(int actorId, String country, String genre) {
        try {
            Gson gson = new Gson();

            String apiUrl =
                    "https://api.themoviedb.org/3/person/"
                            + actorId
                            + "/movie_credits?api_key="
                            + TMDB_API_KEY
                            + "&language=zh-TW";

            JsonObject jsonObject = gson.fromJson(request(apiUrl), JsonObject.class);
            JsonArray cast = jsonObject.getAsJsonArray("cast");

            String genreListUrl =
                    "https://api.themoviedb.org/3/genre/movie/list?language=zh-TW&api_key="
                            + TMDB_API_KEY;
            JsonObject genreListObject = gson.fromJson(request(genreListUrl), JsonObject.class);
            JsonArray genreArray = genreListObject.getAsJsonArray("genres");

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4;
                }
            };

            tableModel.addColumn("Title");
            tableModel.addColumn("Country");
            tableModel.addColumn("Genre");
            tableModel.addColumn("Overview");
            tableModel.addColumn("List");
            resultsTable.setModel(tableModel);

            new ButtonColumn(resultsTable, 4, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addSelectedMovieToDatabase();
                }
            });

            if (cast == null) {
                return;
            }

            for (JsonElement movie : cast) {
                JsonObject movieObject = movie.getAsJsonObject();
                String title = getString(movieObject, "title", "Unknown");
                String overview = getString(movieObject, "overview", "");
                JsonArray genreIdsArray = movieObject.getAsJsonArray("genre_ids");

                String movieCountry = getMovieCountry(movieObject);
                boolean countryMatched = country == null
                        || country.equals("All")
                        || country.equals(movieCountry);

                boolean genreMatched = genre == null || genre.equals("All");
                String matchedGenre = genre;

                if (!genreMatched && genreIdsArray != null) {
                    for (JsonElement genreIdElement : genreIdsArray) {
                        int genreId = genreIdElement.getAsInt();
                        String genreName = getGenreName(genreId, genreArray);
                        if (genreName != null && genreName.equals(genre)) {
                            genreMatched = true;
                            matchedGenre = genreName;
                            break;
                        }
                    }
                }

                if (countryMatched && genreMatched) {
                    tableModel.addRow(new Object[]{
                            title,
                            movieCountry,
                            matchedGenre,
                            overview,
                            "Add to database"
                    });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "取得電影資料失敗：" + e.getMessage(),
                    "TMDB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addSelectedMovieToDatabase() {
        if (!hasUser() || !hasMongoDbUri()) {
            return;
        }

        int row = resultsTable.convertRowIndexToModel(resultsTable.getEditingRow());
        if (row < 0) {
            return;
        }

        String movieTitle = String.valueOf(resultsTable.getModel().getValueAt(row, 0));
        String movieCountry = String.valueOf(resultsTable.getModel().getValueAt(row, 1));
        String movieGenre = String.valueOf(resultsTable.getModel().getValueAt(row, 2));
        String movieOverview = String.valueOf(resultsTable.getModel().getValueAt(row, 3));

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGODB_URI))) {
            MongoDatabase database = mongoClient.getDatabase("mydb");
            MongoCollection<Document> collection = database.getCollection("customers");

            Document document = new Document("MovieTitle", movieTitle)
                    .append("Country", movieCountry)
                    .append("Genre", movieGenre)
                    .append("Overview", movieOverview)
                    .append("User_name", userName);

            collection.insertOne(document);
            JOptionPane.showMessageDialog(this, "已加入片單：" + movieTitle);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "加入片單失敗：" + e.getMessage(),
                    "MongoDB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String getGenreName(int genreId, JsonArray genreArray) {
        if (genreArray == null) {
            return null;
        }

        for (JsonElement genreElement : genreArray) {
            JsonObject genreObject = genreElement.getAsJsonObject();
            if (genreObject.get("id").getAsInt() == genreId) {
                return genreObject.get("name").getAsString();
            }
        }
        return null;
    }

    private String getMovieCountry(JsonObject movieObject) {
        int movieId = movieObject.get("id").getAsInt();

        try {
            String apiUrl =
                    "https://api.themoviedb.org/3/movie/"
                            + movieId
                            + "?language=zh-TW&api_key="
                            + TMDB_API_KEY;

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(request(apiUrl), JsonObject.class);
            JsonArray productionCountries = jsonObject.getAsJsonArray("production_countries");

            if (productionCountries != null && productionCountries.size() > 0) {
                JsonObject countryObject = productionCountries.get(0).getAsJsonObject();
                return countryObject.get("name").getAsString();
            }
        } catch (IOException e) {
            System.err.println("取得電影國家失敗：" + e.getMessage());
        }

        return "Unknown";
    }

    private String getString(JsonObject object, String key, String defaultValue) {
        if (object.has(key) && !object.get(key).isJsonNull()) {
            return object.get(key).getAsString();
        }
        return defaultValue;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MovieRecommendationSystem::new);
    }
}
