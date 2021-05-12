package backend;

import backend.util.FileUtil;
import backend.util.HttpClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashSet;
import java.util.Set;

public class LoginApi {
    public static String csrfToken = "";


    private static final String loginUrl = "https://passport.bilibili.com/login";
    private static final Set<Cookie> cookieSet = new HashSet<>();

    /**
     * login: some api need to login first
     */
    public static void login() {
        WebDriver driver = new ChromeDriver();
        driver.get(loginUrl);
        WebElement element = driver.findElement(By.id("login-username"));
        element.sendKeys("maxwangein@gmail.com");
        driver.findElement(By.id("login-passwd")).click();

        //wait until logged in
        WebDriverWait wait = new WebDriverWait(driver, 24 * 60 * 60);
        wait.until(ExpectedConditions.urlToBe("https://passport.bilibili.com/account/security#/home"));

        cookieSet.addAll(driver.manage().getCookies());
        String userAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        for (Cookie cookie : cookieSet) {
            if ("bili_jct".equals(cookie.getName())) {
                csrfToken = cookie.getValue();
                break;
            }
        }
        driver.close();

        OkHttpClient cookieClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request cookieRequest = original.newBuilder()
                            .addHeader("Cookie", cookieToString())
                            .addHeader("user-agent", userAgent)
                            .build();
                    return chain.proceed(cookieRequest);
                }).build();
        FileUtil.writeToFile(FileAddress.COOKIE_PATH,cookieToString());
        HttpClient.setClient(cookieClient);
    }

    /**
     * cookie to string
     */
    private static String cookieToString() {
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookieSet) {
            sb.append(cookie.getName())
                    .append("=")
                    .append(cookie.getValue())
                    .append("; ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}

