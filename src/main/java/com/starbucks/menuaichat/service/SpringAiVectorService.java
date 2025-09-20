package com.starbucks.menuaichat.service;

import com.starbucks.menuaichat.model.DrinkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SpringAiVectorService {
    
    private static final Logger logger = LoggerFactory.getLogger(SpringAiVectorService.class);
    
    @Autowired
    private VectorStore vectorStore;
    
    @Autowired
    private EmbeddingModel embeddingModel;
    
    public void addDrinkToVectorStore(DrinkItem drink) {
        logger.debug("🔢 Adding drink to vector store: {}", drink.getBeverage());
        
        // Create description document
        String descriptionText = String.format("%s %s %s", 
            drink.getBeverageCategory(), drink.getBeverage(), drink.getBeveragePrep());
        
        Document descDoc = new Document(descriptionText, Map.of(
            "drink_id", drink.getId().toString(),
            "type", "description",
            "beverage", drink.getBeverage(),
            "category", drink.getBeverageCategory(),
            "prep", drink.getBeveragePrep()
        ));
        
        // Create nutritional document
        String nutritionalText = String.format("calories: %d, fat: %.1fg, protein: %.1fg, caffeine: %dmg, sugars: %dg",
            drink.getCalories() != null ? drink.getCalories() : 0,
            drink.getTotalFat() != null ? drink.getTotalFat() : 0.0,
            drink.getProtein() != null ? drink.getProtein() : 0.0,
            drink.getCaffeine() != null ? drink.getCaffeine() : 0,
            drink.getSugars() != null ? drink.getSugars() : 0);
        
        Document nutDoc = new Document(nutritionalText, Map.of(
            "drink_id", drink.getId().toString(),
            "type", "nutritional",
            "beverage", drink.getBeverage(),
            "calories", drink.getCalories() != null ? drink.getCalories().toString() : "0",
            "caffeine", drink.getCaffeine() != null ? drink.getCaffeine().toString() : "0"
        ));
        
        // Add both documents to vector store
        vectorStore.add(List.of(descDoc, nutDoc));
        logger.debug("✅ Added drink documents to vector store");
    }
    
    public List<Document> searchSimilarDrinks(String query, String type, int limit) {
        logger.debug("🔍 Spring AI vector search: '{}' (type: {})", query, type);
        
        // Use the simple similaritySearch method with query string
        List<Document> allResults = vectorStore.similaritySearch(query);
        
        // Filter by type and limit results
        List<Document> results = allResults.stream()
            .filter(doc -> type.equals(doc.getMetadata().get("type")))
            .limit(limit)
            .toList();
        
        logger.info("📊 Spring AI found {} similar documents", results.size());
        
        return results;
    }
    
    public List<Document> searchSimilarDrinksByDescription(String query, int limit) {
        return searchSimilarDrinks(query, "description", limit);
    }
    
    public List<Document> searchSimilarDrinksByNutrition(String query, int limit) {
        return searchSimilarDrinks(query, "nutritional", limit);
    }
}