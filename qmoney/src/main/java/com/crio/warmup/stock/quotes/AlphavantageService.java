
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService {//implements StockQuotesService {

//   private RestTemplate restTemplate;

//   protected AlphavantageService(RestTemplate restTemplate) {
//     this.restTemplate = restTemplate;
//   }

//   // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
//   // Inplement the StockQuoteService interface as per the contracts.
//   // The implementation of this functions will be doing following tasks
//   // 1. Build the appropriate url to communicate with thirdparty.
//   // The url should consider startDate and endDate if it is supported by the
//   // provider.
//   // 2. Perform thirdparty communication with the Url prepared in step#1
//   // 3. Map the response and convert the same to List<Candle>
//   // 4. If the provider does not support startDate and endDate, then the
//   // implementation
//   // should also filter the dates based on startDate and endDate.
//   // Make sure that result contains the records for for startDate and endDate
//   // after filtering.
//   // 5. return a sorted List<Candle> sorted ascending based on Candle#getDate
//   // Call alphavantage service to fetch daily adjusted data for last 20 years.
//   // Refer to
//   // documentation here - https://www.alphavantage.co/documentation/
//   // Make sure you use {RestTemplate#getForObject(URI, String)} else the test will
//   // fail.
//   // Run the tests using command below and make sure it passes
//   // ./gradlew test --tests AlphavantageServiceTest
//   // CHECKSTYLE:OFF
//   // CHECKSTYLE:ON
//   // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
//   // Write a method to create appropriate url to call alphavantage service. Method
//   // should
//   // be using configurations provided in the {@link @application.properties}.
//   // Use thie method in #getStockQuote.
//   @Override
//   public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
//       throws JsonProcessingException {

//     // List<Candle> stockStartEnd;
//     String url = buildUri(symbol);
    
//     String apiresp = restTemplate.getForObject(url, String.class);
    
//     ObjectMapper mapper = new ObjectMapper();
//     mapper.registerModule(new JavaTimeModule());

//     // if (from.compareTo(to) >= 0) {
//     // throw new RuntimeException();
//     // }



//     Map<LocalDate, AlphavantageCandle> dailyresponse = mapper.readValue(apiresp, AlphavantageDailyResponse.class)
//         .getCandles();

//     List<Candle> stocks = getRequiredCandle(dailyresponse, from, to);

//     return stocks;
//   }

//   public static List<Candle> getRequiredCandle(Map<LocalDate, AlphavantageCandle> dailyresponse, LocalDate from,
//       LocalDate to) {

//     List<Candle> stocks = new ArrayList<>();

//     for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
//       AlphavantageCandle candle = dailyresponse.get(date);

//       if (candle != null) {
//         candle.setDate(date);
//         stocks.add(candle);
//       }
//     }
//     return stocks;
//   }

//   public static List<Candle> getStocksWithLocalDateAdded(Map<LocalDate, AlphavantageCandle> candles) {
//     List<Candle> stocks = new ArrayList<>();
//     for (Map.Entry<LocalDate, AlphavantageCandle> response : candles.entrySet()) {
//       response.getValue().setDate(response.getKey());
//       stocks.add(response.getValue());
//     }
//     return stocks;
//   }

//   protected String buildUri(String symbol) {
//     String uri = String.format("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s" 
//       + "&output=full&apikey=BWOP3W5TZLVW7GEJ", symbol);
//     return uri;
//   }

 }
