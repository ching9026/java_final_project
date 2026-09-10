# Java 電影推薦與個人片單管理系統

> 大學 Java 程式設計課程期末專題

這是我大學時期學習 Java 所完成的期末專題，使用 **Java Swing** 建立桌面 GUI，串接 **TMDB (The Movie Database) API** 搜尋電影資訊，並透過 **MongoDB** 儲存不同使用者的個人電影片單。

這個專案主要用來練習 Java GUI、物件導向程式設計、HTTP API 串接、JSON 資料處理、MongoDB 資料庫操作，以及 Maven 專案建置。

---

## 專案功能

### 電影搜尋

使用者可以輸入演員名稱，程式會透過 TMDB API 搜尋該演員參與的電影。

搜尋結果可依照：

- 國家（Country）
- 電影類型（Genre）
- 演員（Actor）

進行篩選。

電影結果會顯示於 Java Swing 的 `JTable` 中，包括電影名稱、國家、類型與電影簡介。

### 個人電影片單

使用者輸入名稱並登入後，可以：

- 將搜尋到的電影加入個人片單
- 查看自己的電影片單
- 查看電影詳細介紹（Overview）
- 刪除自己的電影片單

不同使用者的資料透過 `User_name` 欄位區分。

### MongoDB 儲存

加入片單的電影資料會儲存在 MongoDB，包括：

```text
MovieTitle
Country
Genre
Overview
User_name
```

---

## 系統架構

```text
使用者
  │
  ▼
Java Swing GUI
  │
  ├───────────────┐
  ▼               ▼
TMDB API        MongoDB
  │               │
  ▼               ▼
電影資料        個人電影片單
  │               │
  └───────┬───────┘
          ▼
       Swing GUI
```

---

## 使用技術

| 類別 | 技術 |
|---|---|
| 程式語言 | Java 17 |
| GUI | Java Swing |
| Build Tool | Maven |
| 電影資料 | TMDB API |
| HTTP | `HttpURLConnection` |
| JSON | Gson 2.10.1 |
| Database | MongoDB / MongoDB Atlas |
| MongoDB Driver | mongo-java-driver 3.12.13 |
| Testing | JUnit 4 |

---

## 專案結構

```text
java_final_project/
├── README.md
├── .gitignore
└── firstMaven/
    ├── pom.xml
    └── src/
        ├── main/java/ocean_university/firstMaven/
        │   ├── App.java
        │   ├── ButtonColumn.java
        │   └── MovieRecommendationSystem.java
        └── test/java/ocean_university/firstMaven/
            └── AppTest.java
```

Maven 產生的 `target/`、`.class` 與 executable JAR 屬於 build artifacts，因此不再提交到 Git repository，可使用 Maven 重新產生。

---

## 主要程式

### `MovieRecommendationSystem.java`

專案核心程式，負責：

- 建立 Swing GUI
- 使用者操作
- TMDB API 呼叫
- Actor / Country / Genre 搜尋與篩選
- MongoDB 連線
- 電影片單新增、讀取與刪除

### `ButtonColumn.java`

將按鈕加入 `JTable` 欄位，讓使用者可以直接把搜尋結果加入 MongoDB 電影片單。

### `pom.xml`

管理 Maven dependencies 與 build 設定，並設定：

```text
ocean_university.firstMaven.MovieRecommendationSystem
```

為 executable JAR 的 Main Class。

---

## 執行需求

建議環境：

```text
Java 17+
Maven 3.x
MongoDB / MongoDB Atlas
TMDB API Key
```

Clone Repository：

```bash
git clone https://github.com/ching9026/java_final_project.git
cd java_final_project/firstMaven
```

---

## API Key 與資料庫設定

為避免將帳號、密碼或 API Key 提交到公開 GitHub，程式現在改為從 **環境變數**讀取設定。

需要設定兩個環境變數：

```text
TMDB_API_KEY
MONGODB_URI
```

其中：

- `TMDB_API_KEY`：你自己的 TMDB API Key
- `MONGODB_URI`：你自己的 MongoDB / MongoDB Atlas Connection String

### Windows PowerShell

```powershell
$env:TMDB_API_KEY="YOUR_TMDB_API_KEY"
$env:MONGODB_URI="YOUR_MONGODB_CONNECTION_STRING"
```

### Linux / macOS

```bash
export TMDB_API_KEY="YOUR_TMDB_API_KEY"
export MONGODB_URI="YOUR_MONGODB_CONNECTION_STRING"
```

> 不要把真正的 API Key、MongoDB 帳號或密碼 commit 到 GitHub。

---

## Maven Build

進入 Maven 專案：

```bash
cd firstMaven
```

安裝 dependency 並編譯：

```bash
mvn clean package
```

完成後 Maven 會重新建立：

```text
target/
```

並產生 executable JAR。

---

## 執行方式

完成環境變數設定與 Maven Build 後，可執行：

```bash
java -Dfile.encoding=UTF-8 -jar target/firstMaven-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

也可以直接從 IntelliJ IDEA / Eclipse 執行：

```text
MovieRecommendationSystem.main()
```

---

## 操作流程

```text
啟動程式
    │
    ▼
輸入 User Name
    │
    ▼
Login
    │
    ▼
選擇 Country / Genre
    │
    ▼
輸入 Actor
    │
    ▼
Search
    │
    ▼
TMDB API
    │
    ▼
顯示電影搜尋結果
    │
    ├── 查看電影資訊
    │
    └── Add to database
             │
             ▼
          MongoDB
             │
             ▼
        個人電影片單
```

---

## 學習內容

這個專案是我在大學學習 Java 時完成的課程專題，主要練習：

- Java 基本語法
- Object-Oriented Programming (OOP)
- Class 與 Method 設計
- Event-driven Programming
- Java Swing GUI
- JTable / JButton / JPanel / JFrame
- HTTP Request
- REST API 串接
- JSON Parsing
- Maven Dependency Management
- MongoDB CRUD
- 外部 API 與資料庫整合

---

## 安全性整理

早期課程版本曾直接將外部服務設定寫在程式碼中。現在 Repository 版本已改成透過環境變數取得：

```java
System.getenv("TMDB_API_KEY")
System.getenv("MONGODB_URI")
```

同時移除舊的編譯產物與含舊設定的測試 prototype。

如果憑證曾經提交到公開 GitHub，即使最新版程式已經移除，**舊 Git commit history 仍可能保留原值**，因此應該到對應服務重新產生新的 API Key / Database Password，而不是繼續使用舊憑證。

---

## 未來可改善方向

如果重新開發這個專案，可以進一步改善：

- 使用 JavaFX 改善 UI
- 將 TMDB API 邏輯抽成 Service Class
- 將 MongoDB 操作抽成 Repository / DAO
- 使用 DTO / Model Class 管理電影資料
- 增加真正的 Authentication
- 改善 Exception Handling
- 使用非同步 API Request 避免 Swing UI Blocking
- 加入 Unit Test
- 加入電影海報圖片
- 支援片單單筆刪除
- 支援收藏、評分與推薦功能

---

## 專案定位

這是我大學 Java 課程的期末學習專題，重點並不是建立 production-ready 的電影推薦服務，而是透過一個完整的小型應用程式，實際練習：

> **Java GUI + REST API + JSON + MongoDB + Maven**

並理解桌面應用程式如何與外部 Web API 以及 Database 整合。

---

## 外部服務

本專案使用：

- [TMDB (The Movie Database)](https://www.themoviedb.org/)
- [MongoDB](https://www.mongodb.com/)

電影相關資料由 TMDB API 提供。

