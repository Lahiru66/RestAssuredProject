/*
 * Copyright (c) 2025 Lahiru Kasun
 * Licensed under the MIT License – see the LICENSE file for details.
 */

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import service.Helper;
import dto.TestDTO;
import utils.CommonUtils;
import utils.StatusCodes;
import org.testng.annotations.Listeners;

import java.util.List;

import static validations.CommonValidations.verifyResponseCode;
import static utils.Constants.*;

@Listeners(io.qameta.allure.testng.AllureTestNg.class)
public class PostsTest extends TestBase {
    private static final Logger generalLogger = LogManager.getLogger(PostsTest.class);
    private static final Logger listenerLogger = LogManager.getLogger("ListenerLogger");
    public Helper helper;
    public StatusCodes statusCodes;
    public static SoftAssert softAssert;
    public static Response response;
    private static String baseUrl;
    private static String apiKey;

    private static String env;

    // Static block for watermark in logs
    static {
        generalLogger.info("🚀 API Automation Framework by Lahiru Kasun - 2025");
        listenerLogger.info("🚀 API Automation Framework by Lahiru Kasun - 2025");
    }

    @BeforeClass(alwaysRun = true)
    @Parameters("env")
    public void serviceSetup(@Optional("dev") String environment) {

        // Use the provided parameter or fall back to the system property
        env = environment != null && !environment.isEmpty() ? environment : System.getProperty("env", "dev");

        // Load environment-specific configurations
        EnvironmentManager.loadConfig(env);
        baseUrl = EnvironmentManager.getProperty("base.url");
        apiKey = EnvironmentManager.getProperty("api.key");

        System.out.println("Running test in environment: " + env);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        softAssert = new SoftAssert();
        helper = new Helper();
        statusCodes = new StatusCodes();
    }

    @DataProvider(name = "postDataProvider")
    public Object[][] postDataProvider() {
        List<TestDTO> testDataList = EnvironmentManager.loadTestDataList(env, TestDTO[].class);
        return testDataList.stream().map(data -> new Object[]{data}).toArray(Object[][]::new);
    }

    @Test(enabled = true, priority = 1, description = "create posts", groups ={"smoke"})
    public void postRequest() {
        listenerLogger.info("Starting postRequest...");

        // Load static test data
        TestDTO testData = EnvironmentManager.loadTestData(env, TestDTO.class);

        // Generate dynamic data using CommonUtils
        String randomTitle = CommonUtils.generateRandomString();
        String randomBody = CommonUtils.generateRandomString();
        String randomUserId = CommonUtils.generateRandomAlphanumeric(5);

        // Override static data with dynamic values if needed
        String title = testData.getTitle() != null ? testData.getTitle() : randomTitle;
        String body = testData.getBody() != null ? testData.getBody() : randomBody;
        String userId = testData.getUserId() != null ? testData.getUserId() : randomUserId;

        TestDTO testDTO = TestDTO.builder()
                .title(title)
                .body(body)
                .userId(userId)
                .build();

        response = helper.createPost(testDTO);

        // Retrieve the status code from the response
        int statusCode = response.getStatusCode();
        generalLogger.info("status code is: " + statusCode);

        verifyResponseCode(statusCode, statusCodes.SC_CREATED);

        String returnedTitle = response.jsonPath().getString(TITLE);
        String returnedBody = response.jsonPath().getString(BODY);
        String returnedUserId = response.jsonPath().getString(USERID);
        String id = response.jsonPath().getString(ID);

        softAssert.assertEquals(returnedTitle, title);
        softAssert.assertEquals(returnedBody, body);
        softAssert.assertEquals(returnedUserId, userId);
        softAssert.assertNotNull(id, "Id is null");

        softAssert.assertAll();

    }

    @Test(enabled = true, priority = 2, description = "create posts", groups ={"smoke"},dataProvider = "postDataProvider")
    public void DataDrivenPostRequest(TestDTO testData) {
        listenerLogger.info("Starting DataDrivenPostRequest...");

        // Generate dynamic data using CommonUtils
        String randomTitle = CommonUtils.generateRandomString();
        String randomBody = CommonUtils.generateRandomString();
        String randomUserId = CommonUtils.generateRandomAlphanumeric(5);

        // Generate dynamic data if static data is missing
        String title = testData.getTitle() != null ? testData.getTitle() : randomTitle;
        String body = testData.getBody() != null ? testData.getBody() : randomBody;
        String userId = testData.getUserId() != null ? testData.getUserId() : randomUserId;

        // Build the PostDTO object
        TestDTO postDTO = TestDTO.builder()
                .title(title)
                .body(body)
                .userId(userId)
                .build();

        response = helper.createPost(postDTO);

        // Retrieve the status code from the response
        int statusCode = response.getStatusCode();
        generalLogger.info("status code is: " + statusCode);

        verifyResponseCode(statusCode, statusCodes.SC_CREATED);

        String returnedTitle = response.jsonPath().getString(TITLE);
        String returnedBody = response.jsonPath().getString(BODY);
        String returnedUserId = response.jsonPath().getString(USERID);
        String id = response.jsonPath().getString(ID);

        softAssert.assertEquals(returnedTitle, title);
        softAssert.assertEquals(returnedBody, body);
        softAssert.assertEquals(returnedUserId, userId);
        softAssert.assertNotNull(id, "Id is null");

        softAssert.assertAll();

    }

    @Test(enabled = true, priority = 3, description = "Get one post", groups ={"regression"})
    public void getRequest() {
        listenerLogger.info("Starting getRequest...");
        response = helper.getPost();

        // Retrieve the status code from the response
        int statusCode = response.getStatusCode();
        generalLogger.info("status code is: " + statusCode);

        verifyResponseCode(statusCode, statusCodes.SC_OK);

        // Retrieve the response body as a String
        String responseBody = response.getBody().asString();

        // Perform assertions on the response body (for example, checking if it contains certain text)
        softAssert.assertEquals(responseBody.contains("userId"), true, "Response body does not contain 'userId'");
        softAssert.assertEquals(responseBody.contains("id"), true, "Response body does not contain 'id'");
        softAssert.assertEquals(responseBody.contains("title"), true, "Response body does not contain 'title'");
        softAssert.assertEquals(responseBody.contains("body"), true, "Response body does not contain 'body'");

        softAssert.assertAll();

    }
    @Test(enabled = true, priority = 4, description = "Update one post", groups ={"regression"})
    public void putRequest() {
        listenerLogger.info("Starting putRequest...");

        // Load static test data
        TestDTO testData = EnvironmentManager.loadTestData(env, TestDTO.class);

        // Generate dynamic data using CommonUtils
        String randomTitle = CommonUtils.generateRandomString();
        String randomBody = CommonUtils.generateRandomString();
        String randomUserId = CommonUtils.generateRandomAlphanumeric(5);

        // Override static data with dynamic values if needed
        String title = testData.getTitle() != null ? testData.getTitle() : randomTitle;
        String body = testData.getBody() != null ? testData.getBody() : randomBody;
        String userId = testData.getUserId() != null ? testData.getUserId() : randomUserId;

        TestDTO testDTO = TestDTO.builder()
                .title(title)
                .body(body)
                .userId(userId)
                .build();

        response = helper.createPost(testDTO);

//        System.out.println(response);

        // Generate dynamic data using CommonUtils
        randomTitle = CommonUtils.generateRandomString();
        randomBody = CommonUtils.generateRandomString();
        randomUserId = CommonUtils.generateRandomAlphanumeric(5);

        testDTO = TestDTO.builder()
                .title(randomTitle)
                .body(randomBody)
                .userId(randomUserId)
                .build();

        response = helper.updatePost(testDTO);

//        System.out.println(response);

        // Retrieve the status code from the response
        int statusCode = response.getStatusCode();
        generalLogger.info("status code is: " + statusCode);

        verifyResponseCode(statusCode, statusCodes.SC_OK);

        String returnedTitle2 = response.jsonPath().getString(TITLE);
        String returnedBody2 = response.jsonPath().getString(BODY);
        String returnedUserId2 = response.jsonPath().getString(USERID);
        String id2 = response.jsonPath().getString(ID);

        softAssert.assertEquals(returnedTitle2, randomTitle);
        softAssert.assertEquals(returnedBody2, randomBody);
        softAssert.assertEquals(returnedUserId2, randomUserId);
        softAssert.assertNotNull(id2, "Id is null");

        softAssert.assertAll();
    }

    @Test(enabled = true, priority = 5, description = "partial update one post",groups ={"regression"})
    public void patchRequest() {
        listenerLogger.info("Starting patchRequest...");

        // Load static test data
        TestDTO testData = EnvironmentManager.loadTestData(env, TestDTO.class);

        // Generate dynamic data using CommonUtils
        String randomTitle = CommonUtils.generateRandomString();
        String randomBody = CommonUtils.generateRandomString();
        String randomUserId = CommonUtils.generateRandomAlphanumeric(5);

        // Override static data with dynamic values if needed
        String title = testData.getTitle() != null ? testData.getTitle() : randomTitle;
        String body = testData.getBody() != null ? testData.getBody() : randomBody;
        String userId = testData.getUserId() != null ? testData.getUserId() : randomUserId;

        TestDTO testDTO = TestDTO.builder()
                .title(title)
                .body(body)
                .userId(userId)
                .build();

        response = helper.createPost(testDTO);

        System.out.println(response);

        String returnedTitle = response.jsonPath().getString(TITLE);
        String returnedBody = response.jsonPath().getString(BODY);
        String returnedUserId = response.jsonPath().getString(USERID);
        String id = response.jsonPath().getString(ID);

        randomTitle = CommonUtils.generateRandomString();

        testDTO = TestDTO.builder()
                .title(randomTitle)
                .body(returnedBody)
                .userId(returnedUserId)
                .build();

        response = helper.partialUpdatePost(testDTO);

        System.out.println(response);

        // Retrieve the status code from the response
        int statusCode = response.getStatusCode();
        generalLogger.info("status code is: " + statusCode);

        verifyResponseCode(statusCode, statusCodes.SC_OK);

        String returnedTitle2 = response.jsonPath().getString(TITLE);
        String returnedBody2 = response.jsonPath().getString(BODY);
        String returnedUserId2 = response.jsonPath().getString(USERID);
        String id2 = response.jsonPath().getString(ID);

        softAssert.assertEquals(returnedTitle2, randomTitle);
        softAssert.assertNotNull(returnedBody2, "Body is null");
        softAssert.assertNotNull(returnedUserId2, "UserID is null");
        softAssert.assertNotNull(id2, "Id is null");

        softAssert.assertAll();
    }

    @Test(enabled = true, priority = 6, description = "Remove one post", groups ={"smoke"})
    public void deleteRequest() {
        listenerLogger.info("Starting deleteRequest...");

        // Load static test data
        TestDTO testData = EnvironmentManager.loadTestData(env, TestDTO.class);

        // Generate dynamic data using CommonUtils
        String randomTitle = CommonUtils.generateRandomString();
        String randomBody = CommonUtils.generateRandomString();
        String randomUserId = CommonUtils.generateRandomAlphanumeric(5);

        // Override static data with dynamic values if needed
        String title = testData.getTitle() != null ? testData.getTitle() : randomTitle;
        String body = testData.getBody() != null ? testData.getBody() : randomBody;
        String userId = testData.getUserId() != null ? testData.getUserId() : randomUserId;

        TestDTO testDTO = TestDTO.builder()
                .title(title)
                .body(body)
                .userId(userId)
                .build();

        response = helper.createPost(testDTO);

        int statuCode = response.getStatusCode();
        generalLogger.info("status code is: " + statuCode);

        verifyResponseCode(statuCode, statusCodes.SC_CREATED);

        response = helper.deletePost();

        // Retrieve the status code from the response
        int statusCode2 = response.getStatusCode();
        generalLogger.info("status code is: " + statusCode2);

        verifyResponseCode(statusCode2, statusCodes.SC_OK);

    }
    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        response = null;
    }
}