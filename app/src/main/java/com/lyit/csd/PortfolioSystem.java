package com.lyit.csd;

import java.io.IOException;
import java.util.List;

public interface PortfolioSystem {

  /* Notes: the current value of any asset should be calculated based on live value of that asset
     retrieved from an appropriate web API such as Yahoo finance https://www.yahoofinanceapi.com/

     The format of String return values should be decided by the team. Think carefully about the
     information that should be returned and how best to present that information.
  */

  /**
   * Add the specified amount in USD to the total cash funds available within the portfolio system.
   *
   * @param amount the amount of money in USD to add to the system.
   */
  void addFunds(double amount);

  /**
   * Withdraw the specified amount in USD from the total cash funds available within the portfolio
   * management system.
   *
   * @param amount the amount of money in USD to withdraw from the system.
   * @return True if we have successfully withdrawn the funds (sufficient funds are available)
   * otherwise false.
   */
  boolean withdrawFunds(double amount);

  /**
   * Record a purchase of the named asset if available funds >= the total value of the assets (stock
   * or cryptocurrency) being purchased. The price paid should be the real live price of the asset.
   *
   * @param assetSymbol the name of the asset (stock symbol or cryptocurrency) to purchase
   * @param amount      the amount of the asset to purchase
   * @return True if the asset is purchased successfully, otherwise False.
   */
  boolean purchaseAsset(String assetSymbol, double amount) throws IOException, InterruptedException;

  /**
   * Record a sale of the named asset (stock or cryptocurrency) at the current live market value if
   * we hold that asset. The sale price should be the real live price of the asset at the time of
   * sale retrieved from an appropriate web API. The revenue generated from the sale should be added
   * to the total funds available to the user.
   * <p>
   * Business logic: If we hold > 1 units of the specified asset (say 10 units of Microsoft stock
   * MSFT), and the parameter amount is < total units of the stock, we should sell the units that
   * maximise our profit. Remember some of the stock could have been purchased on different dates
   * and therefore have been purchased at different price points.
   *
   * @param assetSymbol the name of the asset (stock symbol or cryptocurrency) to sell
   * @param amount      the amount of the asset to sell
   * @return True if the asset is sold successfully, otherwise false (we may not have that asset in
   * our portfolio)
   */
  boolean sellAsset(String assetSymbol, double amount) throws IOException, InterruptedException;

  /**
   * Returns a list of trending stocks symbols, their current market price and the days gain or loss
   * in price and as a percentage. Yahoo finance provides this information for you.
   *
   * @param region a string country code specifying the region of interest. Examples include US, GB,
   *               FR, DE, HK
   * @return a list of strings each representing trending stock symbols e.g. APPL, TSLA, BARC
   */
  List<String> getTrendingStocks(String region);

  /**
   * Retrieve a set of historic data points for the specified assets.
   *
   * @param assetSymbols a list of strings representing the symbols of the assets for which we need
   *                     to obtain Historic data.
   * @param interval     a String representing the time interval between quotes. Valid values
   *                     include 1m 5m 15m 1d 1wk 1mo
   * @param range        a String representing the time range over which we should obtain historic
   *                     data for the specified assets. Valid values include 1d 5d, 1mo, 3mo, 6mo,
   *                     1y, 5y, max. Where max represents the maximum available duration (lifetime
   *                     of the asset).
   * @return A list of assetQuotes objects.
   */
  List<AssetQuote> getHistoricalData(List<String> assetSymbols, String interval, String range);

  /**
   * Returns summary information on an exchange in the region specified.
   *
   * @param region   a string country code specifying the region of interest. Examples include US,
   *                 GB, FR, DE, HK
   * @param exchange a string specifying the exchange we want information on. Examples include FTSE,
   *                 DOW, DASDAQ, DAX
   * @return a String containing exchange summary information. Data includes at a minimum the
   * exchange name, exchange symbol, previous closing value, opening value, gain/loss since opening.
   * Add any additional data you feel is relevant.
   */
  String getExchangeSummary(String region, String exchange);

  /**
   * Retrieve realtime quote data for the assets within the list assetNames from the online
   * exchange.
   *
   * @param assetNames a list of asset symbols for example, "Bitcoin-USD", "Appl", "TSLA"
   * @return A list of AssetQuote objects. Return an empty list if we have no assets in our
   * portfolio.
   */
  List<AssetQuote> getAssetInformation(List<String> assetNames);

  /**
   * Retrieve the current value of all of the assets in the portfolio based on the current live
   * value of each asset.
   *
   * @return a double representing the value of the portfolio in USD
   */
  double getPortfolioValue();

  /**
   * Returns a formatted string detailing the name, symbol, average purchase price, current value
   * and amount of each asset within the portfolio. The difference in average purchase price and
   * current price should also be displayed in both USD and as a percentage.
   *
   * @return a String containing summary information on the assets in the portfolio.
   */
  String listAllInvestments();

  /**
   * Retrieve a formatted string containing all of the assets within the portfolio of the specified
   * asset type ("stock" or "cryptocurrencies"). String contains the name, symbol, average purchase
   * price, current value and amount of each asset within the portfolio. The difference in average
   * purchase price and current price are displayed in USD and as a percentage.
   *
   * @param assetType a string specifying the asset type. Valid values are "stock" or "crypto"
   * @return a formatted String containing summary of all of the investments within the portfolio.
   * Return an empty string if we have no assets within our portfolio.
   */
  String listPortfolioAssetsByType(String assetType) throws IOException, InterruptedException;

  /**
   * Retrieve a formatted String containing details on all of the assets within the portfolio
   * matching the assetName in full or partially. String contains the name, symbol, average purchase
   * price, current value and amount of each asset within the portfolio. The difference in average
   * purchase price and current price are displayed in USD and as a percentage.
   *
   * @param assetNames a list of Strings containing asset symbols such as "MSFT" or "BTC-USD" or
   *                   full name "Bitcoin USD" or partial string "Bitco"
   * @return A formatted String containing summary information for the assetNames provided in the
   * list. Return an empty string if we have no matching assets.
   */
  String listPortfolioAssetsByName(List<String> assetNames);


  /**
   * Retrieve a formatted String containing summary information for all assets within the portfolio
   * purchased between the dates startTimeStamp and endTimeStamp. Summary information contains the
   * purchase price, current price, difference between the purchase and sale price (in USD and as a
   * percentage).
   * <p>
   * If the several units of the asset have been purchased at different time points between the
   * startTimeStamp and endTimeStamp, list each asset purchase separately by date (oldest to most
   * recent).
   *
   * @param startTimeStamp a UNIX timestamp representing the start range date
   * @param endTimeStamp   a UNIX timestamp representing the end range date
   * @return A formatted String containing summary information for all of the assets purchased
   * between the startTimeStamp and endTimeStamp. Return an empty string if we have no matching
   * assets in our portfolio.
   */
  String listPortfolioPurchasesInRange(long startTimeStamp, long endTimeStamp) throws IOException, InterruptedException;


  /**
   * Retrieve a formatted string containing a summary of all of the assets sales between the dates
   * startTimeStamp and endTimeStamp. Summary information contains the average purchase price for
   * each asset, the sale price and the profit or loss (in USD and as a percentage).
   * <p>
   * If the several units of the asset have been sold at different time points between the
   * startTimeStamp and endTimeStamp, list by date (oldest to most recent) each of those individual
   * sales.
   *
   * @param startTimeStamp a UNIX timestamp representing the start range date
   * @param endTimeStamp   a UNIX timestamp representing the end range date
   * @return A formatted String containing summary information for all of the assets sold between
   * the startTimeStamp and endTimeStamp. Return an empty string if we have no matching assets in
   * our portfolio.
   */
  String listPortfolioSalesInRange(long startTimeStamp, long endTimeStamp);


}
