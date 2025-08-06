package com.geovannycode.controller;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagController {

    private final VectorStore vectorStore;
    private final ResourceLoader resourceLoader;
    private final OpenAiChatModel openAiChatModel;

    private boolean initialized = false;

    private static final Logger logger = LoggerFactory.getLogger(RagController.class);

    @GetMapping
    public String askData(@RequestParam String question) {
        // Inicializar la base de datos vectorial si aún no se ha hecho
        if (!initialized) {
            initializeVectorStore();
            initialized = true;
        }

        // Buscar información relevante para la pregunta
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(1) // Obtener el fragmento más similar
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);

        // Construir contexto con los resultados
        String context = results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // Crear prompt con contexto y pregunta
        String prompt = String.format("""
                Usa la siguiente información para responder:
                %s

                %s
                """, context, question);

        // Obtener respuesta de OpenAI
        return openAiChatModel.call(prompt);
    }

    private void initializeVectorStore() {

        try {
            // Cargar recurso
            Resource resource = resourceLoader.getResource("classpath:data.txt");

            // Crear un lector de texto adecuado
            TextReader textReader = new TextReader(resource);

            // Configurar un splitter adecuado (con parámetros correctos según la API)
            TokenTextSplitter splitter = new TokenTextSplitter();

            // Leer los documentos
            List<Document> documents = textReader.get();

            // Aplicar el splitter para fragmentar adecuadamente
            documents = splitter.apply(documents);

            // Registrar metadatos adicionales si es necesario
            documents.forEach(document -> {
                // Añadir metadatos adicionales según sea necesario
                document.getMetadata().put("source", "data.txt");
            });

            // Verificar que tenemos documentos para procesar
            if (documents.isEmpty()) {
                System.out.println("Advertencia: No se encontraron documentos para vectorizar");
                return;
            }

            // Almacenar documentos en la base vectorial con manejo de errores adecuado
            try {
                vectorStore.add(documents);
                System.out.println("Base vectorial inicializada correctamente con " + documents.size() + " documentos");
            } catch (Exception e) {
                System.err.println("Error al almacenar documentos en la base vectorial: " + e.getMessage());
                throw e; // Relanzar para manejo adecuado en nivel superior
            }

        } catch (Exception e) {
            System.err.println("Error en el proceso de inicialización de la base vectorial: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar la base vectorial", e);
        }
    }

}
