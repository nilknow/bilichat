package backend;

import backend.util.DownloadUtil;
import backend.util.FileUtil;
import backend.util.HttpClient;
import com.google.gson.Gson;
import dto.RoomInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class LoginApi {
    public static String csrfToken = "";


    private static final String loginUrl = "https://passport.bilibili.com/login";
    private static final Set<Cookie> cookieSet = new HashSet<>();

    /**
     * login: some api need to login first
     */
    public static void login() {
        ChromeDriver driver = new ChromeDriver();
        //can use strategy pattern to optimise readability
        if (updateDriver(driver)) {
            driver=new ChromeDriver();
        }

        inputUserNameAndPassword(driver);
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
        FileUtil.writeToFile(FileAddress.COOKIE_PATH, cookieToString());
        HttpClient.setClient(cookieClient);
    }

    /**
     * update driver if need
     *
     * @return true if updated, need to restart driver
     */
    private static boolean updateDriver(ChromeDriver driver) {
        String chromeVersion = driver.getCapabilities().getVersion().split("\\.")[0];
        log.info("chrome browser version is {}", chromeVersion);
        driver.quit();

        try (
                InputStream configIs = LoginApi.class.getClassLoader().getResourceAsStream("app.properties");
                OutputStream configOs = new FileOutputStream(new File(LoginApi.class.getClassLoader().getResource("app.properties").toURI()))
        ) {
            if (configIs != null) {
                Properties properties = new Properties();
                properties.load(configIs);
                String driverVersion = properties.getProperty("driverVersion");
                if (driverVersion == null
                        || driverVersion.length() == 0
                        || Integer.parseInt(driverVersion) < Integer.parseInt(chromeVersion)) {
                    log.info("no driver version info or version is too old");
                    log.info("chrome driver should be updated");
                    downloadDriver(chromeVersion);
                    properties.setProperty("driverVersion", chromeVersion);
                    properties.store(configOs, null);
                    return true;
                }
            } else {
                log.info("no config file");
                log.info("chrome driver should be updated");
                downloadDriver(chromeVersion);
            }
        } catch (IOException | URISyntaxException e) {
            log.error("cannot load config file");
            System.exit(1);
        }
        return false;
    }

    private static void downloadDriver(String chromeVersion) {
        String driverVersionStrUrl = "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_" + chromeVersion;
        log.info("url to get detail driver version {}",driverVersionStrUrl);
        Request request = new Request.Builder()
                .url(driverVersionStrUrl).get().build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            String detailDriverVersion = Objects.requireNonNull(response.body()).string();
            log.info("detail version is {}", detailDriverVersion);
            String driverDownloadAddress = "https://chromedriver.storage.googleapis.com/"
                    + detailDriverVersion + "/chromedriver_linux64.zip";
            DownloadUtil.download(driverDownloadAddress, new File("chromedriver"));
        } catch (Exception e) {
            log.error("can't download browser driver",e);
            System.exit(1);
        }
        log.info("chrome driver has been downloaded");
    }

    private static void inputUserNameAndPassword(ChromeDriver driver) {
        driver.get(loginUrl);
        InputStream configInputStream = LoginApi.class.getClassLoader().getResourceAsStream("app.properties");
        if (configInputStream != null) {
            Properties prop = new Properties();
            try {
                prop.load(configInputStream);
                String username = prop.getProperty("username");
                if (username != null && username.length() > 0) {
                    WebElement userName = driver.findElement(By.id("login-username"));
                    userName.sendKeys(username);
                }
                String password = prop.getProperty("password");
                if (password != null && password.length() > 0) {
                    driver.findElement(By.id("login-passwd")).sendKeys(password);
                }
            } catch (IOException e) {
                log.info("cannot read config file app.properties");
                e.printStackTrace();
            }
        }
        driver.findElement(By.id("login-passwd")).click();
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

