package com.facerecog.impl;

import com.facerecog.model.RecognitionResult;
import com.facerecog.service.RecognitionClient;
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

public class RecognitionClientImpl implements RecognitionClient {

    private final boolean useMock;
    private final String apiUrl = "http://localhost:8000/recognize";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public RecognitionClientImpl(boolean useMock) {
        this.useMock = useMock;
    }

    @Override
    public RecognitionResult recognize(BufferedImage frame) {
        if (useMock) {
            return new RecognitionResult("recognized", "Nafis Test", 0.95);
        }
        return callPythonAPI(frame);
    }

    private RecognitionResult callPythonAPI(BufferedImage frame) {
        try {
            // Konversi frame ke Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(frame, "jpg", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

            // Buat JSON request
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("image", base64Image);

            // Kirim HTTP POST ke Python
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            // Parse response JSON
            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            String status = json.get("status").getAsString();
            String name = json.get("name").isJsonNull() ? "Unknown" : json.get("name").getAsString();
            double confidence = json.get("confidence").getAsDouble();

            return new RecognitionResult(status, name, confidence);

        } catch (IOException | InterruptedException e) {
            System.out.println("ERROR koneksi ke Python API: " + e.getMessage());
            return new RecognitionResult("error", "Unknown", 0.0);
        }
    }
}