package com.github.Ramble21.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GeometryDashLevel {

    String jsonResponse;

    String name;
    int id;
    String author;
    String difficulty;

    int attempts;

    public GeometryDashLevel(int levelId){
        jsonResponse = getApiResponse(levelId);
        parseJson();
        System.out.println(this);
    }

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public String getAuthor() {
        return author;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public int getAttempts(){
        return attempts;
    }
    public String toString(){
        return "{\n \"name\": \"" + name + "\",\n \"id\": \"" + id + "\",\n \"author\": \"" + author + "\",\n \"difficulty\": \"" + difficulty + "\"\n}";
    }


    public String getApiResponse(int levelId){
        try{
            String apiUrl = "https://gdbrowser.com/api/level/" + levelId;
            URL url = new URL(apiUrl);
            System.out.println("API URL: " + apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            System.out.println("Response Code: " + connection.getResponseCode());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
            else {
                System.out.println("GET request failed. Response code: " + responseCode);
                return "Error";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void parseJson(){
        Gson gson = new Gson();
        GeometryDashLevel data = gson.fromJson(jsonResponse, GeometryDashLevel.class);
        this.name = data.name;
        this.author = data.author;
        this.difficulty = data.difficulty;
        this.id = data.id;
    }

    public void writeToPersonalJson(User user){
        try {
            for (String pathStr : new String[]{
                    "data",
                    "data/json",
                    "data/json/completions"
            }) {
                Path path = Paths.get(pathStr);
                if (!Files.exists(path)) Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String userId = user.getId();

        List<GeometryDashLevel> geometryDashLevels;

        try (FileReader reader = new FileReader("data/json/completions/" + user.getId() + ".json")) {
            Type listType = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
            geometryDashLevels = gson.fromJson(reader, listType);

            if (geometryDashLevels == null) {
                geometryDashLevels = new ArrayList<>();
            }

        } catch (IOException e) {
            geometryDashLevels = new ArrayList<>();
        }

        geometryDashLevels.add(this);

        try (FileWriter writer = new FileWriter("data/json/personalvocab/" + user.getId() + ".json")){
            gson.toJson(geometryDashLevels,writer);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<GeometryDashLevel> getPersonalJsonList(User user){
        Gson gson = new Gson();
        String personalJson = "data/json/completions/" + user.getId() + ".json";
        Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();

        try (FileReader reader = new FileReader(personalJson)) {
            return gson.fromJson(reader, type);
        }
        catch (IOException e) {
            return null;
        }
    }
}
