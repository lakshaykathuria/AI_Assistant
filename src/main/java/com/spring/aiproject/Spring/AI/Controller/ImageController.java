package com.spring.aiproject.Spring.AI.Controller;

import com.spring.aiproject.Spring.AI.Service.ZhiPuAiImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
public class ImageController {

    private final ZhiPuAiImageService imageService;

    public ImageController(ZhiPuAiImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Endpoint to generate an image based on a text prompt.
     * * Usage: GET /generate-image?prompt=A%20cat%20wearing%20a%20tiny%20hat
     */
    @GetMapping("/generate-image")
    public ResponseEntity<byte[]> generateImage(@RequestParam(value = "prompt", defaultValue = "A cute robot waving") String prompt) {
        try {
            byte[] imageBytes = imageService.generateImage(prompt);

            HttpHeaders headers = new HttpHeaders();
            // Assuming the downloaded image is a JPEG, adjust if ZhipuAI returns PNG/other
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageBytes.length);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            System.err.println("Error generating or downloading image: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
