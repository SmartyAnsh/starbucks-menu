package com.starbucks.menuaichat.service;

import com.starbucks.menuaichat.model.DrinkItem;
import com.starbucks.menuaichat.repository.DrinkItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private DrinkItemRepository drinkItemRepository;

    @Mock
    private SpringAiVectorService springAiVectorService;

    private DrinkItem testDrink;

    @BeforeEach
    void setUp() {
        testDrink = new DrinkItem();
        testDrink.setBeverage("Test Latte");
        testDrink.setBeverageCategory("Coffee");
        testDrink.setCalories(150);
        testDrink.setCaffeine(95);
    }

    @Test
    @DisplayName("Should get all drinks successfully")
    void testGetAllDrinks() {
        // Arrange
        List<DrinkItem> expectedDrinks = Arrays.asList(testDrink);
        when(drinkItemRepository.findAll()).thenReturn(expectedDrinks);

        // Act
        List<DrinkItem> result = menuService.getAllDrinks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Latte", result.get(0).getBeverage());
        verify(drinkItemRepository).findAll();
    }

    @Test
    @DisplayName("Should find drinks by category successfully")
    void testFindDrinksByCategory() {
        // Arrange
        String category = "Coffee";
        List<DrinkItem> expectedDrinks = Arrays.asList(testDrink);
        when(drinkItemRepository.findByBeverageCategory(category)).thenReturn(expectedDrinks);

        // Act
        List<DrinkItem> result = menuService.findDrinksByCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getBeverageCategory());
        verify(drinkItemRepository).findByBeverageCategory(category);
    }

    @Test
    @DisplayName("Should format drinks for AI successfully")
    void testFormatDrinksForAI() {
        // Arrange
        List<DrinkItem> drinks = Arrays.asList(testDrink);

        // Act
        String result = menuService.formatDrinksForAI(drinks);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Test Latte"));
        assertTrue(result.contains("Coffee"));
        assertTrue(result.contains("150 cal"));
    }

    @Test
    @DisplayName("Should handle empty drinks list for AI formatting")
    void testFormatDrinksForAIEmpty() {
        // Arrange
        List<DrinkItem> emptyDrinks = Arrays.asList();

        // Act
        String result = menuService.formatDrinksForAI(emptyDrinks);

        // Assert
        assertNotNull(result);
        assertEquals("No drinks found matching the criteria.", result);
    }
}