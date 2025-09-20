package com.starbucks.menuaichat.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Table("drink_items")
public class DrinkItem {
    
    @Id
    private Long id;
    
    @Column("beverage_category")
    private String beverageCategory;
    
    @Column("beverage")
    private String beverage;
    
    @Column("beverage_prep")
    private String beveragePrep;
    
    @Column("calories")
    private Integer calories;
    
    @Column("total_fat")
    private Double totalFat;
    
    @Column("trans_fat")
    private Double transFat;
    
    @Column("saturated_fat")
    private Double saturatedFat;
    
    @Column("sodium")
    private Integer sodium;
    
    @Column("total_carbohydrates")
    private Integer totalCarbohydrates;
    
    @Column("cholesterol")
    private Integer cholesterol;
    
    @Column("dietary_fibre")
    private Integer dietaryFibre;
    
    @Column("sugars")
    private Integer sugars;
    
    @Column("protein")
    private Double protein;
    
    @Column("vitamin_a")
    private String vitaminA;
    
    @Column("vitamin_c")
    private String vitaminC;
    
    @Column("calcium")
    private String calcium;
    
    @Column("iron")
    private String iron;
    
    @Column("caffeine")
    private Integer caffeine;

    // Constructors
    public DrinkItem() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBeverageCategory() { return beverageCategory; }
    public void setBeverageCategory(String beverageCategory) { this.beverageCategory = beverageCategory; }

    public String getBeverage() { return beverage; }
    public void setBeverage(String beverage) { this.beverage = beverage; }

    public String getBeveragePrep() { return beveragePrep; }
    public void setBeveragePrep(String beveragePrep) { this.beveragePrep = beveragePrep; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public Double getTotalFat() { return totalFat; }
    public void setTotalFat(Double totalFat) { this.totalFat = totalFat; }

    public Double getTransFat() { return transFat; }
    public void setTransFat(Double transFat) { this.transFat = transFat; }

    public Double getSaturatedFat() { return saturatedFat; }
    public void setSaturatedFat(Double saturatedFat) { this.saturatedFat = saturatedFat; }

    public Integer getSodium() { return sodium; }
    public void setSodium(Integer sodium) { this.sodium = sodium; }

    public Integer getTotalCarbohydrates() { return totalCarbohydrates; }
    public void setTotalCarbohydrates(Integer totalCarbohydrates) { this.totalCarbohydrates = totalCarbohydrates; }

    public Integer getCholesterol() { return cholesterol; }
    public void setCholesterol(Integer cholesterol) { this.cholesterol = cholesterol; }

    public Integer getDietaryFibre() { return dietaryFibre; }
    public void setDietaryFibre(Integer dietaryFibre) { this.dietaryFibre = dietaryFibre; }

    public Integer getSugars() { return sugars; }
    public void setSugars(Integer sugars) { this.sugars = sugars; }

    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }

    public String getVitaminA() { return vitaminA; }
    public void setVitaminA(String vitaminA) { this.vitaminA = vitaminA; }

    public String getVitaminC() { return vitaminC; }
    public void setVitaminC(String vitaminC) { this.vitaminC = vitaminC; }

    public String getCalcium() { return calcium; }
    public void setCalcium(String calcium) { this.calcium = calcium; }

    public String getIron() { return iron; }
    public void setIron(String iron) { this.iron = iron; }

    public Integer getCaffeine() { return caffeine; }
    public void setCaffeine(Integer caffeine) { this.caffeine = caffeine; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s): %d calories, %dmg caffeine, %.1fg fat, %dg carbs, %.1fg protein",
                beverageCategory, beverage, beveragePrep, calories, caffeine, totalFat, totalCarbohydrates, protein);
    }
}