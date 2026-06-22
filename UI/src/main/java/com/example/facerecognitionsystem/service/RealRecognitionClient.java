package com.example.facerecognitionsystem.service;

import com.example.facerecognitionsystem.model.RecognitionResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class RealRecognitionClient implements RecognitionClient {

    private final String apiUrl = "http://localhost:8000/recognize";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Override
    public RecognitionResult recognize(BufferedImage frame) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(frame, "jpg", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("image", base64Image);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            String status = json.get("status").getAsString();
            String name = json.get("name").isJsonNull() ? "Unknown" : json.get("name").getAsString();
            double confidence = json.get("confidence").getAsDouble();

            return new RecognitionResult(status, name, confidence);

        } catch (IOException | InterruptedException e) {
            System.out.println("ERROR koneksi ke Python API: " + e.getMessage());
            return new RecognitionResult("unknown", "Unknown", 0.0);
        }
    }
}