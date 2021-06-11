package backend;

import backend.util.*;
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
import org.openqa.selenium.support.pagefactory.ByAll;
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
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        ChromeDriver driver = new ChromeDriver();
        //can use strategy pattern to optimise readability
        if (updateDriver(driver)) {
            driver = new ChromeDriver();
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
        Properties properties = new Properties();
        try (InputStream configIs = LoginApi.class.getClassLoader().getResourceAsStream("app.properties")) {
            //properties file must exist
            if (configIs != null) {
                properties.load(configIs);
                String driverVersion = properties.getProperty("driverVersion");
                if (driverVersion != null && driverVersion.equals(driver.getCapabilities().getVersion())) {
                    return false;
                } else {
                    //update driver file
                    if (driverVersion == null
                            || driverVersion.length() == 0
                            || Integer.parseInt(driverVersion) < Integer.parseInt(chromeVersion)) {
                        driver.quit();
                        try (OutputStream configOs = new FileOutputStream(new File(LoginApi.class.getClassLoader().getResource("app.properties").toURI()))) {
                            log.debug("driverVersion is {}", driverVersion);
                            updateProperties(chromeVersion, properties, configOs);
                            return true;
                        }
                    }
                }
            } else {
                throw new Exception("should have config file: app.properties");
            }
        } catch (Exception e) {
            log.error("cannot load config file", e);
            System.exit(1);
        }
        return false;
    }

    private static void updateProperties(String chromeVersion, Properties properties, OutputStream configOs) throws IOException {
        log.info("no driver version info or version is too old");
        log.info("chrome driver should be updated");
        downloadDriver(chromeVersion);
        properties.put("driverVersion", chromeVersion);
        properties.store(configOs, null);
    }

    private static void downloadDriver(String chromeVersion) {
        String driverVersionStrUrl = "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_" + chromeVersion;
        log.info("url to get detail driver version {}", driverVersionStrUrl);
        Request request = new Request.Builder()
                .url(driverVersionStrUrl).get().build();
        try (Response response = HttpClient.getClient().newCall(request).execute()) {
            String detailDriverVersion = Objects.requireNonNull(response.body()).string();
            log.info("detail version is {}", detailDriverVersion);
            String driverDownloadAddress = "https://chromedriver.storage.googleapis.com/"
                    + detailDriverVersion + "/chromedriver_linux64.zip";
            DownloadUtil.download(driverDownloadAddress, new File("chromedriver.zip"));
            UnzipUtil.unzip("chromedriver.zip", "chromedriver");
        } catch (Exception e) {
            log.error("can't download browser driver", e);
            System.exit(1);
        }
        log.debug("chrome driver has been downloaded");
    }

    private static void inputUserNameAndPassword(ChromeDriver driver) {
        driver.get(loginUrl);
        log.debug("url: {}", loginUrl);
        WebElement loginStyle = driver.findElement(By.className("type-tab")).findElements(By.tagName("span")).get(0);
        loginStyle.click();

        InputStream configInputStream = LoginApi.class.getClassLoader().getResourceAsStream("app.properties");
        if (configInputStream != null) {
            log.debug("config exist");
            Properties prop = new Properties();
            try {
                prop.load(configInputStream);
                String username = prop.getProperty("username");
                log.debug("username is {}", username);
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
        } else {
            log.debug("no config file");
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

