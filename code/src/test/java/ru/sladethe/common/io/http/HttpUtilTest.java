package ru.sladethe.common.io.http;

import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.*;
import ru.sladethe.common.io.*;
import ru.sladethe.common.lang.ThreadUtil;
import ru.sladethe.common.math.NumberUtil;
import ru.sladethe.common.math.RandomUtil;
import ru.sladethe.common.text.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 */
@SuppressWarnings({"MessageMissingOnJUnitAssertion", "CallToPrintStackTrace"})
public class HttpUtilTest {
    private static final String BASE_TESTING_URL = "http://127.0.0.1:8081";

    private static final int CONCURRENCY_LEVEL = 20;
    private static final int REQUEST_COUNT = 500;

    private static final int DEFAULT_RESPONSE_SIZE = 1024;
    private static final int LARGE_RESPONSE_SIZE = 100000;

    private static final String POST_DATA = "Trololo Трололо №\"!?#@'`/\\,.()&^%$*<> ёыъьяю ™šœ "
            + RandomUtil.getRandomAlphanumeric(LARGE_RESPONSE_SIZE);

    private static final boolean VERBOSE = false;

    private final NanoHTTPD server = new HttpRequestTestServer();

    @Before
    public void setUp() throws Exception {
        server.start();
    }

    @After
    public void tearDown() {
        server.stop();
    }

    private static byte[] doGet(String s) throws IOException {
        URL url = new URL(s);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        byte[] result = IOUtils.toByteArray(inputStream);
        inputStream.close();
        connection.disconnect();
        return result;
    }

    @Test
    public void manyConcurrentGets() throws InterruptedException {
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger count = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENCY_LEVEL);

        long startTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < REQUEST_COUNT; ++i) {
            pool.submit(() -> {
                try {
                    byte[] bytes = HttpUtil.executeGetRequestAndReturnResponse(
                            100000, BASE_TESTING_URL, "size", LARGE_RESPONSE_SIZE
                    ).getBytes();

                    Assert.assertEquals(LARGE_RESPONSE_SIZE, ArrayUtils.getLength(bytes));

                    if (VERBOSE) {
                        println("HttpUtilTest.testManyConcurrentGets: done " + count.incrementAndGet());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    exceptions.add(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(1L, TimeUnit.DAYS);

        if (!exceptions.isEmpty()) {
            throw new RuntimeException("exceptions.size() = " + exceptions.size());
        }

        printf("Done 'HttpUtilTest.testManyConcurrentGets' in %d ms.%n", System.currentTimeMillis() - startTimeMillis);
    }

    @Test
    public void manyConcurrentPosts() throws InterruptedException {
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger count = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENCY_LEVEL);

        long startTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < REQUEST_COUNT; ++i) {
            pool.submit(() -> {
                try {
                    byte[] bytes = HttpUtil.executePostRequestAndReturnResponse(
                            100000, BASE_TESTING_URL, "size", LARGE_RESPONSE_SIZE
                    ).getBytes();

                    Assert.assertEquals(LARGE_RESPONSE_SIZE, ArrayUtils.getLength(bytes));

                    if (VERBOSE) {
                        println("HttpUtilTest.testManyConcurrentPosts: done " + count.incrementAndGet());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    exceptions.add(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(1L, TimeUnit.DAYS);

        if (!exceptions.isEmpty()) {
            throw new RuntimeException("exceptions.size() = " + exceptions.size());
        }

        printf("Done 'HttpUtilTest.testManyConcurrentPosts' in %d ms.%n", System.currentTimeMillis() - startTimeMillis);
    }

    @Test
    public void manyConcurrentDoGets() throws InterruptedException {
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger count = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENCY_LEVEL);

        long startTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < REQUEST_COUNT; ++i) {
            pool.submit(() -> {
                try {
                    byte[] bytes = doGet(BASE_TESTING_URL + "?size=" + LARGE_RESPONSE_SIZE);

                    Assert.assertEquals(LARGE_RESPONSE_SIZE, bytes.length);

                    if (VERBOSE) {
                        println("HttpUtilTest.testManyConcurrentDoGets: done " + count.incrementAndGet());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    exceptions.add(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(1L, TimeUnit.DAYS);

        if (!exceptions.isEmpty()) {
            throw new RuntimeException("exceptions.size() = " + exceptions.size());
        }

        printf("Done 'HttpUtilTest.testManyConcurrentDoGets' in %d ms.%n", System.currentTimeMillis() - startTimeMillis);
    }

    @Test
    public void manyNotTimedOutPosts() throws InterruptedException {
        int concurrency = 5;
        int requestCount = 5 * concurrency;

        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger count = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(concurrency);

        long startTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < requestCount; ++i) {
            pool.submit(() -> {
                try {
                    HttpResponse response = HttpUtil.executePostRequestAndReturnResponse(
                            1500, BASE_TESTING_URL + "?delay=1000"
                    );

                    Assert.assertEquals(
                            getIllegalResponseLengthMessage(response, DEFAULT_RESPONSE_SIZE),
                            DEFAULT_RESPONSE_SIZE, ArrayUtils.getLength(response.getBytes())
                    );

                    if (VERBOSE) {
                        println("HttpUtilTest.testManyNotTimedOutPosts: done " + count.incrementAndGet());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    exceptions.add(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(1L, TimeUnit.DAYS);

        if (!exceptions.isEmpty()) {
            throw new RuntimeException("exceptions.size() = " + exceptions.size(), exceptions.get(0));
        }

        printf("Done 'HttpUtilTest.testManyNotTimedOutPosts' in %d ms.%n", System.currentTimeMillis() - startTimeMillis);
    }

    @Test
    public void manyTimedOutPosts() throws InterruptedException {
        int concurrency = 5;
        int requestCount = 5 * concurrency;

        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger count = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(concurrency);

        long startTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < requestCount; ++i) {
            pool.submit(() -> {
                try {
                    HttpResponse response = HttpUtil.executePostRequestAndReturnResponse(
                            950, BASE_TESTING_URL + "?delay=1000"
                    );

                    Assert.assertEquals(
                            getIllegalResponseLengthMessage(response, DEFAULT_RESPONSE_SIZE),
                            DEFAULT_RESPONSE_SIZE, ArrayUtils.getLength(response.getBytes())
                    );

                    if (VERBOSE) {
                        println("HttpUtilTest.testManyTimedOutPosts: done " + count.incrementAndGet());
                    }
                } catch (Throwable e) {
                    exceptions.add(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(1L, TimeUnit.DAYS);

        if (exceptions.size() != requestCount) {
            throw new RuntimeException("exceptions.size() = " + exceptions.size());
        }

        printf("Done 'HttpUtilTest.testManyTimedOutPosts' in %d ms.%n", System.currentTimeMillis() - startTimeMillis);
    }

    @Test
    public void timedOutPost() {
        long startTimeMillis = System.currentTimeMillis();

        HttpUtil.executePostRequestAndReturnResponse(1000, BASE_TESTING_URL + "?delay=1000000");

        Assert.assertTrue(
                "Response with 1000 ms timeout takes more than 2050 ms.",
                (System.currentTimeMillis() - startTimeMillis) < 2050
        );

        println("Done 'HttpUtilTest.testTimedOutPost' in " + (System.currentTimeMillis() - startTimeMillis) + " ms.");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void postWithBinaryEntity() {
        long startTimeMillis = System.currentTimeMillis();

        HttpResponse response = HttpRequest.create(BASE_TESTING_URL)
                .setMethod(HttpMethod.POST)
                .setBinaryEntity(POST_DATA.getBytes(StandardCharsets.UTF_8))
                .setTimeoutMillis(20000)
                .executeAndReturnResponse();

        response.getHeadersByNameMap();

        Assert.assertEquals(
                getIllegalResponseLengthMessage(response, DEFAULT_RESPONSE_SIZE),
                DEFAULT_RESPONSE_SIZE, ArrayUtils.getLength(response.getBytes())
        );

        Assert.assertEquals(String.format(
                "Got unexpected response code %d.", response.getCode()
        ), HttpCode.OK, response.getCode());

        printf("Done 'HttpUtilTest.testPostWithBinaryEntity' in %d ms.%n", System.currentTimeMillis() - startTimeMillis);
    }

    /* TODO */
    @Ignore
    @Test
    public void postWithGzippedBinaryEntity() {
        long startTimeMillis = System.currentTimeMillis();

        HttpResponse response = HttpRequest.create(BASE_TESTING_URL)
                .setMethod(HttpMethod.POST)
                .setBinaryEntity(POST_DATA.getBytes(StandardCharsets.UTF_8))
                .setTimeoutMillis(20000)
                .setGzip(true)
                .executeAndReturnResponse();

        Assert.assertEquals(
                getIllegalResponseLengthMessage(response, DEFAULT_RESPONSE_SIZE),
                DEFAULT_RESPONSE_SIZE, ArrayUtils.getLength(response.getBytes())
        );

        Assert.assertEquals(String.format(
                "Got unexpected response code %d.", response.getCode()
        ), HttpCode.OK, response.getCode());

        printf(
                "Done 'HttpUtilTest.testPostWithGzippedBinaryEntity' in %d ms.%n",
                System.currentTimeMillis() - startTimeMillis
        );
    }

    /* TODO */
    @Ignore
    @Test
    public void postWithGzippedParameters() {
        long startTimeMillis = System.currentTimeMillis();

        HttpResponse response = HttpRequest.create(BASE_TESTING_URL)
                .setMethod(HttpMethod.POST)
                .appendParameter("size", LARGE_RESPONSE_SIZE)
                .setTimeoutMillis(20000)
                .setGzip(true)
                .executeAndReturnResponse();

        Assert.assertEquals(
                getIllegalResponseLengthMessage(response, LARGE_RESPONSE_SIZE),
                LARGE_RESPONSE_SIZE, ArrayUtils.getLength(response.getBytes())
        );

        Assert.assertEquals(String.format(
                "Got unexpected response code %d.", response.getCode()
        ), HttpCode.OK, response.getCode());

        printf(
                "Done 'HttpUtilTest.testPostWithGzippedParameters' in %d ms.%n",
                System.currentTimeMillis() - startTimeMillis
        );
    }

    @Test
    public void postWithoutReadingResponse() {
        Assert.assertEquals(HttpCode.OK, HttpRequest.create(BASE_TESTING_URL).setMethod(HttpMethod.POST).execute());
    }

    private static String getIllegalResponseLengthMessage(HttpResponse response, int expectedLength) {
        return String.format("Expected response length: %d. %s.", expectedLength, response);
    }

    private static void println(String line) {
        System.out.println(line);
        System.out.flush();
    }

    private static void printf(String format, Object... arguments) {
        System.out.printf(format, arguments);
        System.out.flush();
    }

    private static final class HttpRequestTestServer extends NanoHTTPD {
        private static final int TRUE_RANDOM_PART_LENGTH = 50;

        private final String randomString1024 = getRandomString(DEFAULT_RESPONSE_SIZE - 2 * TRUE_RANDOM_PART_LENGTH);
        private final String randomString100000 = getRandomString(LARGE_RESPONSE_SIZE - 2 * TRUE_RANDOM_PART_LENGTH);

        private HttpRequestTestServer() {
            super(8081);
        }

        @SuppressWarnings({"RefusedBequest", "OverlyLongMethod"})
        @Override
        public Response serve(IHTTPSession session) {
            Map<String, String> files = new HashMap<>();

            @Nullable Response response = parseRequest(session, files);
            if (response != null) {
                return response;
            }

            Map<String, String> parameterValueByName = new HashMap<>(session.getParms());

            response = validatePostDataAndUpdateParameters(session, files, parameterValueByName);
            if (response != null) {
                return response;
            }

            String delayString = parameterValueByName.get("delay");
            if (delayString != null) {
                ThreadUtil.sleep(NumberUtil.toInt(delayString));
            }

            String sizeString = parameterValueByName.get("size");
            int size = sizeString == null ? DEFAULT_RESPONSE_SIZE : NumberUtil.toInt(sizeString);

            String randomPrefixPart = getRandomString(TRUE_RANDOM_PART_LENGTH);
            String randomPostfixPart = getRandomString(TRUE_RANDOM_PART_LENGTH);

            String responseBody;
            if (size == DEFAULT_RESPONSE_SIZE) {
                responseBody = randomPrefixPart + randomString1024 + randomPostfixPart;
            } else if (size == LARGE_RESPONSE_SIZE) {
                responseBody = randomPrefixPart + randomString100000 + randomPostfixPart;
            } else {
                throw new IllegalArgumentException(String.format("Unsupported size %d.", size));
            }

            return new Response(Response.Status.OK, MimeType.TEXT_PLAIN, responseBody);
        }

        /**
         * Parses HTTP request.
         *
         * @param session all-in-one HTTP session and request
         * @param files   map to store parsed file data
         * @return {@code null} if succeeded and {@code {@link Response Response}} in case of some error
         */
        @Nullable
        private static Response parseRequest(IHTTPSession session, Map<String, String> files) {
            Map<String, String> headerValueByName = session.getHeaders();

            if (session.getMethod() == Method.PUT || session.getMethod() == Method.POST) {
                if ("gzip".equalsIgnoreCase(headerValueByName.get("Content-Encoding".toLowerCase()))) {
                    try {
                        String contentLengthString = headerValueByName.get("Content-Length".toLowerCase());
                        InputStream inputStream = session.getInputStream();
                        ByteArrayOutputStream outputStream;

                        if (StringUtil.isBlank(contentLengthString)) {
                            outputStream = new ByteArrayOutputStream();
                            IOUtils.copy(inputStream, outputStream, NumberUtil.toInt(FileUtil.BYTES_PER_GB));
                            outputStream.close();
                        } else {
                            int contentLength = NumberUtil.toInt(contentLengthString);
                            outputStream = new LimitedByteArrayOutputStream(contentLength, true);
                            IOUtils.copy(inputStream, outputStream, contentLength);
                            outputStream.close();
                        }

                        byte[] bytes = outputStream.toByteArray();
                        GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
                        bytes = IOUtils.toByteArray(gzipInputStream);
                        gzipInputStream.close();

                        files.put("postData", new String(bytes, StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        return new Response(
                                Response.Status.INTERNAL_ERROR, MimeType.TEXT_PLAIN, ExceptionUtils.getStackTrace(e)
                        );
                    }
                } else {
                    try {
                        session.parseBody(files);
                    } catch (IOException e) {
                        return new Response(
                                Response.Status.INTERNAL_ERROR, MimeType.TEXT_PLAIN, ExceptionUtils.getStackTrace(e)
                        );
                    } catch (ResponseException e) {
                        return new Response(e.getStatus(), MimeType.TEXT_PLAIN, ExceptionUtils.getStackTrace(e));
                    }
                }
            }

            return null;
        }

        @Nullable
        private static Response validatePostDataAndUpdateParameters(
                IHTTPSession session, Map<String, String> files, Map<String, String> parameterValueByName) {
            String postData = files.get("postData");
            if (postData == null) {
                return null;
            }

            Map<String, String> headerValueByName = session.getHeaders();
            String contentType = headerValueByName.get("Content-Type".toLowerCase());

            if (MimeType.APPLICATION_OCTET_STREAM.equalsIgnoreCase(contentType)) {
                if (!POST_DATA.equals(postData)) {
                    return new Response(
                            Response.Status.BAD_REQUEST, MimeType.TEXT_PLAIN, "Received illegal POST data."
                    );
                }
            } else if (MimeType.APPLICATION_X_WWW_FORM_URLENCODED.equalsIgnoreCase(contentType)) {
                for (String postParameter : StringUtil.split(postData, '&')) {
                    if (StringUtil.isBlank(postParameter)) {
                        continue;
                    }

                    String[] postParameterParts = StringUtil.split(postParameter, '=');
                    if (postParameterParts.length != 2) {
                        continue;
                    }

                    String parameterName = postParameterParts[0];

                    if (parameterValueByName.containsKey(parameterName)) {
                        return new Response(
                                Response.Status.BAD_REQUEST, MimeType.TEXT_PLAIN,
                                "Received duplicate parameter '" + parameterName + "'."
                        );
                    }

                    parameterValueByName.put(parameterName, postParameterParts[1]);
                }
            }

            return null;
        }

        @Nonnull
        private static String getRandomString(int length) {
            return RandomUtil.getRandomAlphanumeric(length);
        }
    }
}