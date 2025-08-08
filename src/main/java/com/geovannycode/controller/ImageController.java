package com.geovannycode.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi.OpenAiImageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    private final OpenAiImageModel imageModel;

    @GetMapping
    public String generateImage(@RequestParam String prompt) {

        OpenAiImageOptions options = new OpenAiImageOptions();
            options.setSize("1024x1024");
            options.setQuality("standard");
       
        // La llamada correcta es con ImagePrompt
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageModel.call(imagePrompt);

        // Toma el primer resultado (getResult() es un helper del primero)
        var result = response.getResult();
        if (result == null || result.getOutput() == null) {
            log.warn("No se recibió salida de imagen para el prompt");
            return "No se pudo generar la imagen.";
        }

        // Si el modelo retornó URL (por defecto), úsala
        if (result.getOutput().getUrl() != null) {
            return result.getOutput().getUrl();
        }

        // Si retornó base64 (b64_json), podrías devolver un data URI
        if (result.getOutput().getB64Json() != null) {
            return "data:image/png;base64," + result.getOutput().getB64Json();
        }

        return "No se pudo generar la imagen.";
    }
}