package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {
  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Copy the relevant code from #mainReadQuotes to parse the Json into PortfolioTrade list and
  //  Get the latest quotes from TIingo.
  //  Now That you have the list of PortfolioTrade And their data,
  //  With this data, Calculate annualized returns for the stocks provided in the Json
  //  Below are the values to be considered for calculations.
  //  buy_price = open_price on purchase_date and sell_value = close_price on end_date
  //  startDate and endDate are already calculated in module2
  //  using the function you just wrote #calculateAnnualizedReturns
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.
  //  use gralde command like below to test your code
  //  ./gradlew run --args="trades.json 2020-01-01"
  //  ./gradlew run --args="trades.json 2019-07-01"
  //  ./gradlew run --args="trades.json 2019-12-03"
  //  where trades.json is your json file

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    ObjectMapper objectMapper = getObjectMapper();
    List<PortfolioTrade> trades = Arrays
        .asList(objectMapper.readValue(resolveFileFromResources(args[0]), PortfolioTrade[].class));
    List<AnnualizedReturn> returns = new ArrayList<AnnualizedReturn>();
    for (PortfolioTrade i : trades) {
      String tic = i.getSymbol();
      LocalDate startDate = i.getPurchaseDate();
      String endDate = args[1];
      String uri = "https://api.tiingo.com/tiingo/daily/";
      uri = uri.concat(tic + "/prices?startDate=");
      uri = uri.concat(startDate.toString() + "&endDate=");
      uri = uri.concat(endDate + "&token=28f177ecf47f725538b65b1f587c7aea37b1bd34");
      RestTemplate restTemplate = new RestTemplate();
      String res = restTemplate.getForObject(uri, String.class);
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      List<TiingoCandle> collection = mapper.readValue(res,
          new TypeReference<ArrayList<TiingoCandle>>() {});
      double buyVal = collection.get(0).getOpen();
      int size = collection.size();
      double sellVal = collection.get(size - 1).getClose();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
      LocalDate endDates = LocalDate.parse(endDate, formatter);
      AnnualizedReturn toReturn = calculateAnnualizedReturns(endDates, i, buyVal, sellVal);
      returns.add(
          new AnnualizedReturn(toReturn.getSymbol(),
                  toReturn.getAnnualizedReturn(), toReturn.getTotalReturns()));
    }
    Collections.sort(returns,AnnualizedReturn.closingComparator);
    return returns;
  }

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
    
    LocalDate endDate = LocalDate.parse(args[1]);
    ObjectMapper objectMapper = getObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    PortfolioTrade[] portfolioTrades =
        (objectMapper.readValue(resolveFileFromResources(args[0]), PortfolioTrade[].class));
    PortfolioManagerFactory portfolioManagerFactory = new PortfolioManagerFactory();
    //PortfolioManagerFactory stockquoteservicefactory = new PortfolioManagerFactory();
    //StockQuoteServiceFactory stockquoteservicefactory = new StockQuoteServiceFactory();
    PortfolioManager portfolioManager = portfolioManagerFactory.getPortfolioManager("tiingo", restTemplate);
    return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  annualized returns should be calculated in two steps -
  //  1. Calculate totalReturn = (sell_value - buy_value) / buy_value
  //  Store the same as totalReturns
  //  2. calculate extrapolated annualized returns by scaling the same in years span. The formula is
  //  annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //  Store the same as annualized_returns
  //  return the populated list of AnnualizedReturn for all stocks,
  //  Test the same using below specified command. The build should be successful
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade i,  Double buyVal, Double sellVal) {
    String tic = i.getSymbol();
    LocalDate startDate = i.getPurchaseDate();
    double totalReturn = (sellVal - buyVal) / buyVal;
    Long totalNumDays = ChronoUnit.DAYS.between(startDate, endDate);
    double totalNumYears = totalNumDays * 0.00273973;
    double annualizedReturns = (Math.pow((1 + totalReturn),(1 / totalNumYears))) - 1;
    return new AnnualizedReturn(tic, annualizedReturns, totalReturn);
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Read the json file provided in the argument[0]. The file will be avilable in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. parse the json file using ObjectMapper provided with #getObjectMapper,
  // and extract symbols provided in every trade.
  // return the list of all symbols in the same order as provided in json.
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json"
  // Make sure that it prints below String on the console -
  // ["AAPL","MSFT","GOOGL"]
  // Now, run
  // ./gradlew build and make sure that the build passes successfully
  // There can be few unused imports, you will need to fix them to make the build
  // pass.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper mapper = getObjectMapper();
    PortfolioTrade[] trades = mapper.readValue(file, PortfolioTrade[].class);
 
    List<String> symbols = new ArrayList<>();
    for (int i = 0; i < trades.length; i++) {
      symbols.add(trades[i].getSymbol());
    }
    return symbols;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread()
      .getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = 
        "/home/crio-user/workspace/jayajn98-ME_QMONEY/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@59f63e24";
    String functionNameFromTestFileInStackTrace = "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "22";
    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, 
        toStringOfObjectMapper,functionNameFromTestFileInStackTrace, 
        lineNumberFromTestFileInStackTrace });
  }
  
  // TODO: CRIO_TASK_MODULE_REST_API
  //  Copy the relavent code from #mainReadFile to parse the Json into PortfolioTrade list.
  //  Now That you have the list of PortfolioTrade already populated in module#1
  //  For each stock symbol in the portfolio trades,
  //  Call Tiingo api (https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=&endDate=&token=)
  //  with
  //   1. ticker = symbol in portfolio_trade
  //   2. startDate = purchaseDate in portfolio_trade.
  //   3. endDate = args[1]
  //  Use RestTemplate#getForObject in order to call the API,
  //  and deserialize the results in List<Candle>
  //  Note - You may have to register on Tiingo to get the api_token.
  //    Please refer the the module documentation for the steps.
  //  Find out the closing price of the stock on the end_date and
  //  return the list of all symbols in ascending order by its close value on endDate
  //  Test the function using gradle commands below
  //   ./gradlew run --args="trades.json 2020-01-01"
  //   ./gradlew run --args="trades.json 2019-07-01"
  //   ./gradlew run --args="trades.json 2019-12-03"
  //  And make sure that its printing correct results.

  public static List<TotalReturnsDto> mainReadQuotesHelper(String[] args, 
      List<PortfolioTrade> trades) throws IOException, URISyntaxException {
    RestTemplate restmp = new RestTemplate();
    List<TotalReturnsDto> returns = new ArrayList<TotalReturnsDto>();
    for (PortfolioTrade t : trades) {
      String url = "https://api.tiingo.com/tiingo/daily/" + t.getSymbol() + "/prices?startDate=" 
          + t.getPurchaseDate().toString() + "&endDate=" + args[1] 
          + "&token=28f177ecf47f725538b65b1f587c7aea37b1bd34";
      TiingoCandle[] result = restmp.getForObject(url, TiingoCandle[].class);
      if (result != null) {
        returns.add(new TotalReturnsDto(t.getSymbol(), result[result.length - 1].getClose()));
      }
    }
    return returns;
  }

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper mapper = getObjectMapper();
    List<PortfolioTrade> trades = Arrays.asList(mapper.readValue(file, PortfolioTrade[].class));
    
    List<TotalReturnsDto> sortedByValue = mainReadQuotesHelper(args, trades);
    Collections.sort(sortedByValue, TotalReturnsDto.closingComp);
    List<String> stocks = new ArrayList<String>();
    for (TotalReturnsDto tr : sortedByValue) {
      stocks.add(tr.getSymbol());
    }
    return stocks;
  } 
  
  
  
  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadQuotes(args));
    printJsonObject(mainCalculateSingleReturn(args));
  }
}