package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws StockQuoteServiceException {
    try { 
      String response = this.restTemplate.getForObject(buildUri(symbol, from, to), String.class);
      String invalidTic = "{\"detail\":\"Error: Ticker '" + symbol + "' not found\"}";
      if (response == null) {
        throw new StockQuoteServiceException("Response is Null");
      } else if (invalidTic.equals(response)) {
        throw new StockQuoteServiceException("Ticker" + symbol + "does not exist");
      }
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      List<TiingoCandle> tiingoCandle = objectMapper.readValue(response,
           new TypeReference<List<TiingoCandle>>() {});
      List<Candle> candle = new ArrayList<Candle>(tiingoCandle);
      return candle;         
    } catch (Exception e) {
      throw new StockQuoteServiceException("Error", e);
    }
  }

  public Comparator<TiingoCandle> getComparator() {
    return Comparator.comparing(TiingoCandle::getDate);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uri = "https://api.tiingo.com/tiingo/daily/";
    uri = uri.concat(symbol + "/prices?startDate=");
    uri = uri.concat(startDate.toString() + "&endDate=");
    uri = uri.concat(endDate + "&token=28f177ecf47f725538b65b1f587c7aea37b1bd34");
    return uri;
  }
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Now we will be separating communication with Tiingo from PortfolioManager.
  //  Generate the functions as per the declarations in the interface and then
  //  Move the code from PortfolioManagerImpl#getSTockQuotes inside newly created method.
  //  Run the tests using command below -
  //  ./gradlew test --tests TiingoServiceTest and make sure it passes.
  //CHECKSTYLE:OFF
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call tiingo service.





  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
