#!/bin/bash

echo "🤖 Starting AI-powered test generation..."

# Create test directories
mkdir -p src/test/java/com/starbucks/menuaichat/{controller,service,repository,model}
mkdir -p src/test/resources

# Function to generate test using Ollama
generate_test_with_ai() {
    local java_file="$1"
    local class_name="$2"
    local package="$3"
    local class_type="$4"
    
    echo "🧠 Analyzing $class_name ($class_type) with AI..."
    
    # Read the source file
    source_code=$(cat "$java_file")
    
    # Create AI prompt based on class type
    if [[ "$class_type" == "controller" ]]; then
        test_type="Spring Boot Controller"
        test_annotations="@WebMvcTest"
        additional_context="Use MockMvc for HTTP testing. Mock all service dependencies with @MockBean."
    elif [[ "$class_type" == "service" ]]; then
        test_type="Service"
        test_annotations="@ExtendWith(MockitoExtension.class)"
        additional_context="Mock all repository dependencies. Focus on business logic testing."
    elif [[ "$class_type" == "repository" ]]; then
        test_type="Repository"
        test_annotations="@ExtendWith(MockitoExtension.class)"
        additional_context="Mock the repository interface. Test query methods."
    else
        test_type="Model/Entity"
        test_annotations="@ExtendWith(MockitoExtension.class)"
        additional_context="Test getters, setters, and business logic methods."
    fi
    
    # Create the AI prompt
    prompt="You are an expert Java developer. Generate a comprehensive JUnit 5 test class for the following $test_type.

REQUIREMENTS:
- Use JUnit 5 with $test_annotations
- Use Mockito for mocking
- Include proper imports
- Add @DisplayName annotations
- Create realistic test scenarios
- Mock all external dependencies
- $additional_context

SOURCE CODE TO TEST:
\`\`\`java
$source_code
\`\`\`

Generate ONLY the complete test class code, no explanations. The test class should be named ${class_name}Test and be in package $package."

    # Call Ollama to generate the test
    test_content=$(curl -s http://localhost:11434/api/generate -d "{
        \"model\": \"llama3.2\",
        \"prompt\": \"$prompt\",
        \"stream\": false,
        \"options\": {
            \"temperature\": 0.3,
            \"top_p\": 0.9
        }
    }" | jq -r '.response')
    
    # Clean up the response (remove markdown if present)
    test_content=$(echo "$test_content" | sed 's/```java//g' | sed 's/```//g')
    
    # Determine output path
    relative_path=$(echo "$java_file" | sed 's|src/main/java/||')
    test_file_path="src/test/java/$(dirname "$relative_path")/${class_name}Test.java"
    
    # Create directory and write test file
    mkdir -p "$(dirname "$test_file_path")"
    echo "$test_content" > "$test_file_path"
    
    echo "✅ Generated test: $test_file_path"
}

# Wait for Ollama to be ready
echo "⏳ Waiting for Ollama to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
        echo "✅ Ollama is ready!"
        break
    fi
    echo "Waiting... ($i/30)"
    sleep 2
done

# Check if Ollama is responding
if ! curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
    echo "❌ Ollama is not responding. Falling back to template generation."
    exit 1
fi

# Find and process all Java files
echo "🔍 Scanning for Java source files..."

find src/main/java -name "*.java" -not -name "*Application.java" | while read -r java_file; do
    echo "📝 Processing: $java_file"
    
    # Extract class information
    class_name=$(basename "$java_file" .java)
    package=$(grep -m1 "^package " "$java_file" | sed 's/package //; s/;//')
    
    # Determine class type based on annotations and package
    if grep -q "@Controller\|@RestController" "$java_file"; then
        class_type="controller"
    elif grep -q "@Service" "$java_file"; then
        class_type="service"
    elif grep -q "@Repository" "$java_file"; then
        class_type="repository"
    elif [[ "$java_file" == *"/model/"* ]]; then
        class_type="model"
    else
        class_type="service"  # default
    fi
    
    # Check if test already exists
    test_file="src/test/java/${java_file#src/main/java/}"
    test_file="${test_file%.java}Test.java"
    
    if [[ -f "$test_file" ]]; then
        echo "⏭️  Test already exists: $test_file"
        continue
    fi
    
    # Generate test with AI
    generate_test_with_ai "$java_file" "$class_name" "$package" "$class_type"
done

echo "🎉 AI-powered test generation completed!"