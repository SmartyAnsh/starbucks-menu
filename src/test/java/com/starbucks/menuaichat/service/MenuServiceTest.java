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

    private DrinkItem testDrink;

    @BeforeEach
    void setUp() {
        testDrink = new DrinkItem();
        testDrink.setBeverage("Test Latte");
        testDrink.setBeverageCategory("Coffee");
        testDrink.setCalories(150);
        testDrink.setCaffeineMs(95);
    }

    @Test
    @DisplayName("Should find all drinks successfully")
    void testFindAllDrinks() {
        // Arrange
        List<DrinkItem> expectedDrinks = Arrays.asList(testDrink);
        when(drinkItemRepository.findAll()).thenReturn(expectedDrinks);

        // Act
        List<DrinkItem> result = menuService.findAllDrinks();

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
    @DisplayName("Should find low calorie drinks successfully")
    void testFindLowCalorieDrinks() {
        // Arrange
        int maxCalories = 200;
        List<DrinkItem> expectedDrinks = Arrays.asList(testDrink);
        when(drinkItemRepository.findByCaloriesLessThanEqual(maxCalories)).thenReturn(expectedDrinks);

        // Act
        List<DrinkItem> result = menuService.findLowCalorieDrinks(maxCalories);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getCalories() <= maxCalories);
        verify(drinkItemRepository).findByCaloriesLessThanEqual(maxCalories);
    }
}