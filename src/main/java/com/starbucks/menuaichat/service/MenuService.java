package com.starbucks.menuaichat.service;

import com.starbucks.menuaichat.model.DrinkItem;
import com.starbucks.menuaichat.repository.DrinkItemRepository;
import org.springframework.ai.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuService.class);
    
    @Autowired
    private DrinkItemRepository drinkItemRepository;
    
    @Autowired
    private SpringAiVectorService springAiVectorService;
    
    public List<DrinkItem> getAllDrinks() {
        return (List<DrinkItem>) drinkItemRepository.findAll();
    }
    
    public List<DrinkItem> findDrinksByCategory(String category) {
        logger.debug("🔍 Searching drinks by exact category: '{}'", category);
        List<DrinkItem> results = drinkItemRepository.findByBeverageCategory(category);
        logger.info("📊 Found {} drinks in category '{}'", results.size(), category);
        return results;
    }
    
    public String formatDrinksForAI(List<DrinkItem> drinks) {
        if (drinks.isEmpty()) {
            return "No drinks found matching the criteria.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(drinks.size()).append(" drinks:\n\n");
        
        for (DrinkItem drink : drinks) {
            sb.append("• ").append(drink.getBeverage())
              .append(" (").append(drink.getBeveragePrep()).append(")")
              .append(" - Category: ").append(drink.getBeverageCategory())
              .append("\n  Nutrition: ").append(drink.getCalories()).append(" cal, ")
              .append(drink.getCaffeine()).append("mg caffeine, ")
              .append(drink.getTotalFat()).append("g fat, ")
              .append(drink.getProtein()).append("g protein\n\n");
        }
        
        return sb.toString();
    }
    
    public List<DrinkItem> findSimilarDrinksByDescription(String query, int limit) {
        logger.debug("🔍 Spring AI vector search for drinks by description: '{}'", query);
        List<Document> documents = springAiVectorService.searchSimilarDrinksByDescription(query, limit);
        
        List<Long> drinkIds = documents.stream()
            .map(doc -> Long.parseLong(doc.getMetadata().get("drink_id").toString()))
            .distinct()
            .toList();
        
        List<DrinkItem> results = drinkItemRepository.findByIdIn(drinkIds);
        logger.info("📊 Spring AI found {} similar drinks by description", results.size());
        return results;
    }
    
    public List<DrinkItem> findSimilarDrinksByNutrition(String query, int limit) {
        logger.debug("🔍 Spring AI vector search for drinks by nutrition: '{}'", query);
        List<Document> documents = springAiVectorService.searchSimilarDrinksByNutrition(query, limit);
        
        List<Long> drinkIds = documents.stream()
            .map(doc -> Long.parseLong(doc.getMetadata().get("drink_id").toString()))
            .distinct()
            .toList();
        
        List<DrinkItem> results = drinkItemRepository.findByIdIn(drinkIds);
        logger.info("📊 Spring AI found {} similar drinks by nutrition", results.size());
        return results;
    }
}