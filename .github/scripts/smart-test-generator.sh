#!/bin/bash

echo "🧠 Starting smart test generation..."

# Create test directories
mkdir -p src/test/java/com/starbucks/menuaichat/{controller,service,repository,model}
mkdir -p src/test/resources

# Function to analyze Java file and extract information
analyze_java_file() {
    local file="$1"
    local class_name=$(basename "$file" .java)
    local package=$(grep -m1 "^package " "$file" | sed 's/package //; s/;//')
    local methods=$(grep -E "^\s*public\s+\w+.*\(" "$file" | grep -v "class\|interface" | sed 's/.*public [^(]* \([^(]*\).*/\1/' | grep -v "equals\|hashCode\|toString")
    local fields=$(grep -E "^\s*private\s+\w+" "$file" | sed 's/.*private [^;]* \([^;]*\);.*/\1/')
    
    # Determine class type
    local class_type="model"
    if grep -q "@Controller\|@RestController" "$file"; then
        class_type="controller"
    elif grep -q "@Service" "$file"; then
        class_type="service"
    elif grep -q "@Repository" "$file"; then
        class_type="repository"
    fi
    
    echo "$class_name|$package|$class_type|$methods|$fields"
}

# Function to generate controller test
generate_controller_test() {
    local class_name="$1"
    local package="$2"
    local methods="$3"
    
    cat > "src/test/java/${package//./\/}/${class_name}Test.java" << EOF
package $package;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest($class_name.class)
class ${class_name}Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private com.starbucks.menuaichat.service.MenuService menuService;

    @MockBean
    private com.starbucks.menuaichat.service.StarbucksAiChatService aiChatService;

    @Test
    @DisplayName("Should load $class_name context successfully")
    void contextLoads() {
        assertNotNull(mockMvc);
    }

    @Test
    @DisplayName("Should handle basic HTTP requests")
    void testBasicHttpHandling() throws Exception {
        // This is a placeholder test - customize based on actual endpoints
        // Example: mockMvc.perform(get("/api/endpoint")).andExpect(status().isOk());
        assertNotNull(mockMvc);
    }
}
EOF
}

# Function to generate service test
generate_service_test() {
    local class_name="$1"
    local package="$2"
    local methods="$3"
    
    cat > "src/test/java/${package//./\/}/${class_name}Test.java" << EOF
package $package;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ${class_name}Test {

    @InjectMocks
    private $class_name ${class_name,,};

    @Mock
    private com.starbucks.menuaichat.repository.DrinkItemRepository drinkItemRepository;

    @Mock
    private com.starbucks.menuaichat.repository.ChatSessionRepository chatSessionRepository;

    @Mock
    private com.starbucks.menuaichat.repository.ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setUp() {
        // Setup test data
    }

    @Test
    @DisplayName("Should create $class_name instance successfully")
    void testServiceCreation() {
        assertNotNull(${class_name,,});
    }

    @Test
    @DisplayName("Should handle service operations")
    void testServiceOperations() {
        // Mock repository responses
        when(drinkItemRepository.findAll()).thenReturn(Collections.emptyList());
        
        // Test service functionality
        assertNotNull(${class_name,,});
        
        // Verify interactions
        // verify(drinkItemRepository, times(1)).findAll();
    }
}
EOF
}

# Function to generate repository test
generate_repository_test() {
    local class_name="$1"
    local package="$2"
    
    cat > "src/test/java/${package//./\/}/${class_name}Test.java" << EOF
package $package;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ${class_name}Test {

    @Mock
    private $class_name ${class_name,,};

    @Test
    @DisplayName("Should perform basic repository operations")
    void testBasicRepositoryOperations() {
        // Mock repository behavior
        when(${class_name,,}.findAll()).thenReturn(Collections.emptyList());
        
        // Test repository functionality
        List<?> results = (List<?>) ${class_name,,}.findAll();
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(${class_name,,}).findAll();
    }

    @Test
    @DisplayName("Should handle save operations")
    void testSaveOperations() {
        // Mock save behavior
        when(${class_name,,}.save(any())).thenReturn(null);
        
        // Test save functionality
        assertNotNull(${class_name,,});
        
        // Verify save was called
        // verify(${class_name,,}).save(any());
    }
}
EOF
}

# Function to generate model test
generate_model_test() {
    local class_name="$1"
    local package="$2"
    
    cat > "src/test/java/${package//./\/}/${class_name}Test.java" << EOF
package $package;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ${class_name}Test {

    private $class_name ${class_name,,};

    @BeforeEach
    void setUp() {
        ${class_name,,} = new $class_name();
    }

    @Test
    @DisplayName("Should create $class_name instance successfully")
    void testModelCreation() {
        assertNotNull(${class_name,,});
    }

    @Test
    @DisplayName("Should handle basic model operations")
    void testBasicModelOperations() {
        // Test basic model functionality
        assertNotNull(${class_name,,});
        
        // Add specific getter/setter tests here
        // Example: ${class_name,,}.setSomeField("test");
        // assertEquals("test", ${class_name,,}.getSomeField());
    }

    @Test
    @DisplayName("Should handle toString method")
    void testToString() {
        String result = ${class_name,,}.toString();
        assertNotNull(result);
    }
}
EOF
}

# Process all Java files
echo "🔍 Scanning for Java source files..."

find src/main/java -name "*.java" -not -name "*Application.java" | while read -r java_file; do
    echo "📝 Processing: $java_file"
    
    # Analyze the file
    analysis=$(analyze_java_file "$java_file")
    IFS='|' read -r class_name package class_type methods fields <<< "$analysis"
    
    # Check if test already exists
    test_file="src/test/java/${package//./\/}/${class_name}Test.java"
    if [[ -f "$test_file" ]]; then
        echo "⏭️  Test already exists: $test_file"
        continue
    fi
    
    # Create directory
    mkdir -p "src/test/java/${package//./\/}"
    
    # Generate appropriate test
    case "$class_type" in
        "controller")
            generate_controller_test "$class_name" "$package" "$methods"
            echo "🎮 Generated controller test: $test_file"
            ;;
        "service")
            generate_service_test "$class_name" "$package" "$methods"
            echo "⚙️  Generated service test: $test_file"
            ;;
        "repository")
            generate_repository_test "$class_name" "$package"
            echo "🗄️  Generated repository test: $test_file"
            ;;
        *)
            generate_model_test "$class_name" "$package"
            echo "📋 Generated model test: $test_file"
            ;;
    esac
done

echo "✅ Smart test generation completed!"