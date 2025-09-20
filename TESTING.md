# Testing Setup for Starbucks Menu AI Chat

This document explains the automated testing setup for the Starbucks Menu AI Chat application.

## Overview

The project uses GitHub Actions to automatically generate and run tests whenever code changes are pushed to the repository.

## How It Works

### 1. Automatic Test Generation

When you push changes to `main` or `develop` branches, the GitHub Actions workflow:

1. **Scans your Java source files** in `src/main/java/`
2. **Analyzes each class** to determine if it's a Controller, Service, Repository, or Entity
3. **Generates appropriate test files** with:
   - Proper test structure and annotations
   - Mock dependencies based on class type
   - Test method stubs for all public methods
   - Integration tests for the overall application

### 2. Test Types Generated

#### Controller Tests
- Uses `@WebMvcTest` for focused testing
- Includes MockMvc setup
- Mocks service dependencies
- Tests HTTP endpoints

#### Service Tests  
- Uses `@ExtendWith(MockitoExtension.class)`
- Mocks repository dependencies
- Tests business logic

#### Repository Tests
- Uses `@DataJpaTest` 
- Tests database interactions
- Uses H2 in-memory database for fast execution

#### Integration Tests
- Uses `@SpringBootTest` with random port
- Tests full application startup
- Tests API endpoints end-to-end

### 3. Pull Request Creation

After generating tests, the workflow automatically:
- Creates a new branch called `auto-generated-tests`
- Commits all generated test files
- Opens a pull request with detailed information about what was generated

## GitHub Actions Only

Tests are generated automatically through GitHub Actions. No local test generation is available - all test creation happens in the cloud when you push changes.

## Running Tests

### Run all tests:
```bash
mvn test
```

### Run with specific profile:
```bash
mvn test -Dspring.profiles.active=test
```

### Run only unit tests:
```bash
mvn test -Dtest="**/*Test"
```

### Run only integration tests:
```bash
mvn test -Dtest="**/*IntegrationTest"
```

## Test Configuration

### Test Database
- Uses H2 in-memory database for fast execution
- Schema defined in `src/test/resources/test-schema.sql`
- Configuration in `src/test/resources/application-test.yml`

### Test Profiles
- `test` profile disables AI services and uses minimal logging
- Random port assignment prevents conflicts
- Embedded database mode for isolation

## Customizing Generated Tests

The generated tests include TODO comments where you should:

1. **Add specific test data setup**
2. **Implement actual method calls**  
3. **Add meaningful assertions**
4. **Mock external dependencies properly**

Example of completing a generated test:

```java
@Test
@DisplayName("Should find drinks by category successfully")
void testFindDrinksByCategory() {
    // Arrange
    String category = "Coffee";
    List<DrinkItem> expectedDrinks = Arrays.asList(
        new DrinkItem("Latte", category, "Hot", 150)
    );
    when(drinkItemRepository.findByBeverageCategory(category))
        .thenReturn(expectedDrinks);
    
    // Act
    List<DrinkItem> result = menuService.findDrinksByCategory(category);
    
    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Latte", result.get(0).getBeverage());
    verify(drinkItemRepository).findByBeverageCategory(category);
}
```

## GitHub Actions Configuration

The workflow is defined in `.github/workflows/auto-test-generation.yml` and includes:

- **Java 17 setup**
- **Maven dependency caching**
- **Test generation and execution**
- **Automatic PR creation**
- **Test reporting**

## Best Practices

1. **Review generated tests** before merging the PR
2. **Add business logic assertions** to replace TODO comments
3. **Keep test data realistic** but minimal
4. **Use descriptive test names** that explain the expected behavior
5. **Mock external dependencies** properly to ensure test isolation

## Troubleshooting

### Tests fail due to missing dependencies
- Check that all required test dependencies are in `pom.xml`
- Ensure test configuration files are properly set up

### Database connection issues in tests
- Verify H2 dependency is included
- Check test schema matches your entity structure

### AI service connection failures
- Tests should mock AI services, not connect to real Ollama
- Use `@MockBean` for Spring AI components in integration tests

### GitHub Actions workflow fails
- Check that the repository has proper permissions for creating PRs
- Ensure Python 3 is available in the GitHub Actions environment

## Future Enhancements

- **Property-based testing** with random test data generation
- **Performance testing** for database queries
- **Contract testing** for API endpoints
- **Mutation testing** to verify test quality
- **Test coverage reporting** with badges