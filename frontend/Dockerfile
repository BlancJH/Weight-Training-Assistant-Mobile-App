# Use a Node.js image for Flutter web
FROM cirrusci/flutter:latest

# Set the working directory inside the container
WORKDIR /app

# Copy the Flutter app code
COPY . .

# Install dependencies
RUN flutter pub get

# Build the Flutter app
RUN flutter build web

# Expose the Flutter app's web server port (if using web)
EXPOSE 8080

# Serve the built Flutter web app (if required)
CMD ["flutter", "run", "--release"]
