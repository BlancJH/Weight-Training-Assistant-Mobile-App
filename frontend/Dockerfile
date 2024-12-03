# Use Flutter image
FROM cirrusci/flutter:latest

# Set the working directory inside the container
WORKDIR /app

# Copy all project files into the container
COPY . .

# Install Flutter dependencies
RUN flutter pub get

# Build the Flutter project (for web or specific target platforms)
RUN flutter build web

# Expose the server port (if running as a web app)
EXPOSE 3000

# Command to serve the app (if targeting web)
CMD ["flutter", "run", "--release"]
