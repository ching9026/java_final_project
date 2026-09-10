# Java 電影推薦與片單管理系統

這是我大學學習 Java 時完成的期末專題，主要目標是練習 **Java GUI、API 串接、JSON 資料處理、資料庫操作與 Maven 專案管理**。

專案以 **Java Swing** 製作桌面介面，透過 **TMDB API** 搜尋電影資訊，並使用 **MongoDB** 儲存不同使用者的個人片單。

> 此專案為大學時期的 Java 學習作品，保留當時的程式設計方式與實作內容，主要用於展示 Java 基礎、GUI、API 與資料庫整合能力。

---

## 專案功能

### 1. 使用者登入

使用者輸入名稱後登入系統，程式會以使用者名稱區分不同使用者的片單資料。

### 2. 電影搜尋

可依照以下條件搜尋電影：

- 國家 / 地區
- 電影類型（Genre）
- 演員名稱（Actor）

系統會透過 TMDB API 取得演員與電影資訊，並將搜尋結果顯示在 Java Swing 的表格中。

### 3. 電影資訊顯示

搜尋結果包含：

- 電影名稱
- 國家 / 地區
- 電影類型
- 電影簡介（Overview）

### 4. 加入個人片單

使用者可以將搜尋到的電影加入自己的片單。

系統會將以下資料寫入 MongoDB：

```text
User Name
Movie Title
Country
Genre
Overview
```

### 5. 查看片單

可查詢目前登入使用者已儲存的電影清單，並透過 GUI 查看每部電影的詳細介紹。

### 6. 刪除片單

可刪除目前使用者所建立的片單資料。

---

## 系統架構

```text
使用者
  │
  ▼
Java Swing GUI
  │
  ├── 使用者名稱
  ├── 國家篩選
  ├── 電影類型篩選
  └── 演員搜尋
  │
  ▼
TMDB API
  │
  ├── Actor Search
  ├── Movie Credits
  ├── Genre List
  └── Movie Details
  │
  ▼
Gson JSON Parsing
  │
  ▼
搜尋結果 JTable
  │
  └── Add to Database
          │
          ▼
      MongoDB Atlas
          │
          ├── 儲存片單
          ├── 查看片單
          └── 刪除片單
```

---

## 使用技術

| 類別 | 技術 |
|---|---|
| 程式語言 | Java |
| GUI | Java Swing / AWT |
| API | TMDB API |
| HTTP | `HttpURLConnection` |
| JSON | Gson |
| Database | MongoDB / MongoDB Atlas |
| Build Tool | Apache Maven |
| Java Version | Java 17 |
| Testing | JUnit 4 |

Maven 主要 dependency 包含：

- Gson 2.10.1
- MongoDB Java Driver 3.12.13
- BSON 3.12.13
- JUnit 4.11

---

## 專案結構

```text
java_final_project/
├── README.md
├── firstMaven-0.0.1-SNAPSHOT-jar-with-dependencies.jar
└── firstMaven/
    ├── pom.xml
    ├── src/
    │   ├── main/
    │   │   └── java/
    │   │       └── ocean_university/
    │   │           └── firstMaven/
    │   │               ├── App.java
    │   │               ├── ButtonColumn.java
    │   │               └── MovieRecommendationSystem.java
    │   └── test/
    └── target/
```

### 主要程式

#### `MovieRecommendationSystem.java`

整個系統的主要 GUI 與商業邏輯，包括：

- Swing 視窗與元件建立
- 使用者登入
- TMDB API 呼叫
- 電影搜尋與條件篩選
- JSON 解析
- JTable 顯示搜尋結果
- MongoDB 新增、查詢與刪除

#### `ButtonColumn.java`

讓 JTable 中的指定欄位可以顯示並操作按鈕，用來實作「Add to Database」功能。

#### `pom.xml`

負責 Maven dependency、Java 版本以及 JAR 打包設定。

---

## 執行環境

建議環境：

```text
Java 17
Maven 3.x
MongoDB Atlas / MongoDB
Internet connection
TMDB API Key
```

檢查 Java：

```bash
java -version
```

檢查 Maven：

```bash
mvn -version
```

---

## 建置方式

Clone 專案：

```bash
git clone https://github.com/ching9026/java_final_project.git
cd java_final_project/firstMaven
```

使用 Maven 建置：

```bash
mvn clean package
```

完成後 Maven 會在 `target/` 中產生 JAR。

---

## 執行方式

可以執行包含 dependencies 的 JAR：

```bash
java -Dfile.encoding=UTF-8 -jar target/firstMaven-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

也可以直接從 IDE 執行：

```text
ocean_university.firstMaven.MovieRecommendationSystem
```

---

## API 與資料庫設定

此專案需要兩個外部服務：

### TMDB API

需要申請 TMDB API Key：

https://www.themoviedb.org/

程式會使用 TMDB API 取得：

- Country Configuration
- Movie Genre List
- Actor Search
- Actor Movie Credits
- Movie Details

### MongoDB

使用 MongoDB 儲存使用者片單。

資料庫內容主要包含：

```text
User_name
MovieTitle
Country
Genre
Overview
```

---

## 安全性提醒

目前這份大學時期的原始程式碼中，仍可看到當時直接寫在程式裡的 API / Database connection 設定。

**公開 GitHub Repository 不應將 API Key、MongoDB 帳號密碼或 Connection String 直接寫在 Source Code 中。**

如果要重新執行或繼續維護此專案，建議：

1. 先更換（Rotate）舊的 TMDB API Key 與 MongoDB Atlas 密碼。
2. 將敏感設定改為 Environment Variables。
3. 不要將 `.env` 或實際 Credential 上傳到 GitHub。

例如未來可改為：

```java
String apiKey = System.getenv("TMDB_API_KEY");
String mongoUri = System.getenv("MONGODB_URI");
```

---

## 這個專案練習到的內容

這份專題是我大學學習 Java 時，將多個基礎概念整合成一個完整應用的練習，包含：

- Java OOP 與 Class 設計
- Java Swing GUI
- Event Listener / Event-Driven Programming
- HTTP API 串接
- URL Encoding
- JSON parsing
- JTable / TableModel
- MongoDB CRUD
- Maven Dependency Management
- JAR Packaging
- 第三方 Library 整合

相較於只練習單一 Java 語法題目，這個專題讓我第一次實際將 **GUI、外部 API 與 Database** 串在同一個 Java Application 中。

---

## 可以進一步改善的方向

如果重新整理這個專案，可以進一步改善：

- 將 TMDB API Key 與 MongoDB URI 移出原始碼
- 將 API、Database、GUI 邏輯拆成不同 Class
- 採用 MVC / Service Layer 架構
- 使用較新的 MongoDB Java Driver
- 使用 Java `HttpClient` 取代 `HttpURLConnection`
- 增加例外處理與錯誤提示
- 增加輸入資料驗證
- 將 MongoDB 查詢限制為目前使用者，而不是先讀取全部資料再篩選
- 增加片單單筆刪除功能
- 增加電影海報與評分顯示
- 增加 Unit Test / Integration Test
- 移除 Git 中的 `target/` build artifacts

---

## 專案定位

這是一個 **Java 學習階段的課程期末專題**，並不是目前仍持續維護的 production application。

保留此 Repository 的主要目的，是呈現從 Java 基礎語法逐步學習到：

```text
Java
  → GUI
  → API Integration
  → JSON Processing
  → Database
  → Maven Build
  → Executable JAR
```

的完整學習歷程。
