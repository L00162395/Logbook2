package com.lyit.csd;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public class User implements PortfolioSystem {

  private String keyApi;
  private double availableFunds;
  private Portfolio userPortfolio;

  /**
    * Constructor to instantiate a User.
    *
    * @param keyApi the Api Key used to the user connect to Yahoo Finance API
    * and retrieve information.
    */
  public User(String keyApi) {
    this.keyApi = keyApi;
    availableFunds = 10_000;
    userPortfolio = new Portfolio();
  }


  /**
    * Add the specified amount in USD to the total cash funds available within
    * the portfolio system.
    *
    * @param amount the amount of money in USD to add to the system.
    */
  @Override
  public void addFunds(double amount) {
    if (amount > 0) {
      availableFunds += amount;
    }
  }

  @Override
  public boolean withdrawFunds(double amount) {
    return availableFunds - amount >= 0;
  }


  /**
   * Record a purchase of the named asset if available funds >= the total value of the assets
   * (stock or cryptocurrency) being purchased. The price paid should be the real live price of
   * the asset.
   *
   * @param assetSymbol the name of the asset (stock symbol or cryptocurrency) to purchase
   * @param amount      the amount of the asset to purchase
   * @return True if the asset is purchased successfully, otherwise False.
   */
  @Override
  public boolean purchaseAsset(String assetSymbol, double amount)
        throws IOException, InterruptedException {

    // if amount is negative or zero
    if (amount <= 0) {
      return false;
    }

    // make connection to get asset quote from yahoo finance api
    String requestString = "v6/finance/quote?region=US&lang=en&symbols=" + assetSymbol;
    ConnectionPort cp = new ConnectionPort(requestString, keyApi);

    // if requested symbol is not real asset symbol
    if (cp.getAssetQuote().isEmpty()) {
      return false;
    }

    // if we have enough funds to purchase asset
    double transactionCost = cp.getAssetQuote().get(0).getLivePrice() * amount;

    if (withdrawFunds(transactionCost)) {

      //create new asset instance
      Asset newAsset = new Asset(
          cp.getAssetQuote().get(0).getAssetSymbol(),
          cp.getAssetQuote().get(0).getAssetFullName(),
          cp.getAssetQuote().get(0).getAssetType(),
          cp.getAssetQuote().get(0).getTimeStamp(),
          cp.getAssetQuote().get(0).getLivePrice(),
          amount
      );

      //add it to the right portfolio type and pay for asset
      findPortfolioType(newAsset.getAssetType(), newAsset);
      availableFunds -= transactionCost;

      // update our portfolio class with needed information
      if (!userPortfolio.getAssetsInPortfolio().containsKey(assetSymbol)) {
        userPortfolio.getAssetsInPortfolio().put(newAsset.getAssetSymbol(),
            newAsset.getAssetType());
      }

      if (!userPortfolio.getAllAssetNames().contains(newAsset.getAssetFullName())) {
        userPortfolio.getAllAssetNames().add(newAsset.getAssetFullName());
      }
    }

    return true;
  }

  /**
   * Helper method to find proper asset list by type for a new Asset.
   *
   * @param type type of the new Asset
   * @param asset new Asset
   */
  private void findPortfolioType(String type, Asset asset) {

    //check asset type and add it in the right type portfolio list
    switch (type) {
      case "EQUITY" -> userPortfolio.getStock().add(asset);
      case "CRYPTOCURRENCY" -> userPortfolio.getCrypto().add(asset);
    }
  }

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
   */
  @Override
  public boolean sellAsset(String assetSymbol, double amount)
      throws IOException, InterruptedException {

    // check if asset symbol is in portfolio
    if(!userPortfolio.getAssetsInPortfolio().containsKey(assetSymbol))
      return false;

    if(amount <= 0)
      return false;

    // find amount of asset user holds in the portfolio
    double userHoldsAmount = 0;
    double toSell = amount;
    String assetFullName = "";

    //make list with all assets of this symbol
    List<Asset> requestedAssets = new ArrayList<>();
    for (Asset asset : userPortfolio.findPortfolioListType(assetSymbol)) {
      if(asset.getAssetSymbol().equals(assetSymbol)){
        userHoldsAmount += asset.getAmount();
        requestedAssets.add(asset);
      }
    }

    // if user wants to sell more than he have
    if(amount > userHoldsAmount)
      return false;

    // sort list by price (low to high)
    requestedAssets.sort(Comparator.comparing(Asset::getPriceBought));

    // as list is sorted, we can sell assets in right order
    double avgPurchasePrice = 0;
    int assetsCount = 0;
    boolean isStill = false;
    for (Asset asset : requestedAssets){

      avgPurchasePrice += asset.getPriceBought();
      assetsCount++;

      if(toSell - asset.getAmount() >= 0) {
        userPortfolio.findPortfolioListType(assetSymbol).remove(asset);
        toSell -= asset.getAmount();
      } else {
        asset.setAmount(asset.getAmount() - toSell);
        isStill = true;
      }
      if(assetFullName.isEmpty())
        assetFullName = asset.getAssetFullName();
    }

    // make connection to get live price
    String requestString = "v6/finance/quote?region=US&lang=en&symbols=" + assetSymbol;
    ConnectionPort cp = new ConnectionPort(requestString, keyApi);

    // to avoid limit exceeded if user has more than 100 requests per day
    if(cp.getAssetQuote().isEmpty())
      return false;

    // check transaction cost and add it to our funds
    double transactionCost = cp.getAssetQuote().get(0).getLivePrice() * amount;
    availableFunds += transactionCost;

    // add sold asset to the sold asset list
    userPortfolio.getSoldAssets().add(new SoldAsset(
        cp.getAssetQuote().get(0).getAssetSymbol(),
        cp.getAssetQuote().get(0).getAssetFullName(),
        cp.getAssetQuote().get(0).getAssetType(),
        cp.getAssetQuote().get(0).getTimeStamp(),
        avgPurchasePrice / assetsCount,
        cp.getAssetQuote().get(0).getLivePrice(),
        amount
    ));

    // if asset is fully sold we need to remove extra information we hold
    if(!isStill) {
      userPortfolio.getAssetsInPortfolio().remove(assetSymbol);
      userPortfolio.getSymbolFullName().remove(assetSymbol);
      userPortfolio.getAllAssetNames().remove(assetFullName);
    }

    return true;
  }

  /**
   * Returns a list of trending stocks symbols, their current market price and the days gain or loss
   * in price and as a percentage. Yahoo finance provides this information for you.
   *
   * @param region a string country code specifying the region of interest. Examples include US, GB,
   *               FR, DE, HK
   * @return a list of strings each representing trending stock symbols e.g. APPL, TSLA, BARC
   */
  @Override
  public List<String> getTrendingStocks(String region) {
    return null;
  }

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
  @Override
  public List<AssetQuote> getHistoricalData(List<String> assetSymbols, String interval, String range) {
    return null;
  }

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
  @Override
  public String getExchangeSummary(String region, String exchange) {
    return null;
  }

  /**
   * Retrieve realtime quote data for the assets within the list assetNames from the online
   * exchange.
   *
   * @param assetNames a list of asset symbols for example, "Bitcoin-USD", "Appl", "TSLA"
   * @return A list of AssetQuote objects. Return an empty list if we have no assets in our
   * portfolio.
   */
  @Override
  public List<AssetQuote> getAssetInformation(List<String> assetNames) {
    return null;
  }

  /**
   * Retrieve the current value of all of the assets in the portfolio based on the current live
   * value of each asset.
   *
   * @return a double representing the value of the portfolio in USD
   */
  @Override
  public double getPortfolioValue() {
    return 0;
  }

  /**
   * Returns a formatted string detailing the name, symbol, average purchase price, current value
   * and amount of each asset within the portfolio. The difference in average purchase price and
   * current price should also be displayed in both USD and as a percentage.
   *
   * @return a String containing summary information on the assets in the portfolio.
   */
  @Override
  public String listAllInvestments() {

    // Creating StringBuilder to store all investments for return
    StringBuilder result = new StringBuilder();
    // HashMap to help return assets by symbol
    HashMap<String, List<Asset>> sortedPortfolio = getAllSortedPortfolio();

    // getting current information of the user's assets
    List<String> userHoldsSymbol = userPortfolio.getAllAssetNames();
    List<AssetQuote> quotes = getAssetInformation(userHoldsSymbol);

    // creating loop to iterate through the quotes for detailed information
    for (AssetQuote quote : quotes) {
      List<Asset> assetList = sortedPortfolio.get(quote.getAssetSymbol());
      result.append(assetDetailedInfo(assetList, quote.getLivePrice()));
    }


    return result.toString();
  }

 
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
  @Override
  public String listPortfolioAssetsByType(String assetType)
      throws IOException, InterruptedException{

      String result = "";

      if(assetType.equals("CRYPTOCURRENCY")|| assetType.equals("EQUITY")){
          StringBuilder body = new StringBuilder();
          HashMap<String, List<Asset>> orderedPortfolio = new HashMap<>();
          List<Asset> checkPortfolio = new ArrayList<>();
          List<String> symbolsInPortfolio = new ArrayList<>();

        if(assetType.equals("EQUITY"))
            checkPortfolio = userPortfolio.getStock();

        if(assetType.equals("CRYPTOCURRENCY"))
            checkPortfolio = userPortfolio.getCrypto();

        for(Asset asset : checkPortfolio){
            if(!orderedPortfolio.containsKey(asset.getAssetSymbol())){
                ArrayList<Asset> list = new ArrayList<>();
                list.add(asset);
                orderedPortfolio.put(asset.getAssetSymbol(), list);
            }else{
                orderedPortfolio.get(asset.getAssetSymbol()).add(asset);
            }
        }
          double totalPrice;
          double amount;
          List<AssetQuote> quotes = getAssetInformation(symbolsInPortfolio);
          HashMap<String, AssetQuote> quotesGrouped = new HashMap<>();
          for(AssetQuote quote : quotes)
              quotesGrouped.put(quote.getAssetSymbol(), quote);

          for(String assetKey : orderedPortfolio.keySet()) {
              totalPrice = 0;
              amount = 0;
              for (Asset asset : orderedPortfolio.get(assetKey)) {
                  totalPrice += asset.getPriceBought();
                  amount += asset.getAmount();
                  if (orderedPortfolio.get(asset.getAssetSymbol()).indexOf(asset) ==
                          orderedPortfolio.get(asset.getAssetSymbol()).size() - 1) {

                      double avgPurchasePrice = totalPrice /
                              orderedPortfolio.get(asset.getAssetSymbol()).size();
                      avgPurchasePrice = Double.parseDouble(
                              new DecimalFormat(".##").format(avgPurchasePrice));

                      double livePrice = quotesGrouped.get(asset.getAssetSymbol()).getLivePrice();
                      double currentValue = amount *
                              quotesGrouped.get(asset.getAssetSymbol()).getLivePrice();
                      currentValue =  Double.parseDouble(
                              new DecimalFormat(".##").format(currentValue));

                      String ANSI_GREEN = "\u001B[32m";
                      String ANSI_RED = "\u001B[31m";
                      String rightColor = avgPurchasePrice < livePrice ?ANSI_GREEN : ANSI_RED;

                      body.append(rightColor);
                      body.append("Asset Name          :  ").append(asset.getAssetFullName()). append("\n");
                      body.append("Asset Symbol        :  ").append(asset.getAssetSymbol()). append("\n");
                      body.append("Amount in Portfolio :  ").append(amount). append("\n");
                      body.append("Avg Purchase Price  :  ").append(avgPurchasePrice). append("\n");
                      body.append("Asset Live Price:   :  ").append(livePrice). append("\n");
                      body.append("Current Value:      :  ")
                              .append(currentValue).append("\n").append("\n");
                      body.append(rightColor);


                  }
              }
          }
          result = body.toString();

      }


      return result ;
  }


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
  @Override
  public String listPortfolioAssetsByName(List<String> assetNames) {
    return null;
  }

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
  @Override
  public String listPortfolioPurchasesInRange(long startTimeStamp, long endTimeStamp)
          throws IOException, InterruptedException {

    StringBuilder result = new StringBuilder();

    // if start date is in front of the end date
    if(startTimeStamp > endTimeStamp)
      return result.toString();

    HashMap<String, List<Asset>> sortedAllPortfolio = getAllSortedPortfolio();

    // sort map sortedAllPortfolio by purchase timestamp
    for (String symbol : sortedAllPortfolio.keySet()) {
      List<Asset> toSort = sortedAllPortfolio.get(symbol);

      // remove assets not in range
      toSort.removeIf(
              asset -> asset.getTimeStamp() < startTimeStamp || asset.getTimeStamp() > endTimeStamp);

      // sort based on timestamp (low to high)
      toSort.sort(Comparator.comparing(Asset::getTimeStamp));
    }

    // get live information for each asset symbol
    List<AssetQuote> quotes = getAssetInformation(userPortfolio.getAllAssetNames());

    // loop through quotes and print detailed information
    for (AssetQuote quote : quotes) {
      List<Asset> assetList = sortedAllPortfolio.get(quote.getAssetSymbol());
      result.append(assetPurchaseRangeInfo(assetList, quote.getLivePrice()));
    }

    return result.toString();
  }

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
  @Override
  public String listPortfolioSalesInRange(long startTimeStamp, long endTimeStamp) {

    StringBuilder result = new StringBuilder();

    // if start date is in front of the end date
    if(startTimeStamp > endTimeStamp)
      return result.toString();

    HashMap<String, List<SoldAsset>> sortedSales = new HashMap<>();

    // populate sortedSales with related sales for each asset symbol
    for (SoldAsset soldAsset : userPortfolio.getSoldAssets()) {
      String symbol = soldAsset.getAssetSymbol();
      if(!sortedSales.containsKey(symbol)) {
        List<SoldAsset> list = new ArrayList<>();
        list.add(soldAsset);
        sortedSales.put(symbol, list);
      } else {
        sortedSales.get(symbol).add(soldAsset);
      }
    }

    // loop through map and build result string
    for (String symbol : sortedSales.keySet()) {

      // sort based on timestamp (low to high)
      List<SoldAsset> list = sortedSales.get(symbol);
      list.sort(Comparator.comparing(SoldAsset::getTimeStamp));

      // loop through sorted list and add to result string in range sold assets
      for (SoldAsset soldAsset : list) {

        double avgPurchasePrice = soldAsset.getAvgPurchasePrice();
        double soldPrice = soldAsset.getPriceSold();
        double differenceUSD = soldPrice - avgPurchasePrice;
        differenceUSD = Double.parseDouble(
                new DecimalFormat(".##").format(differenceUSD));
        int differencePercentage = (int) (100 * (soldPrice - avgPurchasePrice) / avgPurchasePrice);

        if(soldAsset.getTimeStamp() >= startTimeStamp && soldAsset.getTimeStamp() <= endTimeStamp) {
          result.append("\nAsset Name         : ").append(soldAsset.getAssetFullName());
          result.append("\nAvg Purchase Price : ").append(soldAsset.getAvgPurchasePrice());
          result.append("\nSale price         : ").append(soldAsset.getPriceSold());
          result.append("\nDifference USD     : ").append(differenceUSD);
          result.append("\nDifference %       : ").append(differencePercentage);
          result.append("\n");
        }
      }
    }

    return result.toString();
  }

  public double getAvailableFunds() {
      return availableFunds;
  }


  public Portfolio getUserPortfolio() {
      return userPortfolio;
  }


  // helper private methods


  /**
   * Helper method to check if user portfolio contains symbol or partial asset name (example: Appl).
   * Will check first 3 letters of name to avoid similar asset names.
   *
   * @param names list of asset partial names or full symbols
   * @return list with symbols which user has from names list.
   */
  private List<String> namesToSymbolList(List<String> names) {

    List<String> result = new ArrayList<>();
    // lower cased names list
    List<String> lowerCasedNames = new ArrayList<>();

    // change to lower case to be sure it matches user input
    for (String name : names) {
      lowerCasedNames.add(name.toLowerCase());
    }

    // check if names contains symbol
    for (Entry<String, String> entry : userPortfolio.getAssetsInPortfolio().entrySet()) {
      String symbol = entry.getKey();
      String toCompare = symbol.toLowerCase();
      if(lowerCasedNames.contains(toCompare)){
        result.add(symbol);
        lowerCasedNames.remove(toCompare);
      }
    }

    // if it is name, we need to cut to 3 characters
    for (int i = 0; i < lowerCasedNames.size(); i++) {
      String tempName = lowerCasedNames.get(i).substring(0, 3);
      lowerCasedNames.remove(i);
      lowerCasedNames.add(i, tempName);
    }

    // check if name contains partial name
    for (Entry<String, String> entry : userPortfolio.getSymbolFullName().entrySet()) {
      String key = entry.getKey();
      String compareTo = key.substring(0, 3).toLowerCase();
      String value = entry.getValue();
      if(lowerCasedNames.contains(compareTo)){
        result.add(value);
      }
    }

    return result;
  }

  /**
   * Helper method to get sorted stock assets in our portfolio by asset symbol
   *
   * @return sorted portfolio hash map
   */
  private HashMap<String, List<Asset>> getSortedStockPortfolio() {

    HashMap<String, List<Asset>> result = new HashMap<>();

    // populate result hash map with stock assets
    for(Asset asset : userPortfolio.getStock()) {
      if(!result.containsKey(asset.getAssetSymbol())) {
        List<Asset> newList = new ArrayList<>();
        newList.add(asset);
        result.put(asset.getAssetSymbol(), newList);
      } else {
        result.get(asset.getAssetSymbol()).add(asset);
      }
    }

    return result;
  }

  /**
   * Helper method to get sorted crypto assets in our portfolio by asset symbol
   *
   * @return sorted portfolio hash map
   */
  private HashMap<String, List<Asset>> getSortedCryptoPortfolio() {

    HashMap<String, List<Asset>> result = new HashMap<>();

    // populate result hash map with crypto assets
    for(Asset asset : userPortfolio.getCrypto()) {
      if(!result.containsKey(asset.getAssetSymbol())) {
        List<Asset> newList = new ArrayList<>();
        newList.add(asset);
        result.put(asset.getAssetSymbol(), newList);
      } else {
        result.get(asset.getAssetSymbol()).add(asset);
      }
    }

    return result;
  }

  /**
   * Helper method to get all sorted crypto and stock portfolio by asset symbol
   *
   * @return sorted portfolio hash map
   */
  private HashMap<String, List<Asset>> getAllSortedPortfolio() {

    HashMap<String, List<Asset>> result = new HashMap<>();

    // merge two hash (stock and crypto) together
    result = getSortedStockPortfolio();
    result.putAll(getSortedCryptoPortfolio());

    return result;
  }

  /**
   * Helper method to get detailed information String on all assets of one symbol.
   *
   * @param assets grouped list of same symbol assets
   * @param livePrice live price for this asset symbol
   * @return detailed information about asset.
   */
  private String assetDetailedInfo(List<Asset> assets, double livePrice) {

    String name = "";
    String symbol = "";
    double avgPurchasePrice = 0;
    double amount = 0;
    double differenceUSD = 0;
    int differencePercentage = 0;

    for (Asset asset : assets) {

      if (name.isEmpty()) {
        name = asset.getAssetFullName();
      }
      if (symbol.isEmpty()) {
        symbol = asset.getAssetSymbol();
      }

      avgPurchasePrice += asset.getPriceBought();
      amount += asset.getAmount();
    }

    avgPurchasePrice /= assets.size();
    differenceUSD = livePrice - avgPurchasePrice;
    livePrice = Double.parseDouble(
        new DecimalFormat(".##").format(livePrice));
    differenceUSD = Double.parseDouble(
        new DecimalFormat(".##").format(differenceUSD));
    differencePercentage = (int) (100 * (livePrice - avgPurchasePrice) / avgPurchasePrice);


    return "\nAsset Name     : " + name +
        "\nAsset Symbol   : " + symbol +
        "\nAsset Amount   : " + amount +
        "\nAverage Price  : " + avgPurchasePrice + " USD" +
        "\nLive Price     : " + livePrice + " USD" +
        "\nDifference USD : " + differenceUSD + " USD" +
        "\nDifference %   : " + differencePercentage + "%\n";

  }

  private String assetPurchaseRangeInfo(List<Asset> assets, double livePrice) {

    StringBuilder result = new StringBuilder();

    for (Asset asset : assets) {

      double purchasePrice = asset.getPriceBought();
      double differenceUSD = livePrice - purchasePrice;
      livePrice = Double.parseDouble(
              new DecimalFormat(".##").format(livePrice));
      differenceUSD = Double.parseDouble(
              new DecimalFormat(".##").format(differenceUSD));
      int differencePercentage = (int) (100 * (livePrice - purchasePrice) / purchasePrice);

      result.append("\nAsset Name     : ").append(asset.getAssetFullName());
      result.append("\nPrice Bought   : ").append(asset.getPriceBought());
      result.append("\nLive Price     : ").append(livePrice);
      result.append("\nDifference USD : ").append(differenceUSD);
      result.append("\nDifference %   : ").append(differencePercentage);
      result.append("\n");
    }

    return result.toString();
  }

}
