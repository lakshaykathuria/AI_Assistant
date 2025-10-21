package com.spring.aiproject.Spring.AI.Service;

import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel; // Corrected import (assuming this package works)
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions; // Corrected import (assuming this package works)
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;

/**
 * Service class for ZhipuAI image generation.
 * This version is minimized to only include working API calls based on troubleshooting
 * an older Spring AI snapshot. All portable options (N, width, height) MUST be set
 * in the application.properties file.
 */
@Service
public class ZhiPuAiImageService {

    private final ZhiPuAiImageModel imageModel;
    private final RestTemplate restTemplate;

    public ZhiPuAiImageService(ZhiPuAiImageModel imageModel) {
        this.imageModel = imageModel;
        this.restTemplate = new RestTemplate(); // For downloading the image
    }

    /**
     * Generates an image using the ZhipuAI model.
     * @param prompt The text prompt to generate the image from.
     * @return The generated image content as a byte array.
     * @throws IOException if image generation or download fails.
     */
    public byte[] generateImage(String prompt) throws IOException {

        // 1. Define the ZhipuAI-specific options
        // We set the 'model' here, as this is the only provider-specific option that works via the builder.
        ZhiPuAiImageOptions zhipuAiOptions = ZhiPuAiImageOptions.builder()
                .model("cogview-3")
                .build();

        // 2. Create the ImagePrompt.
        // We rely on Spring AI's auto-configuration to merge the options set in
        // application.properties (N, width, height) with these provider-specific options.
        ImagePrompt promptWithOptions = new ImagePrompt(prompt, zhipuAiOptions);

        // 3. Call the synchronous ImageModel.
        ImageResponse response = imageModel.call(promptWithOptions);

        // 4. Extract the image URL from the first result (type confirmed as 'Image')
        Image outputImage = response.getResult().getOutput();
        String imageUrl = outputImage.getUrl();

        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IOException("Image generation failed or returned no URL.");
        }

        // 5. Download the image data from the URL to match the 'byte[]' return type
        try {
            System.out.println("Generated image URL: " + imageUrl);
            return restTemplate.getForObject(imageUrl, byte[].class);
        } catch (Exception e) {
            throw new IOException("Failed to download image from URL: " + imageUrl, e);
        }
    }
}
