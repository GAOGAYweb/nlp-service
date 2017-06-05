package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class NPLUtil {

    private static final String SENTIMENT_ANALYSIS_URI = "http://api.bosonnlp.com/sentiment/analysis";
    private static final String KEYWORDS_ANALYSIS_URI = "http://api.bosonnlp.com/keywords/analysis?top_k=2";
    private static final String X_TOKEN = "hDjdlor6.15666.91dsiViRub7P";

    /**
     * This method send a post request to SENTIMENT_ANALYSIS_URI with a JSONified list of text and
     * return the respond a List of of [positive, negative] pair (a List of 2 Double)
     *
     * @param data list of text
     * @return a List of [positive, negative] pair
     * @throws IOException if the post request went wrong
     */
    @SuppressWarnings("unchecked")
    public static List<List<Double>> sentimentAnalysis(List<String> data) throws IOException {
        return sendPost(SENTIMENT_ANALYSIS_URI, data, new TypeToken<List<List<Double>>>() {
        }.getType());
    }

    /**
     * This method send a post request to KEYWORDS_ANALYSIS_URI with a JSONified list of text and
     * return the respond a List of keywords (a List of [weight, keyword] pair)
     *
     * @param data list of text
     * @return a List of keywords
     * @throws IOException if the post request went wrong
     */
    @SuppressWarnings("unchecked")
    public static List<List<List>> keywordsAnalysis(List<String> data) throws IOException {
        return sendPost(KEYWORDS_ANALYSIS_URI, data, new TypeToken<List<List<List>>>() {
        }.getType());
    }

    /**
     * This method send a post request to KEYWORDS_ANALYSIS_URI with a JSONified list of text and
     * return the respond a List of keywords (a List of keyword String)
     *
     * @param data      list of text
     * @param threshold a Double from [0-1), the bigger it is, the lesser result you'll get
     * @return a List of keywords
     * @throws IOException if the post request went wrong
     */
    public static List<List<String>> keywords(List<String> data, Double threshold) throws IOException {
        return keywordsAnalysis(data).stream()
                .map(list -> list.stream()
                        .filter(l -> (Double) l.get(0) > threshold)
                        .map(l -> (String) l.get(1))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private static List sendPost(String uri, List<String> data, Type type) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        System.out.println("analysis: " + json);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(uri);
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Accept", "application/json");
            post.addHeader("X-Token", X_TOKEN);
            StringEntity entity = new StringEntity(json, "utf-8");
            post.setEntity(entity);
            return handleResponse(httpClient.execute(post), type);
        }
    }

    private static List parse(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    private static List handleResponse(final HttpResponse response, Type type) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }
        return parse(EntityUtils.toString(entity), type);
    }
}