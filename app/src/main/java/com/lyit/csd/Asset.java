package com.lyit.csd;

/**
 * Current class represent one asset.
 */
public class Asset {

    private String assetSymbol;
    private String assetFullName;
    private String assetType;
    private long timeStamp;
    private double priceBought;
    private double amount;


    /**
     * Constructor to instantiate new Asset object.
     *
     * @param assetSymbol asset symbol.
     * @param assetFullName asset full name.
     * @param assetType type of the asset.
     * @param timeStamp asset timestamp.
     * @param priceBought price for asset at the moment of purchase
     * @param amount amount of asset.
     */
    public Asset(String assetSymbol, String assetFullName, String assetType, long timeStamp,
                 double priceBought, double amount) {
        this.assetSymbol = assetSymbol;
        this.assetFullName = assetFullName;
        this.assetType = assetType;
        this.timeStamp = timeStamp;
        this.priceBought = priceBought;
        this.amount = amount;
    }


    /**
     * Default constructor.
     */
    public Asset() {
    }


    /**
     * Returning asset symbol.
     *
     * @return the relevant asset's symbol.
     */
    public String getAssetSymbol() {
        return assetSymbol;
    }


    /**
     * Returning asset full name.
     *
     * @return the full name of the relevant asset.
     */
    public String getAssetFullName() { return assetFullName; }


    /**
     * Returning asset type.
     *
     * @return the type of the relevant asset.
     */
    public String getAssetType() { return assetType; }


    /**
     * Returning the time stamp.
     *
     * @return the timestamp of the relevant asset's initial quoted value.
     */
    public long getTimeStamp() { return timeStamp; }


    /**
     * Returning the price brought.
     *
     * @return the price the relevant asset was brought on.
     */
    public double getPriceBought() { return priceBought; }


    /**
     * Returning the amount.
     *
     * @return the amount of the relevant asset.
     */
    public double getAmount() { return amount; }


    /**
     * Setting the amount.
     *
     * @param amount: the number of the relevant asset.
     */
    public void setAmount(double amount) { this.amount = amount; }


    /**
     * The toString method of the asset.
     *
     * @return the stored details of the relevant asset.
     */
    @Override
    public String toString() {
        return "Asset: " +
                "assetSymbol='" + assetSymbol + '\'' +
                ", assetFullName='" + assetFullName + '\'' +
                ", assetType='" + assetType + '\'' +
                ", timeStamp=" + timeStamp +
                ", priceBought=" + priceBought +
                ", amount=" + amount +
                '}';
    }
}