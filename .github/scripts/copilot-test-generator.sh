#!/bin/bash

echo "🤖 Starting GitHub Copilot-powered test generation..."

# Create test directories
mkdir -p src/test/java/com/starbucks/menuaichat/service
mkdir -p src/test/resources

# Function to generate test using GitHub Copilot CLI
generate_test_with_copilot() {
    local java_file="$1"
    local class_name="$2"
    local package="$3"
    
    echo "🧠 Generating test for $class_name using GitHub Copilot..."
    
    # Read the source file
    source_code=$(cat "$java_file")
    
    # Create a prompt for Copilot
    prompt="Generate a comprehensive JUnit 5 unit test class for the following Spring Boot service class. 

Requirements:
- Use JUnit 5 with @ExtendWith(MockitoExtension.class)
- Use @InjectMocks for the service under test
- Use @Mock for all dependencies (repositories, other services)
- Include @BeforeEach setup method
- Add @DisplayName annotations for all tests
- Mock all external dependencies properly
- Test all public methods
- Use realistic test data
- Include proper assertions and verifications

Service class to test:
\`\`\`java
$source_code
\`\`\`

Generate only the complete test class code with package $package and class name ${class_name}Test."

    # Use GitHub Copilot CLI to generate the test (non-interactive)
    gh copilot suggest -p "$prompt" > temp_copilot_response.txt 2>&1
    
    # Check if Copilot responded successfully
    if [ $? -eq 0 ] && [ -s temp_copilot_response.txt ]; then
        # Extract the Java code from Copilot's response
        test_content=$(cat temp_copilot_response.txt | sed -n '/```java/,/```/p' | sed '1d;$d')
        
        # If no Java code block found, try to extract the whole response
        if [ -z "$test_content" ]; then
            test_content=$(cat temp_copilot_response.txt)
        fi
        
        # Clean up the response
        test_content=$(echo "$test_content" | sed 's/^[[:space:]]*//' | sed '/^$/d')
        
        # Determine output path
        test_file_path="src/test/java/${package//./\/}/${class_name}Test.java"
        
        # Create directory and write test file
        mkdir -p "$(dirname "$test_file_path")"
        echo "$test_content" > "$test_file_path"
        
        echo "✅ Generated Copilot test: $test_file_path"
        
        # Clean up temp file
        rm -f temp_copilot_response.txt
        
        return 0
    else
        echo "⚠️  Copilot failed for $class_name, falling back to template..."
        if [ -f temp_copilot_response.txt ]; then
            echo "ℹ️  Copilot error/output for $class_name (first 50 lines):"
            head -n 50 temp_copilot_response.txt || true
        fi
        rm -f temp_copilot_response.txt
        return 1
    fi
}

# Fallback function using simple template
generate_fallback_test() {
    local class_name="$1"
    local package="$2"
    
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
import static org.mockito.Mockito.lenient;

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
        // Use lenient mode to avoid unnecessary stubbing exceptions
        lenient().when(drinkItemRepository.findAll()).thenReturn(Collections.emptyList());
        lenient().when(chatSessionRepository.save(any())).thenReturn(null);
        lenient().when(chatMessageRepository.save(any())).thenReturn(null);
    }

    @Test
    @DisplayName("Should create $class_name instance successfully")
    void testServiceCreation() {
        assertNotNull(${class_name,,});
    }

    @Test
    @DisplayName("Should handle service operations with mocked dependencies")
    void testServiceOperations() {
        // Act - Test service functionality
        // TODO: Add specific method calls for $class_name
        // Example: List<?> result = ${class_name,,}.someMethod();
        
        // Assert - Verify behavior
        assertNotNull(${class_name,,});
        
        // Note: Add mocking when you implement actual method calls
        // when(drinkItemRepository.findAll()).thenReturn(Collections.emptyList());
        // verify(drinkItemRepository, times(1)).findAll();
    }
}
EOF
}

# Function to check if file is a service class
is_service_class() {
    local file="$1"
    grep -q "@Service" "$file"
}

# GitHub CLI is already authenticated via GH_TOKEN environment variable
echo "🔐 GitHub CLI is authenticated and ready"

# Check if Copilot is available from environment variable
if [ "$COPILOT_AVAILABLE" = "true" ]; then
    echo "✅ GitHub Copilot CLI is ready!"
    USE_COPILOT=true
else
    echo "⚠️  GitHub Copilot CLI not available. Using smart templates."
    USE_COPILOT=false
fi

# Process only service files
echo "🔍 Scanning for Service classes..."

find src/main/java -name "*.java" -not -name "*Application.java" | while read -r java_file; do
    # Only process service classes
    if is_service_class "$java_file"; then
        echo "📝 Processing service: $java_file"
        
        class_name=$(basename "$java_file" .java)
        package=$(grep -m1 "^package " "$java_file" | sed 's/package //; s/;//')
        
        # Check if test already exists
        test_file="src/test/java/${package//./\/}/${class_name}Test.java"
        if [[ -f "$test_file" ]]; then
            echo "⏭️  Test already exists: $test_file"
            continue
        fi
        
        # Create directory
        mkdir -p "src/test/java/${package//./\/}"
        
        # Try to generate with Copilot first, fallback to template
        if [ "$USE_COPILOT" = true ]; then
            if ! generate_test_with_copilot "$java_file" "$class_name" "$package"; then
                echo "🔄 Falling back to template for $class_name"
                generate_fallback_test "$class_name" "$package"
                echo "📋 Generated fallback test: $test_file"
            fi
        else
            generate_fallback_test "$class_name" "$package"
            echo "📋 Generated template test: $test_file"
        fi
    else
        echo "⏭️  Skipping non-service class: $java_file"
    fi
done

echo "🎉 GitHub Copilot-powered test generation completed!"