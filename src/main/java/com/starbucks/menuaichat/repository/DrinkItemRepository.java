package com.starbucks.menuaichat.repository;

import com.starbucks.menuaichat.model.DrinkItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrinkItemRepository extends CrudRepository<DrinkItem, Long> {
    
    // Find drinks by IDs (for vector search results)
    @Query("SELECT * FROM drink_items WHERE id IN (:ids)")
    List<DrinkItem> findByIdIn(@Param("ids") List<Long> ids);
    
    // Keep one simple query for basic category filtering if needed
    @Query("SELECT * FROM drink_items WHERE LOWER(beverage_category) = LOWER(:category)")
    List<DrinkItem> findByBeverageCategory(@Param("category") String category);
}