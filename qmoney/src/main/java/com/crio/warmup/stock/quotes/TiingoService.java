
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  public static final String TOKEN = "28f177ecf47f725538b65b1f587c7aea37b1bd34";

  private RestTemplate restTemplate;

  public TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Now we will be separating communication with Tiingo from PortfolioManager.
  //  Generate the functions as per the declarations in the interface and then
  //  Move the code from PortfolioManagerImpl#getSTockQuotes inside newly created method.
  //  Run the tests using command below -
  //  ./gradlew test --tests TiingoServiceTest and make sure it passes.
  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, NullPointerException {
    try {
      String response = this.restTemplate.getForObject(buildUri(symbol, from, to), String.class);
      if (response == null) {
        return null;
      }
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      List<TiingoCandle> tiingoCandle = objectMapper.readValue(response,
           new TypeReference<List<TiingoCandle>>() {});
      List<Candle> candle = new ArrayList<Candle>(tiingoCandle);
      return candle;
    } catch (NullPointerException e) {
      throw e;
    }
  }

    // if (from.compareTo(to) >= 0) {
    //   throw new RuntimeException();
      
//     if (stocksStartToEndDate == null) {
//       int stockLen = 0;
//       return new ArrayList<TiingoCandle>();
// } else {
//       int stockLen = stocksStartToEndDate.length;
//       List<TiingoCandle> stock = Arrays.asList(stocksStartToEndDate);
//       return stock;
// }

      
      // return stockStartEnd;
    

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call tiingo service.

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uri = String.format("https://api.tiingo.com/tiingo/daily/%s/prices?"
      + "startDate=%s&endDate=%s&token=%s", symbol, startDate, endDate, TOKEN);
      
    return uri;
    
  }

}
