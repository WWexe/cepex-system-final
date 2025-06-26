//Observação: Para os testes Selenium acima funcionarem, você precisaria de um servidor web
//rodando seu frontend React na mesma porta que o Spring Boot (ou ajustar o baseUrl para a
//porta do seu frontend, http://localhost:3000 por exemplo). Além disso, os IDs (username
//password, loginButton, error-message, desktop-menu, mobile-menu-toggle) devem
//corresponder aos IDs dos elementos HTML do seu frontend React. Crie arquivos login.html e
//dashboard.html simples em src/main/resources/static para estes testes funcionarem isoladamente
// com o Spring Boot.\

package com.biopark.cepex_system.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Inicia o Spring Boot Application para que o frontend possa se conectar
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeleniumUITest {

    @LocalServerPort
    private int port; // Porta aleatória que o Spring Boot usa

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeAll
    static void setupWebDriverManager() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Executa em modo headless (sem UI visível)
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port; // Assumindo que o frontend React estará em uma porta diferente,
        // mas para este teste, podemos simular acesso ao backend.
        // Idealmente, o Selenium apontaria para a URL do seu frontend React.
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Exemplo: Teste de login (assumindo um formulário HTML simples)
    @Test
    @DisplayName("Should successfully login with valid credentials")
    void testSuccessfulLogin() {
        // Este teste é um mock simples de como seria a interação.
        // Em um cenário real, você apontaria para a URL do seu frontend React
        // e interagiria com os elementos do formulário de login.

        // Simulação de navegação para a página de login
        driver.get(baseUrl + "/login.html"); // URL hipotética da sua página de login
        System.out.println("Navigated to: " + driver.getCurrentUrl());

        // Simulando a entrada de credenciais e clique no botão de login
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        usernameField.sendKeys("admin");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("admin123");

        WebElement loginButton = driver.findElement(By.id("loginButton"));
        loginButton.click();

        // Verificando se o login foi bem-sucedido (ex: redirecionamento para dashboard, presença de elemento)
        // Isso dependerá da sua implementação de frontend.
        // Por exemplo, verificar se a URL mudou para /dashboard ou se um elemento "Bem-vindo, Admin!" apareceu.
        wait.until(ExpectedConditions.urlContains("/dashboard")); // URL hipotética do dashboard
        assertTrue(driver.getCurrentUrl().contains("/dashboard"));
        System.out.println("Login successful, current URL: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Should display error message for invalid login credentials")
    void testInvalidLogin() {
        driver.get(baseUrl + "/login.html");

        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        usernameField.sendKeys("invaliduser");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("invalidpass");

        WebElement loginButton = driver.findElement(By.id("loginButton"));
        loginButton.click();

        // Esperar por uma mensagem de erro (ex: um div com ID "error-message")
        WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("error-message")));
        assertTrue(errorMessage.isDisplayed());
        assertEquals("Invalid username or password.", errorMessage.getText());
        System.out.println("Invalid login, error message: " + errorMessage.getText());
    }

    @Test
    @DisplayName("Should test responsive design for a typical page")
    void testResponsiveDesign() {
        driver.get(baseUrl + "/dashboard.html"); // Página de exemplo

        // Teste para desktop
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1200, 800));
        System.out.println("Testing desktop view (1200x800)");
        // Adicione asserções para elementos que devem estar visíveis ou com certo layout em desktop
        assertTrue(driver.findElement(By.id("desktop-menu")).isDisplayed());
        assertFalse(driver.findElement(By.id("mobile-menu-toggle")).isDisplayed());

        // Teste para tablet
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(768, 1024));
        System.out.println("Testing tablet view (768x1024)");
        // Adicione asserções específicas para tablet
        // Por exemplo, o menu pode mudar para um formato mais compacto
        assertFalse(driver.findElement(By.id("desktop-menu")).isDisplayed());
        assertTrue(driver.findElement(By.id("mobile-menu-toggle")).isDisplayed()); // Toggle button appears

        // Teste para mobile
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667));
        System.out.println("Testing mobile view (375x667)");
        // Adicione asserções específicas para mobile
        assertFalse(driver.findElement(By.id("desktop-menu")).isDisplayed());
        assertTrue(driver.findElement(By.id("mobile-menu-toggle")).isDisplayed());
    }
}
