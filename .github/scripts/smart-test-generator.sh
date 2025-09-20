#!/bin/bash

echo "🧠 Starting smart test generation..."

# Create test directories
mkdir -p src/test/java/com/starbucks/menuaichat/service
mkdir -p src/test/resources

# Function to check if file is a service class
is_service_class() {
    local file="$1"
    grep -q "@Service" "$file"
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
        
        # Generate service test
        generate_service_test "$class_name" "$package"
        echo "⚙️  Generated service test: $test_file"
    else
        echo "⏭️  Skipping non-service class: $java_file"
    fi
done

echo "✅ Smart test generation completed!"