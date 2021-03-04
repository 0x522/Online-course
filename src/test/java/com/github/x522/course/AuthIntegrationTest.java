package com.github.x522.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.x522.course.model.Session;
import com.github.x522.course.model.User;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*告诉junit5执行spring测试*/
@ExtendWith(SpringExtension.class)
/*设置启动类,容器端口*/
@SpringBootTest(classes = CourseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class AuthIntegrationTest {
    @Value("${spring.datasource.url}")
    String dbUrl;
    @Value("${spring.datasource.username}")
    String dbUsername;
    @Value("${spring.datasource.password}")
    String dbPassword;

    @Autowired
    Environment environment;

    private ObjectMapper objectMapper = new ObjectMapper();

    private HttpClient client = HttpClient.newHttpClient();

    public String getPort() {
        return environment.getProperty("local.server.port");
    }

    @BeforeEach /*每次测试前执行*/
    void resetDB() {
        ClassicConfiguration configuration = new ClassicConfiguration();
        configuration.setDataSource(dbUrl, dbUsername, dbPassword);
        Flyway flyway = new Flyway(configuration);
        flyway.clean();
        flyway.migrate();
    }

    //post
    private HttpResponse<String> post(String path, String accept, String contentType, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + getPort() + "/api/v1" + path))
                .header("Accept", accept)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    //get
    private HttpResponse<String> get(String path, String cookie) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Cookie", cookie)
                .uri(URI.create("http://localhost:" + getPort() + "/api/v1" + path))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    //delete
    private HttpResponse<String> delete(String path, String cookie) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Cookie", cookie)
                .uri(URI.create("http://localhost:" + getPort() + "/api/v1" + path))
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void registerLoginLogout() throws IOException, InterruptedException {
        /**
         * 1.注册用户
         * 2.用该用户进行登录
         * 3.确定该用户已经登录成功
         * 4.调用注销接口
         * 5.确定该用户已经登出
         */
        String body = "username=zhangsan&password=123456";
        HttpResponse<String> response = post("/user", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE, body);
        User responseUser = objectMapper.readValue(response.body(), User.class);

        assertEquals(201, response.statusCode());
        assertEquals("zhangsan", responseUser.getUsername());
        Assertions.assertNull(responseUser.getEncryptedPassword());

        response = post("/session", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE, body);
        responseUser = objectMapper.readValue(response.body(), User.class);

        //http response中会有set-cookie作为下一次登录的凭证
        String cookie = response.headers().firstValue("Set-Cookie").get();

        Assertions.assertNotNull(cookie);
        System.out.println(cookie);
        assertEquals(200, response.statusCode());
        assertEquals("zhangsan", responseUser.getUsername());
        Assertions.assertNull(responseUser.getEncryptedPassword());

        //带着Cookie再次登录
        response = get("/session", cookie);
        assertEquals(200, response.statusCode());
        Session session = objectMapper.readValue(response.body(), Session.class);
        assertEquals("zhangsan", session.getUser().getUsername());

        //注销
        response = delete("/session", cookie);
        assertEquals(204, response.statusCode());


        // 再次尝试访问用户的登录状态
        // 确定该用户已经登出
        response = get("/session", cookie);
        assertEquals(401, response.statusCode());
    }

    @Test
    void getErrorIfUsernameAlreadyRegistered() throws IOException, InterruptedException {
        /**
         * 1.注册用户，成功
         * 2.再次使用同名用户注册，失败
         */
        String body = "username=zhangsan&password=123456";
        HttpResponse<String> response = post("/user", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE, body);
        Assertions.assertEquals(201, response.statusCode());
        response = post("/user", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE, body);
        Assertions.assertEquals(409, response.statusCode());

    }

    @Test
    void onlyAdminCanSeeAllUsers() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/admin/users", "COURSE_APP_SESSION_ID=admin_user_cookie");
        assertEquals(200, response.statusCode());
    }

    @Test
    void nonAdminCanNotSeeAllUsers() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/admin/users", "COURSE_APP_SESSION_ID=student_user_cookie");
        assertEquals(403, response.statusCode());
    }

}
