package com.starbucks.menuaichat.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.starbucks.menuaichat.model.DrinkItem;
import com.starbucks.menuaichat.repository.DrinkItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class DataLoaderService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);
    
    @Autowired
    private DrinkItemRepository drinkItemRepository;
    
    @Autowired
    private SpringAiVectorService springAiVectorService;
    
    @Override
    public void run(String... args) throws Exception {
        loadDrinkData();
    }
    
    private void loadDrinkData() {
        try {
            ClassPathResource resource = new ClassPathResource("csv/starbucks_drinkMenu_expanded.csv");
            
            try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
                List<String[]> records = reader.readAll();
                
                // Skip header row
                for (int i = 1; i < records.size(); i++) {
                    String[] record = records.get(i);
                    if (record.length >= 18) {
                        DrinkItem drink = createDrinkFromRecord(record);
                        DrinkItem savedDrink = drinkItemRepository.save(drink);
                        
                        // Add to Spring AI vector store
                        springAiVectorService.addDrinkToVectorStore(savedDrink);
                        
                        if (i % 10 == 0) {
                            logger.info("Processed {} of {} drinks", i, records.size() - 1);
                        }
                    }
                }
                
                logger.info("Loaded {} drink items from CSV with embeddings", records.size() - 1);
            }
        } catch (IOException | CsvException e) {
            logger.error("Error loading drink data from CSV", e);
        }
    }
    
    private DrinkItem createDrinkFromRecord(String[] record) {
        DrinkItem drink = new DrinkItem();
        
        drink.setBeverageCategory(record[0]);
        drink.setBeverage(record[1]);
        drink.setBeveragePrep(record[2]);
        drink.setCalories(parseInteger(record[3]));
        drink.setTotalFat(parseDouble(record[4]));
        drink.setTransFat(parseDouble(record[5]));
        drink.setSaturatedFat(parseDouble(record[6]));
        drink.setSodium(parseInteger(record[7]));
        drink.setTotalCarbohydrates(parseInteger(record[8]));
        drink.setCholesterol(parseInteger(record[9]));
        drink.setDietaryFibre(parseInteger(record[10]));
        drink.setSugars(parseInteger(record[11]));
        drink.setProtein(parseDouble(record[12]));
        drink.setVitaminA(record[13]);
        drink.setVitaminC(record[14]);
        drink.setCalcium(record[15]);
        drink.setIron(record[16]);
        drink.setCaffeine(parseInteger(record[17]));
        
        return drink;
    }
    
    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty() || "Varies".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty() || "Varies".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}