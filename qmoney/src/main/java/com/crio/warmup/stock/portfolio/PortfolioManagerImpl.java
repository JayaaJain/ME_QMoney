package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {




  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility

  private RestTemplate restTemplate;

  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  //TODO: CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  //CHECKSTYLE:OFF

  






  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo thirdparty APIs to a separate function.
  //  It should be split into fto parts.
  //  Part#1 - Prepare the Url to call Tiingo based on a template constant,
  //  by replacing the placeholders.
  //  Constant should look like
  //  https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  //  Where ? are replaced with something similar to <ticker> and then actual url produced by
  //  replacing the placeholders with actual parameters.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {

    
    String result = restTemplate.getForObject(buildUri(symbol,from,to), String.class);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
      
    List<Candle> stocklist = mapper.readValue(result,new TypeReference<ArrayList<Candle>>() {});
      
    return stocklist;
  }

  public List<AnnualizedReturn> calculateAnnualizedReturn(
      List<PortfolioTrade> portfolioTrades,LocalDate endDate) throws JsonProcessingException {
    
    
    List<AnnualizedReturn> returns = new ArrayList<AnnualizedReturn>();
    for (PortfolioTrade i : portfolioTrades) {
      String tic = i.getSymbol();
      LocalDate startDate = i.getPurchaseDate();
      
      List<Candle> collecList= getStockQuote(tic,startDate,endDate);
      double buyVal = collecList.get(0).getOpen();
      int size = collecList.size();
      double sellVal = collecList.get(size - 1).getClose();
      // List<TiingoCandle> collecList= getStockQuote(tic,startDate,endDate);
      // double buyVal = collecList[0];
      // double sellVal = collecList[1];
      double totalReturn = (sellVal - buyVal) / buyVal;
      Long totalNumDays = ChronoUnit.DAYS.between(startDate, endDate);
      double totalNumYears = totalNumDays * 0.00273973;
      double annualizedReturns = (Math.pow((1 + totalReturn),(1 / totalNumYears))) - 1;
      
      
      returns.add(new AnnualizedReturn(i.getSymbol(),
      annualizedReturns, totalReturn));
    }
      Collections.sort(returns,AnnualizedReturn.closingComp);
    
      return returns;
  }
          

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?"
            + "startDate=" + startDate + "&endDate=" + endDate + "&token=f02406030d25c75202f2b4fb182cdc68ed22bf90";
    return uriTemplate;
            
  }
  
}



