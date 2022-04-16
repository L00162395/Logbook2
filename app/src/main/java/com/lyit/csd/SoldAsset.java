package com.lyit.csd;

/**
 * SoldAsset class represent transaction with necessary information for the user when he
 * sells and asset.
 */
public class SoldAsset {

  private String assetSymbol;
  private String assetFullName;
  private String assetType;
  private long timeStamp;
  private double avgPurchasePrice;
  private double priceSold;
  private double amount;

  /**
   * SoldAsset constructor
   *
   * @param assetSymbol symbol of asset sold
   * @param assetFullName full name of asset sold
   * @param assetType type of the asset sold
   * @param timeStamp timestamp for the transaction
   * @param avgPurchasePrice average purchase price for assets
   * @param priceSold price for transaction
   * @param amount amount of assets being sold
   */
  public SoldAsset(String assetSymbol, String assetFullName, String assetType, long timeStamp,
      double avgPurchasePrice, double priceSold, double amount) {
    this.assetSymbol = assetSymbol;
    this.assetFullName = assetFullName;
    this.assetType = assetType;
    this.timeStamp = timeStamp;
    this.avgPurchasePrice = avgPurchasePrice;
    this.priceSold = priceSold;
    this.amount = amount;
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

  public double getAvgPurchasePrice() {
    return avgPurchasePrice;
  }

  public double getPriceSold() {
    return priceSold;
  }

  public double getAmount() {
    return amount;
  }


}
