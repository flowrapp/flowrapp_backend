#!/bin/bash

# Native build script for FlowrApp
echo "🚀 Building GraalVM Native Image for FlowrApp"
echo "================================================"

# Check if GraalVM is available
if ! command -v native-image &> /dev/null; then
    echo "❌ GraalVM native-image not found!"
    echo "Please install GraalVM and add native-image to your PATH"
    echo "Or use Docker build with: docker build -f Dockerfile.native -t flowrapp-native ."
    exit 1
fi

# Check Java version
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
echo "☕ Using Java version: $java_version"

# Check available memory
if [[ "$OSTYPE" == "darwin"* ]]; then
    memory_mb=$(( $(sysctl -n hw.memsize) / 1024 / 1024 ))
    echo "💾 Available memory: ${memory_mb}MB"
    if [ $memory_mb -lt 8192 ]; then
        echo "⚠️  Warning: Native compilation requires at least 8GB RAM"
        echo "   Your system has ${memory_mb}MB. Build may fail or be very slow."
    fi
fi

# Change to code directory
cd code

echo "📦 Building with Maven native profile..."
echo "This may take 3-5 minutes and use significant memory."

# Set Maven options for native compilation
export MAVEN_OPTS="-Xmx8g -XX:+UseG1GC"

# Build the native image  
if command -v mvn &> /dev/null; then
    mvn clean package -Pnative -DskipTests -B
    build_success=$?
else
    echo "❌ Maven not found! Please install Maven or use Docker build."
    exit 1
fi

if [ $build_success -eq 0 ]; then
    echo ""
    echo "✅ Native build successful!"
    echo "📍 Native executable: $(pwd)/boot/target/flowrapp-native"
    
    # Check file size
    if [ -f "boot/target/flowrapp-native" ]; then
        size=$(du -h boot/target/flowrapp-native | cut -f1)
        echo "📏 Executable size: $size"
        
        echo ""
        echo "🧪 Testing native executable..."
        echo "Starting application (Ctrl+C to stop)..."
        
        # Test the native executable
        cd boot/target
        SPRING_PROFILES_ACTIVE=local ./flowrapp-native &
        app_pid=$!
        
        # Wait a moment for startup
        sleep 2
        
        # Check if it's running
        if kill -0 $app_pid 2>/dev/null; then
            echo "✅ Native application started successfully!"
            echo "🌐 Try accessing: http://localhost:8080/flowrapp/actuator/health"
            echo ""
            echo "Press Enter to stop the application..."
            read
            kill $app_pid 2>/dev/null
        else
            echo "❌ Native application failed to start"
        fi
    fi
else
    echo ""
    echo "❌ Native build failed!"
    echo "This might be due to:"
    echo "  1. Missing GraalVM native-image"
    echo "  2. Insufficient memory (needs 8GB+)"
    echo "  3. Missing native configuration hints"
    echo ""
    echo "Try building with Docker instead:"
    echo "  docker build -f Dockerfile.native -t flowrapp-native ."
    exit 1
fi

echo ""
echo "🎉 Native build process completed!"
