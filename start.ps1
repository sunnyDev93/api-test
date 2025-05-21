# Set environment variables for API urls and keys
$env:OPENWEATHERMAP_API_KEY = "dd595e6a832a69e1e5d926182d263e49"
$env:NEWSAPI_API_KEY = "31f1a66c5bbe4b5c8603a97ef3d1d0c0"
$env:OPENWEATHERMAP_URL = "https://api.openweathermap.org/data/2.5/weather"
$env:RESTCOUNTRIES_URL = "https://restcountries.com/v3.1/alpha"
$env:NEWSAPI_URL = "https://newsapi.org/v2/top-headlines"

Write-Host "Building the project..."
mvn clean package

if ($LASTEXITCODE -ne 0) {
    Write-Error "Build failed. Exiting."
    exit $LASTEXITCODE
}

Write-Host "Starting the application..."

java -jar target\cityinfo-0.0.1-SNAPSHOT.jar
