@echo off
echo ========================================
echo  Credit Refinancing BFF - Test Script
echo ========================================
echo.

echo Checking Java installation...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    pause
    exit /b 1
)
echo.

echo Project structure validation:
echo - Checking main application class...
if exist "bff\src\main\java\com\creditrefinancing\bff\CreditRefinancingBffApplication.java" (
    echo   ✓ Main application class exists
) else (
    echo   ✗ Main application class not found
)

echo - Checking controllers...
if exist "bff\src\main\java\com\creditrefinancing\bff\controller\SimulationController.java" (
    echo   ✓ SimulationController exists
) else (
    echo   ✗ SimulationController not found
)

echo - Checking services...
if exist "bff\src\main\java\com\creditrefinancing\bff\service\SimulationService.java" (
    echo   ✓ SimulationService exists
) else (
    echo   ✗ SimulationService not found
)

echo - Checking configuration...
if exist "bff\src\main\resources\application.yml" (
    echo   ✓ Application configuration exists
) else (
    echo   ✗ Application configuration not found
)

echo - Checking tests...
if exist "bff\src\test\java\com\creditrefinancing\bff\controller\SimulationControllerTest.java" (
    echo   ✓ Unit tests exist
) else (
    echo   ✗ Unit tests not found
)

echo.
echo Project structure validation completed!
echo.

echo Instructions to run the application:
echo 1. Install Maven (https://maven.apache.org/install.html)
echo 2. Run: mvn clean install
echo 3. Run: mvn spring-boot:run
echo 4. Access: http://localhost:8080/api/simulation/health
echo 5. Swagger UI: http://localhost:8080/api/swagger-ui.html
echo.

echo Available endpoints:
echo - GET  /api/simulation/health
echo - POST /api/simulation/calculate
echo - GET  /api/simulation/{id}
echo.

pause
