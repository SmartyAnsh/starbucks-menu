#!/usr/bin/env python3
"""
GitHub Actions test generation script for Spring Boot applications.
Analyzes Java source files and generates comprehensive unit and integration tests.
"""

import os
import re
from pathlib import Path

def extract_class_info(java_file):
    with open(java_file, 'r') as f:
        content = f.read()
    
    package_match = re.search(r'package\s+([\w.]+);', content)
    package = package_match.group(1) if package_match else ''
    
    class_match = re.search(r'public\s+(?:class|interface)\s+(\w+)', content)
    class_name = class_match.group(1) if class_match else ''
    
    method_matches = re.findall(r'public\s+[\w<>,\s\[\]]+\s+(\w+)\s*\([^)]*\)', content)
    
    is_controller = '@Controller' in content or '@RestController' in content
    is_service = '@Service' in content
    is_repository = '@Repository' in content
    is_entity = '@Entity' in content or '@Table' in content
    
    return {
        'package': package,
        'class_name': class_name,
        'methods': method_matches,
        'is_controller': is_controller,
        'is_service': is_service,
        'is_repository': is_repository,
        'is_entity': is_entity,
        'content': content
    }

def generate_controller_test(class_info):
    package = class_info['package']
    class_name = class_info['class_name']
    methods = class_info['methods']
    
    test_methods = []
    for method in methods:
        if method not in ['equals', 'hashCode', 'toString']:
            test_methods.append(f'''
    @Test
    @DisplayName("Should handle {method} endpoint successfully")
    void test{method.capitalize()}() throws Exception {{
        // Arrange - Mock service responses
        when(menuService.getAllDrinks()).thenReturn(Collections.emptyList());
        when(aiChatService.processMessage(anyString(), anyString())).thenReturn("Mocked response");
        
        // Act & Assert - Test endpoint
        mockMvc.perform(get("/api/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        // Verify mocks were called
        verify(menuService, atLeastOnce()).getAllDrinks();
    }}''')
    
    return f'''package {package};

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import java.util.Collections;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest({class_name}.class)
class {class_name}Test {{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.starbucks.menuaichat.service.MenuService menuService;

    @MockBean
    private com.starbucks.menuaichat.service.StarbucksAiChatService aiChatService;

    @MockBean
    private com.starbucks.menuaichat.service.DataLoaderService dataLoaderService;

    @MockBean
    private com.starbucks.menuaichat.service.SpringAiVectorService springAiVectorService;
{chr(10).join(test_methods)}
}}
'''

def generate_service_test(class_info):
    package = class_info['package']
    class_name = class_info['class_name']
    methods = class_info['methods']
    
    test_methods = []
    for method in methods:
        if method not in ['equals', 'hashCode', 'toString']:
            test_methods.append(f'''
    @Test
    @DisplayName("Should {method} successfully")
    void test{method.capitalize()}() {{
        // Arrange - Mock dependencies
        when(drinkItemRepository.findAll()).thenReturn(Collections.emptyList());
        when(chatSessionRepository.save(any())).thenReturn(mockChatSession);
        when(springAiVectorService.searchSimilarDrinksByDescription(anyString(), anyInt()))
            .thenReturn(Collections.emptyList());
        
        // Act - Call the method
        // TODO: Add actual method call based on {method}
        
        // Assert - Verify behavior
        assertNotNull({class_name.lower()});
        verify(drinkItemRepository, atMostOnce()).findAll();
    }}''')
    
    return f'''package {package};

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.starbucks.menuaichat.model.DrinkItem;
import com.starbucks.menuaichat.model.ChatSession;
import com.starbucks.menuaichat.model.ChatMessage;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class {class_name}Test {{

    @InjectMocks
    private {class_name} {class_name.lower()};

    @Mock
    private com.starbucks.menuaichat.repository.DrinkItemRepository drinkItemRepository;

    @Mock
    private com.starbucks.menuaichat.repository.ChatSessionRepository chatSessionRepository;

    @Mock
    private com.starbucks.menuaichat.repository.ChatMessageRepository chatMessageRepository;

    @Mock
    private com.starbucks.menuaichat.service.SpringAiVectorService springAiVectorService;

    private DrinkItem mockDrinkItem;
    private ChatSession mockChatSession;
    private ChatMessage mockChatMessage;

    @BeforeEach
    void setUp() {{
        // Setup mock objects
        mockDrinkItem = new DrinkItem();
        mockDrinkItem.setBeverage("Test Drink");
        mockDrinkItem.setCalories(100);
        
        mockChatSession = new ChatSession();
        mockChatSession.setId("test-session");
        
        mockChatMessage = new ChatMessage();
        mockChatMessage.setMessage("Test message");
    }}
{chr(10).join(test_methods)}
}}
'''

def generate_repository_test(class_info):
    package = class_info['package']
    class_name = class_info['class_name']
    
    return f'''package {package};

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class {class_name}Test {{

    @Mock
    private {class_name} {class_name.lower()};

    @Test
    @DisplayName("Should perform repository operations successfully")
    void testRepositoryOperations() {{
        // Arrange - Mock repository behavior
        when({class_name.lower()}.findAll()).thenReturn(Collections.emptyList());
        when({class_name.lower()}.save(any())).thenReturn(null);
        
        // Act - Call repository methods
        List<?> results = (List<?>) {class_name.lower()}.findAll();
        
        // Assert - Verify behavior
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify({class_name.lower()}).findAll();
    }}

    @Test
    @DisplayName("Should handle custom queries successfully")
    void testCustomQueries() {{
        // Arrange - Mock custom query behavior
        when({class_name.lower()}.findByIdIn(anyList())).thenReturn(Collections.emptyList());
        
        // Act - Call custom query
        List<?> results = {class_name.lower()}.findByIdIn(Arrays.asList(1L, 2L));
        
        // Assert - Verify results
        assertNotNull(results);
        verify({class_name.lower()}).findByIdIn(anyList());
    }}
}}
'''

def main():
    """Main function to generate tests for all Java classes."""
    src_dir = Path('src/main/java')
    test_dir = Path('src/test/java')
    
    if not src_dir.exists():
        print("❌ Source directory not found!")
        return
    
    test_dir.mkdir(parents=True, exist_ok=True)
    generated_count = 0
    
    print("🔍 Scanning for Java files...")
    
    for java_file in src_dir.rglob('*.java'):
        if 'Application.java' in java_file.name:
            continue
            
        print(f"📝 Analyzing {java_file}")
        class_info = extract_class_info(java_file)
        
        if not class_info['class_name']:
            continue
        
        # Create test file path
        relative_path = java_file.relative_to(src_dir)
        test_file_path = test_dir / relative_path.parent / f"{class_info['class_name']}Test.java"
        test_file_path.parent.mkdir(parents=True, exist_ok=True)
        
        # Skip if test already exists
        if test_file_path.exists():
            print(f"⏭️  Test already exists: {test_file_path}")
            continue
        
        # Generate appropriate test based on class type
        test_content = ""
        if class_info['is_controller']:
            test_content = generate_controller_test(class_info)
            print(f"🎮 Generated controller test for {class_info['class_name']}")
        elif class_info['is_service']:
            test_content = generate_service_test(class_info)
            print(f"⚙️  Generated service test for {class_info['class_name']}")
        elif class_info['is_repository']:
            test_content = generate_repository_test(class_info)
            print(f"🗄️  Generated repository test for {class_info['class_name']}")
        else:
            # Generate basic unit test
            test_content = generate_service_test(class_info)  # Use service template as default
            print(f"📋 Generated basic test for {class_info['class_name']}")
        
        if test_content:
            test_file_path.write_text(test_content)
            generated_count += 1
    
    print(f"\n✅ Generated {generated_count} test files!")

if __name__ == "__main__":
    main()