# GitHub Actions Setup for Automated Testing

## Overview

Your Starbucks Menu AI Chat project now has a complete automated testing setup with GitHub Actions that will:

1. **Generate tests automatically** when you push code changes
2. **Run tests on every pull request**
3. **Create pull requests** with generated tests
4. **Provide detailed test reports**

## What's Been Added

### 🔧 GitHub Actions Workflows

#### 1. Auto Test Generation (`.github/workflows/auto-test-generation.yml`)
- **Triggers**: Push to `main` or `develop` branches
- **Actions**:
  - Analyzes your Java source code
  - Generates unit tests for Controllers, Services, Repositories
  - Creates integration tests
  - Runs the generated tests
  - Creates a pull request with the new tests

#### 2. Test on Pull Request (`.github/workflows/test-on-pr.yml`)
- **Triggers**: Pull requests to `main` or `develop`
- **Actions**:
  - Runs all existing tests
  - Generates test reports
  - Comments results on the PR
  - Uploads test artifacts

### 🤖 Automated Test Generation

#### GitHub Actions Test Generation Script
- Python script located at `.github/scripts/generate-tests.py`
- Analyzes Java classes and generates appropriate test types
- Creates realistic test structures with proper mocking
- Handles Controllers, Services, Repositories, and Entities differently

### 🧪 Test Configuration

#### Test Dependencies Added to `pom.xml`:
- H2 Database for in-memory testing
- Testcontainers for integration testing
- Additional Spring Boot test utilities

#### Test Configuration Files:
- `src/test/resources/application-test.yml` - Test-specific configuration
- `src/test/resources/test-schema.sql` - Test database schema

### 📋 Example Tests Created

#### `src/test/java/com/starbucks/menuaichat/service/MenuServiceTest.java`
- Example unit test showing proper structure
- Demonstrates mocking and assertions
- Shows best practices for Spring Boot testing

#### `src/test/java/com/starbucks/menuaichat/integration/ApplicationIntegrationTest.java`
- Example integration test
- Tests full application startup
- Validates API endpoints

### ☁️ Cloud-Only Generation

#### GitHub Actions Exclusive
- All test generation happens in GitHub Actions
- No local scripts or dependencies required
- Clean separation between development and test generation
- Automated workflow triggers on code changes

## How to Use

### 1. Automatic Test Generation

Simply push your code changes:

```bash
git add .
git commit -m "Add new feature"
git push origin main
```

The GitHub Actions workflow will:
1. Generate tests for any new or modified Java classes
2. Run the tests to ensure they work
3. Create a pull request with the generated tests

### 2. GitHub Actions Only

All test generation is handled automatically by GitHub Actions. Simply push your changes and the workflow will:
- Analyze your code
- Generate appropriate tests
- Create a pull request with the results

### 3. Running Tests

```bash
# Run all tests
mvn test

# Run with test profile
mvn test -Dspring.profiles.active=test

# Run specific test class
mvn test -Dtest=MenuServiceTest
```

## Generated Test Structure

```
src/test/java/com/starbucks/menuaichat/
├── controller/
│   ├── ChatControllerTest.java
│   └── ...
├── service/
│   ├── MenuServiceTest.java
│   ├── StarbucksAiChatServiceTest.java
│   └── ...
├── repository/
│   ├── DrinkItemRepositoryTest.java
│   └── ...
├── model/
│   └── ...
└── integration/
    └── ApplicationIntegrationTest.java
```

## Test Types Generated

### Controller Tests
- Use `@WebMvcTest` for focused testing
- Include MockMvc setup
- Mock service dependencies
- Test HTTP endpoints with proper status codes

### Service Tests
- Use `@ExtendWith(MockitoExtension.class)`
- Mock repository dependencies
- Test business logic in isolation
- Verify method interactions

### Repository Tests
- Use `@DataJpaTest` for database testing
- Test with H2 in-memory database
- Verify database queries and operations

### Integration Tests
- Use `@SpringBootTest` with random port
- Test full application context
- Validate end-to-end functionality

## Customizing Generated Tests

The generated tests include TODO comments where you should:

1. **Add specific test data** relevant to your business logic
2. **Implement actual method calls** with proper parameters
3. **Add meaningful assertions** that verify expected behavior
4. **Configure mocks** to return realistic test data

Example:
```java
@Test
@DisplayName("Should find drinks by category successfully")
void testFindDrinksByCategory() {
    // Arrange
    String category = "Coffee";
    List<DrinkItem> expectedDrinks = Arrays.asList(
        createTestDrink("Latte", category, 150)
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

## Benefits

### 🚀 **Faster Development**
- Automatic test scaffolding saves hours of manual work
- Consistent test structure across the project
- Immediate feedback on test coverage

### 🛡️ **Better Code Quality**
- Every push triggers test generation and execution
- Pull requests include comprehensive test coverage
- Integration tests catch configuration issues

### 📊 **Visibility**
- Test reports on every PR
- Clear feedback on test status
- Easy identification of failing tests

### 🔄 **Continuous Integration**
- Automated testing pipeline
- No manual intervention required
- Consistent testing environment

## Next Steps

1. **Push your first change** to see the workflow in action
2. **Review the generated tests** in the created pull request
3. **Customize the TODO sections** with actual business logic
4. **Add more specific test cases** as your application grows
5. **Configure any additional test dependencies** as needed

## Troubleshooting

### Common Issues:

1. **Tests fail due to missing AI service**
   - Tests should mock AI services, not connect to real Ollama
   - Check that `@MockBean` is used for Spring AI components

2. **Database connection issues**
   - Verify H2 dependency is included in pom.xml
   - Check that test schema matches your entity structure

3. **GitHub Actions permissions**
   - Ensure repository has permissions to create pull requests
   - Check that `GITHUB_TOKEN` has appropriate scopes

### Getting Help:

- Check the GitHub Actions logs for detailed error messages
- Review the generated test files for syntax issues
- Ensure all dependencies are properly configured in pom.xml

## Future Enhancements

- **Code coverage reporting** with badges
- **Performance testing** for database queries
- **Contract testing** for API endpoints
- **Mutation testing** to verify test quality
- **Automated test data generation** with realistic scenarios

---

🎉 **Your automated testing setup is now complete!** Push some changes and watch the magic happen!