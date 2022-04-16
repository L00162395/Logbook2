package com.lyit.csd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


/**
 * ConnectionPort class represents connection between user and  Yahoo finance api.
 */
public class ConnectionPort {

  private String request;
  private String key;
  private List<AssetQuote> assetQuote;


  /**
   * Constructor to instantiate ConnectionPort object.
   *
   * @param request String value with address.
   * @param key     String value with user api key.
   */
  public ConnectionPort(String request, String key) throws IOException, InterruptedException {
    this.request = request;
    this.key = key;
    assetQuote = new ArrayList<>();
    makeRequest(request);
  }


  /**
   * Default constructor.
   */
  public ConnectionPort() {
  }


  /**
   * Method makes request to access data. After response is received it will map required
   * information the AssetQuote class instance fields.
   *
   * @param requestString address with information.
   * @throws IOException          thrown if wrong data is entered.
   * @throws InterruptedException thrown when a thread is interrupted while it's waiting, sleeping,
   *                              or otherwise occupied.
   */
  private void makeRequest(String requestString) throws IOException, InterruptedException {

    // Code reference - yahoo finance api tutorial. Line 55 - 61
    HttpRequest userRequest = HttpRequest.newBuilder()
        .uri(URI.create("https://yfapi.net/" + requestString))
        .header("x-api-key", key)
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(userRequest, HttpResponse.BodyHandlers.ofString());

    // call mapping method
    mapRequiredFields(response.body());
  }

  /**
   * Method to map chosen values to AssetQuote object fields.
   *
   * @param responseBody api response body JSON formatted string
   */
  private void mapRequiredFields(String responseBody) throws IOException, InterruptedException {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    JsonNode node = objectMapper.readTree(responseBody);

    if (request.contains("v6/finance/quote")) {
      if (node.has("quoteResponse") && node.get("quoteResponse").has("result")) {
        node = node.get("quoteResponse").get("result");
        for (int i = 0; i < node.size(); i++) {
          String object = node.get(i).toString();
          try {
            AssetQuote quote = objectMapper.readValue(object, AssetQuote.class);
            assetQuote.add(quote);
          } catch (JsonProcessingException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }


  /**
   * Method to get access to our asset quotes.
   *
   * @return Information object
   */
  public List<AssetQuote> getAssetQuote() {
    return assetQuote;
  }
}
