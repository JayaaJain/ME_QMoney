package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, NullPointerException {
    try {
      String response = this.restTemplate.getForObject(buildUri(symbol), String.class);
      if (response == null) {
        return null;
      }
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());    
      AlphavantageDailyResponse alphavantageDailyResponse = objectMapper.readValue(response,
          new TypeReference<AlphavantageDailyResponse>(){});
      Map<LocalDate, AlphavantageCandle> map = alphavantageDailyResponse.getCandles();
      List<AlphavantageCandle> alphavantageCandle = new ArrayList<>();
      for (LocalDate date = from; date.isBefore(to) || date.isEqual(to); date = date.plusDays(1)) {
        if (map.containsKey(date)) {
          AlphavantageCandle temp = map.get(date);
          temp.setDate(date);
          alphavantageCandle.add(temp);
        }
      }

      List<Candle> candle = new ArrayList<>(alphavantageCandle);
      return candle;
    } catch (NullPointerException e) {
      throw e;
    }
  }

  public Comparator<AlphavantageCandle> getComparator() {
    return Comparator.comparing(AlphavantageCandle::getDate);
  }

  protected String buildUri(String symbol) {
    String uriTemplate = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED"
            + "&symbol=" + symbol + "&output=full&apikey=BWOP3W5TZLVW7GEJ";
    return uriTemplate;      
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Inplement the StockQuoteService interface as per the contracts.
  //  The implementation of this functions will be doing following tasks
  //  1. Build the appropriate url to communicate with thirdparty.
  //  The url should consider startDate and endDate if it is supported by the provider.
  //  2. Perform thirdparty communication with the Url prepared in step#1
  //  3. Map the response and convert the same to List<Candle>
  //  4. If the provider does not support startDate and endDate, then the implementation
  //  should also filter the dates based on startDate and endDate.
  //  Make sure that result contains the records for for startDate and endDate after filtering.
  //  5. return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  Call alphavantage service to fetch daily adjusted data for last 20 years. Refer to
  //  documentation here - https://www.alphavantage.co/documentation/
  //  Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  Run the tests using command below and make sure it passes
  //  ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  //TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call alphavantage service. Method should
  // be using configurations provided in the {@link @application.properties}
  // Use thie method in #getStockQuote.
}
