
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
      throws NullPointerException, JsonProcessingException  {

   
    List<Candle> stockStartEnd;
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    try{
    // if (from.compareTo(to) >= 0) {
    //   throw new RuntimeException();
    

    String url = buildUri(symbol, from, to);
    String stocks = restTemplate.getForObject(url, String.class);
    if (stocks == null){
      return null;
    } 

    TiingoCandle[] stockStartEndArray = mapper.readValue(stocks, TiingoCandle[].class);
//     if (stocksStartToEndDate == null) {
//       int stockLen = 0;
//       return new ArrayList<TiingoCandle>();
// } else {
//       int stockLen = stocksStartToEndDate.length;
//       List<TiingoCandle> stock = Arrays.asList(stocksStartToEndDate);
//       return stock;
// }

    if (stockStartEndArray == null) {
      
      stockStartEnd = Arrays.asList(new TiingoCandle[0]);
      
      
    } else {
      stockStartEnd = Arrays.asList(stockStartEndArray);
    }
    // return stockStartEnd;
  } catch(NullPointerException n) {
    throw n;
  }
    return stockStartEnd;
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call tiingo service.

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uri = String.format("https://api.tiingo.com/tiingo/daily/%s/prices?"
      + "startDate=%s&endDate=%s&token=%s", symbol, startDate, endDate, TOKEN);
      
    return uri;
    
  }

}
