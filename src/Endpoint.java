import static spark.Spark.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.utils.IOUtils;
import utils.MapBuilder;

public class Endpoint {
	private static ObjectMapper jsonMapper = new ObjectMapper();
	private static Map<String, Object> _returnJsonObject(String jsonString) {
		try {
			return jsonMapper.readValue(jsonString, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private static String _returnJsonString(Object obj) {
		try {
			return jsonMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	private static String _returnJsonStringPlus(Object obj) {
		try {
			return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	public static void main(String[] args) {
		boolean developMode = false;
		staticFiles.location("/site");
		staticFileLocation("/site");
		System.setProperty("logfile.name", "logtest.log");
		port(34561);
		threadPool(12, 2, 30000);
		path("/user", () -> {
			staticFileLocation("/site/user");
		});
		path("/play", () -> {
			get("/info", (req, res) -> {
				String ua = req.userAgent();
				if (!ua.contains("UnityPlayer") && !ua.contains("Sonolus") && !developMode) {
					return "Sonolusアプリケーション以外でのアクセスは禁止となっています";
				}
				res.type("application/json;charset=UTF-8");
				String info = Files.readString(Paths.get("src/main/resources/info.json"));
				return info;
			});
			
			get("/levels/list", (req, res) -> {
				String ua = req.userAgent();
				if (!ua.contains("UnityPlayer") && !ua.contains("Sonolus") && !developMode) {
					return "Sonolusアプリケーション以外でのアクセスは禁止となっています";
				}
				res.type("application/json;charset=UTF-8");
				String info = Files.readString(Paths.get("src/main/resources/levels.json"));
				ObjectMapper jsonMapper = new ObjectMapper();
				List<String> levelList = jsonMapper.readValue(info, new TypeReference<ArrayList<String>>(){});
				List<Object> levels = new ArrayList<Object>();
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("pageCount", 1);
				for (String levelName: levelList) {
					Map<String, Object> levelOption = _returnJsonObject(Files.readString(Paths.get("src/main/resources/levelData/"+levelName+"/level.json")));
					HashMap<String, Object> levelData = new HashMap<String, Object>();
					levelData.put("name", levelName);
					levelData.put("version", 1);
					levelData.put("rating", levelOption.get("rating"));
					levelData.put("engine", _returnJsonObject(Files.readString(Paths.get("src/main/resources/levelData/engine.json"))));
					levelData.put("useSkin", new MapBuilder<String, Object>().put("useDefault", true).toMap());
					levelData.put("useBackground", new MapBuilder<String, Object>().put("useDefault", true).toMap());
					levelData.put("useEffect", new MapBuilder<String, Object>().put("useDefault", true).toMap());
					levelData.put("useParticle", new MapBuilder<String, Object>().put("useDefault", true).toMap());
					levelData.put("title", levelOption.get("title"));
					levelData.put("artists", levelOption.get("artists"));
					levelData.put("author", levelOption.get("author"));
					levelData.put("cover", new MapBuilder<String, Object>().put("type", "LevelCover").put("url", "https://pjsekai.tosirukun.tk/datas/levelData/"+levelName+"/cover.png").toConst());
					levelData.put("bgm", new MapBuilder<String, Object>().put("type", "LevelBgm").put("url", "https://pjsekai.tosirukun.tk/datas/levelData/"+levelName+"/bgm.png").toConst());
					levelData.put("data", new MapBuilder<String, Object>().put("type", "LevelData").put("url", "https://pjsekai.tosirukun.tk/datas/levelData/"+levelName+"/level.lvd").toConst());
					levelData.put("genre", levelOption.get("genre"));
					levelData.put("public", true);
					levelData.put("userId", levelOption.get("userId"));
					levelData.put("notes", 1);
					levelData.put("description", levelOption.get("description"));
					levels.add(levelData);
				}
				data.put("items", levels);
				return _returnJsonString(data);
			});
			
			get("/levels/:levelName", (req, res) -> {
				String ua = req.userAgent();
				if (!ua.contains("UnityPlayer") && !ua.contains("Sonolus") && !developMode) {
					return "Sonolusアプリケーション以外でのアクセスは禁止となっています";
				}
				res.type("application/json;charset=UTF-8");
				String levelName = req.params("levelName");
				Map<String, Object> levelOption = _returnJsonObject(Files.readString(Paths.get("src/main/resources/levelData/"+levelName+"/level.json")));
				HashMap<String, Object> levelData = new HashMap<String, Object>();
				levelData.put("name", levelName);
				levelData.put("version", 1);
				levelData.put("rating", levelOption.get("rating"));
				levelData.put("engine", _returnJsonObject(Files.readString(Paths.get("src/main/resources/levelData/engine.json"))));
				levelData.put("useSkin", new MapBuilder<String, Object>().put("useDefault", true).toMap());
				levelData.put("useBackground", new MapBuilder<String, Object>().put("useDefault", true).toMap());
				levelData.put("useEffect", new MapBuilder<String, Object>().put("useDefault", true).toMap());
				levelData.put("useParticle", new MapBuilder<String, Object>().put("useDefault", true).toMap());
				levelData.put("title", levelOption.get("title"));
				levelData.put("artists", levelOption.get("artists"));
				levelData.put("author", levelOption.get("author"));
				levelData.put("cover", new MapBuilder<String, Object>().put("type", "LevelCover").put("url", "https://pjsekai.tosirukun.tk/datas/levelData/"+levelName+"/cover.png").toConst());
				levelData.put("bgm", new MapBuilder<String, Object>().put("type", "LevelBgm").put("url", "https://pjsekai.tosirukun.tk/datas/levelData/"+levelName+"/bgm.mp3").toConst());
				levelData.put("data", new MapBuilder<String, Object>().put("type", "LevelData").put("url", "https://pjsekai.tosirukun.tk/datas/levelData/"+levelName+"/level.lvd").toConst());
				levelData.put("genre", levelOption.get("genre"));
				levelData.put("public", true);
				levelData.put("userId", levelOption.get("userId"));
				levelData.put("notes", 1);
				levelData.put("description", levelOption.get("description"));
				return _returnJsonString(new MapBuilder<String, Object>().put("item", levelData).toConst());
			});
		});
		
		path("/api", () -> {
			get("/get-levels/:token", (req, res) -> {
				res.type("application/json;charset=UTF-8");
				String userToken = req.params("token");
				String info = Files.readString(Paths.get("src/main/resources/all-levels.json"));
				ObjectMapper jsonMapper = new ObjectMapper();
				List<String> levelList = jsonMapper.readValue(info, new TypeReference<ArrayList<String>>(){});
				ArrayList<Object> levels = new ArrayList<Object>();
				for (String levelName: levelList) {
					Map<String, Object> levelOption = _returnJsonObject(Files.readString(Paths.get("src/main/resources/levelData/"+levelName+"/level.json")));
					HashMap<String, Object> levelData = new HashMap<String, Object>();
					if (levelOption.get("authorToken").equals(userToken)) {
						levelData.put("name", levelName);
						levelData.put("title", levelOption.get("title"));
						levelData.put("rating", levelOption.get("rating"));
						levelData.put("artists", levelOption.get("artists"));
						levelData.put("author", levelOption.get("author"));
						levelData.put("cover", "https://pjsekai.tosirukun.tk/datas/levelData/"+levelName+"/cover.png");
						levelData.put("userId", levelOption.get("userId"));
						levelData.put("notes", 1);
						levelData.put("description", levelOption.get("description"));
						levels.add(levelData);
					}
				}
				return _returnJsonString(new MapBuilder<String, Object>().put("items", levels).toConst());
			});
			post("/new-level", (req, res) -> {
				res.type("application/json;charset=UTF-8");
				req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/var/websites/pjsekai-sonolus/datas/tmp"));
				String levelName = req.queryParams("levelName");
				String levelDescription = req.queryParams("levelDescription");
				String levelAuthor = req.queryParams("levelAuthor");
				String levelAuthorToken = req.queryParams("levelAuthorToken");
				String levelGenre = "vocaloid";
				String levelRating = req.queryParams("levelRating");
				String levelArtists = req.queryParams("levelArtists");
				String levelUserID = "ThisisUserID";
				String levelID = UUID.randomUUID().toString();
				System.out.println(levelRating);
				System.out.println(levelName);
				Map<String, Object> levelJson = new MapBuilder<String, Object>()
						.put("title", levelName)
						.put("description", levelDescription)
						.put("author", levelAuthor)
						.put("genre", levelGenre)
						.put("rating", Integer.parseInt(levelRating))
						.put("artists", levelArtists)
						.put("userId", levelUserID)
						.put("authorToken", levelAuthorToken)
						.toConst();
				String levelJsonString = _returnJsonString(levelJson);
				Files.createDirectory(Paths.get("src/main/resources/levelData/" + levelID));
				Files.createFile(Paths.get("src/main/resources/levelData/" + levelID + "/level.json"));
				Files.writeString(Paths.get("src/main/resources/levelData/" + levelID + "/level.json"), levelJsonString, StandardOpenOption.TRUNCATE_EXISTING);
				Files.createDirectory(Paths.get("/var/websites/pjsekai-sonolus/datas/levelData/" + levelID));
				Files.createFile(Paths.get("/var/websites/pjsekai-sonolus/datas/levelData/" + levelID + "/level.lvd"));
				Files.createFile(Paths.get("/var/websites/pjsekai-sonolus/datas/levelData/" + levelID + "/bgm.mp3"));
				Files.createFile(Paths.get("/var/websites/pjsekai-sonolus/datas/levelData/" + levelID + "/cover.png"));
				Part levelFPart = req.raw().getPart("levelJson");
				Part bgmFPart = req.raw().getPart("levelBGM");
				Part coverFPart = req.raw().getPart("levelCover");
				InputStream input = levelFPart.getInputStream();
					InputStream inputb = bgmFPart.getInputStream();
					InputStream inputc = coverFPart.getInputStream();
					BufferedImage imginput = ImageIO.read(inputc);
					OutputStream output = new GZIPOutputStream(new FileOutputStream("/var/websites/pjsekai-sonolus/datas/levelData/" + levelID + "/level.lvd"));
					OutputStream outputb = new FileOutputStream("/var/websites/pjsekai-sonolus/datas/levelData/" + levelID + "/bgm.mp3");
					output.write(input.readAllBytes());
					IOUtils.copy(inputb, outputb);
					File outputc = new File("/var/websites/pjsekai-sonolus/datas/levelData/" + levelID + "/cover.png");
					ImageIO.write(imginput, "png", outputc);
					output.close();
					outputb.close();
					String info = Files.readString(Paths.get("src/main/resources/levels.json"));
					ObjectMapper jsonMapper = new ObjectMapper();
					List<String> levelList = jsonMapper.readValue(info, new TypeReference<ArrayList<String>>(){});
					levelList.add(levelID);
					Files.writeString(Paths.get("src/main/resources/levels.json"), _returnJsonString(levelList));
					res.redirect("http://technical.tosirukun.tk/index.html");
				return _returnJsonString(new MapBuilder<String, Object>().put("status","allow").toConst());
			});
		});
	}
}