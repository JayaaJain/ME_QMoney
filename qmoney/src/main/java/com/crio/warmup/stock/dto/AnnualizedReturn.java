package com.crio.warmup.stock.dto;
import java.util.Comparator;
public class AnnualizedReturn {
  private final String symbol;
  private final Double annualizedReturn;
  private final Double totalReturns;
  public AnnualizedReturn(String symbol, Double annualizedReturn, Double totalReturns) {
    this.symbol = symbol;
    this.annualizedReturn = annualizedReturn;
    this.totalReturns = totalReturns;
  }
  public String getSymbol() {
    return symbol;
  }
  public Double getAnnualizedReturn() {
    return annualizedReturn;
  }
  public Double getTotalReturns() {
    return totalReturns;
  }
  public static final Comparator<AnnualizedReturn> closingComparator =
      new Comparator<AnnualizedReturn>() {
    @Override
     public int compare(AnnualizedReturn t01, AnnualizedReturn t02) {
        return (int) (t02.getAnnualizedReturn().compareTo(t01.getAnnualizedReturn()));
    }
  };
}
