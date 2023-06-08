package ocean_university.firstMaven;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

//mongodb
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.MongoClientURI;
import org.bson.Document;

public class MovieRecommendationSystem extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private  String userName;
    private JComboBox<String> countryComboBox;
    private JComboBox<String> genreComboBox;
    private JButton searchButton;
    private JButton userButton;

    private JTable resultsTable;
    
    private JTextField userTextField ;
    private JTextField actorTextField;
    private JPanel centerPanel;
    private static MongoClient mongoClient;
    private static MongoClientURI uri;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    
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


        String apiKey = "8b8b3232fbf167821df5e4593434bca3";
        String countryApiUrl = "https://api.themoviedb.org/3/configuration/countries?language=zh-TW&api_key=" + apiKey;
        String genreApiUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey;
        try {

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


            Gson gson = new Gson();
            JsonArray countryJsonArray = gson.fromJson(countryContent.toString(), JsonArray.class);

            DefaultComboBoxModel<String> countryModel = new DefaultComboBoxModel<>();

            for (JsonElement element : countryJsonArray) {
                JsonObject country = element.getAsJsonObject();
                String englishName = country.get("english_name").getAsString();
                countryModel.addElement(englishName);
            }

            countryComboBox = new JComboBox<>(countryModel);
            topPanel.add(countryComboBox);

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


            JsonObject genreJsonObject = gson.fromJson(genreContent.toString(), JsonObject.class);
            JsonArray genreJsonArray = genreJsonObject.getAsJsonArray("genres");

            DefaultComboBoxModel<String> genreModel = new DefaultComboBoxModel<>();


            for (JsonElement element : genreJsonArray) {
                JsonObject genre = element.getAsJsonObject();
                String name = genre.get("name").getAsString();
                genreModel.addElement(name);
            }

            genreComboBox = new JComboBox<>(genreModel);
            topPanel.add(new JLabel("電影類型:"));
            System.out.print("987");
            topPanel.add(genreComboBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
        actorTextField = new JTextField(5);
        topPanel.add(new JLabel("Actor:"));
        topPanel.add(actorTextField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        topPanel.add(searchButton);
        
        topPanel.add(new JLabel("Enter your User's name: (neccessary)"));
        userTextField = new JTextField(5);
        topPanel.add(userTextField);
        
        userButton = new JButton("Login");
        userButton.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {
                checkUser();
            }
        });
        
        topPanel.add(userButton);

        add(topPanel, BorderLayout.NORTH);

         
        centerPanel = new JPanel(new BorderLayout());
        
        //to do
        JButton viewButton = new JButton("View your movie list");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performView();
            }
        });
        
        JButton deleteButton = new JButton("Delete your movie list");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDelete();
            }
        });
        
        resultsTable = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("名稱");
        tableModel.addColumn("國家");
        tableModel.addColumn("電影類型");
        resultsTable.setModel(tableModel);
        
//
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(viewButton, BorderLayout.EAST);
        centerPanel.add(deleteButton, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        
       
        
        

        // show the frame
        setVisible(true);
    }

    private void checkUser() { //以名稱區別使用者，避免片單重複，或刪除到別人的片單
    	if(userTextField.getText().trim().equals("")) {
    		JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "請輸入使用者名稱 !!");
            return;
    	}
    	else {
    		userName = userTextField.getText();
    		JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "Welcome " + userName);
    	}
    };
    
    private String getUsername() {
    	return userName;
    }


	// 執行搜尋
    private void performSearch() {
    	
    	if(userTextField.getText().trim().equals("")) {
    		JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "請輸入使用者名稱 !!");
            return;
    	}
        String country = (String) countryComboBox.getSelectedItem();
        String genre = (String) genreComboBox.getSelectedItem();
        String actor =  actorTextField.getText(); // 目前忽略 actor 欄位

        getActorMovies1(country, genre,actor);

    }

    // 執行搜尋
    private void performDelete() {//mongodb+srv://weichunnien:911009ss@movie.ubzgdac.mongodb.net/ (連接雲端Atlas字串)
    	//mongodb://localhost:27017
    	if(userTextField.getText().trim().equals("")) {
    		JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "請輸入使用者名稱 !!");
            return;
    	}
    	uri = new MongoClientURI("mongodb+srv://weichunnien:911009ss@movie.ubzgdac.mongodb.net/");
        mongoClient = new MongoClient(uri);
        
        
        database = mongoClient.getDatabase("mydb");

        collection = database.getCollection("customers");

        //collection.deleteMany(new Document()); 刪除整個集合
        collection.deleteMany(new Document("User_name", getUsername())); //刪除特定使用者資料
    	
        //collection.countDocuments();
        
        System.out.println("Delete Complete ");
    	mongoClient.close();
    	

    }
    
    
    private void performView() {//mongodb+srv://weichunnien:911009ss@movie.ubzgdac.mongodb.net/
    	if(userTextField.getText().trim().equals("")) {
    		JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "請輸入使用者名稱 !!");
            return;
    	}
        uri = new MongoClientURI("mongodb+srv://weichunnien:911009ss@movie.ubzgdac.mongodb.net/");
        mongoClient = new MongoClient(uri);

        database = mongoClient.getDatabase("mydb");

        collection = database.getCollection("customers");

        // Define the projection
        Document projection = new Document("MovieTitle", 1)
                .append("Country", 1)
                .append("Genre", 1)
                .append("Overview", 1)
                .append("User_name",1)
                .append("_id", 0);


        // Find documents with projection
        FindIterable<Document> documents = collection.find().projection(projection);

        JFrame frame = new JFrame("Movie List");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a panel to hold the movie list and buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Build the movie list and buttons
        for (Document document : documents) {
        	if(userName.equals(document.getString("User_name"))) {
	            // Extract movie title and overview
        		String username = document.getString("User_name");
	            String movieTitle = document.getString("MovieTitle");
	            String overview = document.getString("Overview");
	            String country=document.getString("Country");
	            String genre=document.getString("Genre");
	            //String username=document.getString("");
	            JTextArea textArea = new JTextArea();
	            textArea.setEditable(false);
	            
	            textArea.setText("使用者 : " + username + "\n"+ "MovieTitle:"+movieTitle+" Country: "+country+" Genre: "+genre);
	            // Create a button for each movie
	            JButton button = new JButton("More Details");
	
	            // Create a button for each movie
	            button.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    // Create a JTextArea to display the overview
	                    JTextArea overviewTextArea = new JTextArea(overview);
	                    overviewTextArea.setEditable(false);
	                    overviewTextArea.setLineWrap(true);
	                    overviewTextArea.setWrapStyleWord(true);
	
	                    // Create a JScrollPane to contain the JTextArea
	                    JScrollPane scrollPane = new JScrollPane(overviewTextArea);
	                    scrollPane.setPreferredSize(new Dimension(400, 200));
	
	                    // Create a JOptionPane with the scroll pane as its message
	                    JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.PLAIN_MESSAGE);
	                    JDialog dialog = optionPane.createDialog(frame, "Movie Overview");
	
	                    // Set the size of the dialog
	                    int dialogWidth = 400;
	                    int dialogHeight = 200;
	                    dialog.setSize(dialogWidth, dialogHeight);
	
	                    // Show the dialog
	                    dialog.setVisible(true);
	                }
	            });
	
	
	            // Create a panel to hold the movie and button
	            JPanel moviePanel = new JPanel();
	            moviePanel.setLayout(new BorderLayout());
	            moviePanel.add(textArea, BorderLayout.CENTER);
	            moviePanel.add(button, BorderLayout.EAST);
	            panel.add(moviePanel);
        	}
        }

        // Add the panel to the frame
        frame.getContentPane().add(new JScrollPane(panel));

        // Pack the frame
        frame.pack();

        // Set the frame location to the center of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int centerY = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(centerX, centerY);

        // Display the frame
        frame.setVisible(true);

        mongoClient.close();
    }


    private void getActorMovies1(String country, String genre, String actorName) {
        // URL編碼格式，API請求中使用
        String encodedActorName = URLEncoder.encode(actorName, StandardCharsets.UTF_8);

        // 构建API请求URL
        String apiKey = "8b8b3232fbf167821df5e4593434bca3";

        try {
            // API請求
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

            // 解析JSON數據
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);
            JsonArray results = jsonObject.getAsJsonArray("results");

           
            if (results.size() > 0) {
                JsonObject actor = results.get(0).getAsJsonObject();
                int actorId = actor.get("id").getAsInt();
                getFilteredActorMoviesById(actorId, country, genre); //拿取演員id
            } else {
            	JFrame jFrame = new JFrame();
                JOptionPane.showMessageDialog(jFrame, "No actor found with the name: " + actorName);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFilteredActorMoviesById(int actorId, String country, String genre) {
        String apiKey = "8b8b3232fbf167821df5e4593434bca3";

        try {
            String apiUrl = "https://api.themoviedb.org/3/person/" + actorId + "/movie_credits?api_key=" + apiKey + "&language=zh-TW";
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

            // Create table model
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return true; // Make the table cells editable
                }
            };
            resultsTable.setModel(tableModel);
            tableModel.addColumn("Title");
            tableModel.addColumn("Country");
            tableModel.addColumn("Genre");
            tableModel.addColumn("Overview");
            tableModel.addColumn("List");

            ButtonColumn buttonColumn = new ButtonColumn(resultsTable, 4, new ActionListener() { // 修改 ButtonColumn 的索引
                public void actionPerformed(ActionEvent e) {
                    // Action to be performed when the button is clicked
                    int row = resultsTable.convertRowIndexToModel(resultsTable.getEditingRow());
                    String movieTitle = (String) resultsTable.getModel().getValueAt(row, 0); // 電影title
                    String movieCountry = (String) resultsTable.getModel().getValueAt(row, 1); // country
                    String movieGenre = (String) resultsTable.getModel().getValueAt(row, 2); // genre
                    String movieOverview = (String) resultsTable.getModel().getValueAt(row, 3); // overview

                    JFrame jFrame = new JFrame();
                    JOptionPane.showMessageDialog(jFrame, "Add to Database clicked for movie: " + movieTitle);

                    // Connect to the MongoDB server mongodb+srv://weichunnien:911009ss@movie.ubzgdac.mongodb.net/
                    uri = new MongoClientURI("mongodb+srv://weichunnien:911009ss@movie.ubzgdac.mongodb.net/");
                    mongoClient = new MongoClient(uri);

                    // Connect to the database
                    database = mongoClient.getDatabase("mydb");
                    System.out.println("Connected to the database successfully");

                    

                    collection = database.getCollection("customers");//集合
                    System.out.println("Get to the collection successfully");

                    Document document = new Document("MovieTitle", movieTitle) // movie title , country , genre, overview.
                            .append("Country", movieCountry)
                            .append("Genre", movieGenre)
                            .append("Overview", movieOverview)
                    		.append("User_name", userName);

                    collection.insertOne(document);
                    System.out.println("Insert successfully");
                    mongoClient.close();
                }
            });

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
                String overview = movieObject.get("overview").getAsString(); // 獲取 "overview" 的資料

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
                    tableModel.addRow(new Object[]{title, country, genre, overview,"Add to database"});
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
    	
    	
        

        new MovieRecommendationSystem();
        
        
        
    }
}
//java -Dfile.encoding=UTF-8 -jar firstMaven-0.0.1-SNAPSHOT-jar-with-dependencies.jar
//https://israynotarray.com/nodejs/20220405/475293919/