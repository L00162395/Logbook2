package com.lyit.csd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetQuote {

  /* You should implement relevant methods for the class. You can add additional attributes to
   * store additional information on each asset if you wish. Carefully consider the information that
   * you can retrieve from the finance API that you use and what information the user would like to
   * view or may find useful */

   /**
   * The symbol of the asset e.g. APPL, TSLA, BARC or BTC-USD
   */

  @JsonProperty("symbol")
  private String assetSymbol;

  /**
   * The full name of the asset e.g. Apple, Tesla, Barclays PLC, Bitcoin USD
   */

  @JsonProperty("shortName")
  private String assetFullName;

  /**
   * The type of the asset. e.g. Crypto
   */

  @JsonProperty("quoteType")
  private String assetType;

  /**
   * The UNIX timestamp of the asset's quoted value. Using long instead of int to avoid the year
   * 2038 problem.
   */

  @JsonProperty("regularMarketTime")
  private long timeStamp;

  /**
   * The value in USD of the named asset at this point in time.
   */

  @JsonProperty("regularMarketPrice")
  private double livePrice;


  /**
   * Constructor to instantiate AssetQuote object.
   *
   * @param assetSymbol symbol of the asset.
   * @param assetFullName full name of the asset.
   * @param assetType type of the asset.
   * @param timeStamp UNIX timestamp
   * @param livePrice current price in USD on the market
   */


  public AssetQuote(String assetSymbol, String assetFullName, String assetType, long timeStamp,
                    double livePrice) {
    this.assetSymbol = assetSymbol;
    this.assetFullName = assetFullName;
    this.assetType = assetType;
    this.timeStamp = timeStamp;
    this.livePrice = livePrice;
  }

  /**
   * Default constructor.
   */
  public AssetQuote() {
  }


  public String getAssetSymbol() {
    return assetSymbol;
  }


  public String getAssetFullName() {
    return assetFullName;
  }


  public String getAssetType() {
    return assetType;
  }


  public long getTimeStamp() {
    return timeStamp;
  }


  public double getLivePrice() {
    return livePrice;
  }



}
