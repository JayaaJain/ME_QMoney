
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

public class PortfolioManagerImpl implements PortfolioManager, StockQuotesService{

  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;
  
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  // @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  //   this.restTemplate = restTemplate;
  // }
  
  PortfolioManagerImpl(StockQuotesService stockQuoteService) {
    this.stockQuotesService = stockQuotesService;
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
  // public String[] getBuySell(PortfolioTrade trade, 
  //     LocalDate endDate) throws JsonProcessingException {
  //   String ticker = trade.getSymbol();
  //   LocalDate startLocalDate = trade.getPurchaseDate();
  //   List<Candle> stockStartEnd = getStockQuote(ticker, startLocalDate, endDate);
  //   Collections.sort(stockStartEnd, (candle1, candle2) -> {
  //     return candle1.getDate().compareTo(candle2.getDate());
  //   });
  //   Candle stockstart = stockStartEnd.get(0);
  //   Candle stocklatest = stockStartEnd.get(stockStartEnd.size() - 1);
    
  //   String[] buysell = {stockstart.getOpen().toString(),
  //       stocklatest.getClose().toString(),
  //       stocklatest.getDate().toString()};
  //   return buysell;
  // }

  // public Double[] getAnnualizedAndTotalReturns(PortfolioTrade trade, LocalDate endDate) throws Exception {
  //   String[] sellbuyprices;
  //   LocalDate start = trade.getPurchaseDate();
  //   String symbol = trade.getSymbol();
  //   try{
  //     sellbuyprices = getBuySell(trade, endDate);
  //   } catch (JsonProcessingException e) {
  //     throw new Exception(
  //       String.format("Error processing stocks of %s from %s to %s",
  //           symbol, start.toString(), endDate.toString()),
  //           e.getCause());
  //     }

  //   Double buyPrice = Double.parseDouble(sellbuyprices[0]);
  //   Double sellPrice = Double.parseDouble(sellbuyprices[1]);
  //   endDate = LocalDate.parse(sellbuyprices[2]);

  //   Double totalReturn = (sellPrice - buyPrice) / buyPrice;
  //   long totaldays = ChronoUnit.DAYS.between(start, endDate);
  //   Double totalyears = (double) (totaldays) / 365;

  //   Double annualizedReturns = Math.pow((1 + totalReturn), (1 / totalyears)) - 1;

  //   return new AnnualizedReturn(symbol, annualizedReturns, totalReturn);
  // }

  // @Override
  // public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
  //     LocalDate endDate) throws JsonProcessingException {
    
    
  //   AnnualizedReturn annualizedReturn;
  //   List<AnnualizedReturn> returns = new ArrayList<AnnualizedReturn>();
  //   for(PortfolioTrade i : portfolioTrades) {
  //     annualizedReturn = getAnnualizedAndTotalReturns(i, endDate);
  //     returns.add(annualizedReturn);

  //   }

  //   Collections.sort(returns,AnnualizedReturn.closingComparator);
  //   return returns;
  // }
  //     String tic = i.getSymbol();
  //     LocalDate startDate = i.getPurchaseDate();
  //     List<Candle> collection = getStockQuote(tic, startDate, endDate);
  //     double buyVal = collection.get(0).getOpen();
  //     int size = collection.size();
  //     double sellVal = collection.get(size - 1).getClose();
  //     double totalReturn = (sellVal - buyVal) / buyVal;
  //     Long totalNumDays = ChronoUnit.DAYS.between(startDate, endDate);
  //     double totalNumYears = totalNumDays * 0.00273973;
  //     double annualizedReturns = (Math.pow((1 + totalReturn),(1 / totalNumYears))) - 1;
  //     returns.add(
  //       new AnnualizedReturn(i.getSymbol(),
  //                 annualizedReturns, totalReturn));
  //   }
  //   Collections.sort(returns,AnnualizedReturn.closingComparator);
  //   return returns;
  // }



  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  






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
      throws JsonProcessingException, JsonProcessingException {
      
    return stockQuotesService.getStockQuote(symbol, from, to); //NPE !!!
  }
  
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws JsonProcessingException {
    List<AnnualizedReturn> returns = new ArrayList<AnnualizedReturn>();
    for(PortfolioTrade i : portfolioTrades) {
      String tic = i.getSymbol()        ;
      LocalDate startDate = i.getPurchaseDate();
      List<Candle> collection = getStockQuote(tic, startDate, endDate); // NPE !!!
      double buyVal = collection.get(0).getOpen();
      int size = collection.size();
      double sellVal = collection.get(size - 1).getClose();
      double totalReturn = (sellVal - buyVal) / buyVal;
      Long totalNumDays = ChronoUnit.DAYS.between(startDate, endDate);
      double totalNumYears = totalNumDays * 0.00273973;
      double annualizedReturns = (Math.pow((1 + totalReturn),(1 / totalNumYears))) - 1;
      returns.add(
        new AnnualizedReturn(i.getSymbol(),
                  annualizedReturns, totalReturn));
    }
    Collections.sort(returns,AnnualizedReturn.closingComparator);
    return returns;




  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.
  
}
}
