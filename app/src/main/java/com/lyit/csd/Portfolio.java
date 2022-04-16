package com.lyit.csd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Current class holds all assets
 */
public class Portfolio {

    private HashMap<String, String> symbolFullName = new HashMap<>();
    private HashMap<String, String> assetsInPortfolio = new HashMap<>();
    private List<String> allAssetNames = new ArrayList<>();
    private List<Asset> crypto = new ArrayList<>();
    private List<Asset> stock = new ArrayList<>();
    private List<SoldAsset> soldAssets = new ArrayList<>();



    /**
     * Constructor to instantiate new Portfolio object.
     * Pre-populated assets as per requirements.
     * Asset names are added to the relevant lists.
     * The appropriate asset's symbol and type is stored in the created HashMap.
     */
    public Portfolio() {

        // Pre-populated stocks
        Asset tesla = new Asset("TSLA", "Tesla, Inc.", "EQUITY", 1633107600, 775.22, 10);
        stock.add(tesla);

        Asset apple = new Asset("AAPL", "Apple Inc.", "EQUITY", 1625504400, 139.96, 20);
        stock.add(apple);

        Asset nVidia = new Asset("NVDA", "NVIDIA Corporation", "EQUITY", 1618419600, 152.77, 12);
        stock.add(nVidia);

        // Pre-populated crypto
        Asset bitCoin = new Asset("BTC-USD", "Bitcoin USD", "CRYPTOCURRENCY", 1612893600, 44854.95, 0.0445881);
        crypto.add(bitCoin);

        // Adding asset full names to the relevant lists
        allAssetNames.add(tesla.getAssetFullName());
        allAssetNames.add(apple.getAssetFullName());
        allAssetNames.add(nVidia.getAssetFullName());
        allAssetNames.add(bitCoin.getAssetFullName());

        // Adding relevant asset symbol and type to the hashmap
        assetsInPortfolio.put(tesla.getAssetSymbol(), tesla.getAssetType());
        assetsInPortfolio.put(apple.getAssetSymbol(), apple.getAssetType());
        assetsInPortfolio.put(nVidia.getAssetSymbol(), nVidia.getAssetType());
        assetsInPortfolio.put(bitCoin.getAssetSymbol(), bitCoin.getAssetType());

        // Adding full name and symbol to the hashmap
        symbolFullName.put(tesla.getAssetFullName(), tesla.getAssetSymbol());
        symbolFullName.put(apple.getAssetFullName(), apple.getAssetSymbol());
        symbolFullName.put(nVidia.getAssetFullName(), nVidia.getAssetSymbol());
        symbolFullName.put(bitCoin.getAssetFullName(), bitCoin.getAssetSymbol());
    }


    /**
     * Returns the list of cryptos from the user's portfolio.
     *
     * @return a list of assets that are type crypto.
     */
    public List<Asset> getCrypto() { return crypto;}


    /**
     * Returns the list of stocks from the user's portfolio.
     *
     * @return a list of assets that are type stock.
     */
    public List<Asset> getStock() { return stock;}


    /**
     * Returns the list of asset names from the user's portfolio.
     *
     * @return the list of assets names.
     */
    public List<String> getAllAssetNames() { return allAssetNames;}


    /**
     * Returns the hashmap with the asset symbols and types from the user's portfolio.
     *
     * @return the hashmap with asset info.
     */
    public HashMap<String, String> getAssetsInPortfolio() { return assetsInPortfolio;}


    /**
     * Method to find right portfolio list for specified asset symbol.
     *
     * @param assetSymbol asset symbol
     * @return right portfolio list.
     */
    public List<Asset> findPortfolioListType(String assetSymbol) {
        String rightPortfolio = assetsInPortfolio.get(assetSymbol);

        if(rightPortfolio.equals("CRYPTOCURRENCY"))
            return crypto;

        return stock;
    }

    public HashMap<String, String> getSymbolFullName() {
        return symbolFullName;
    }

    public List<SoldAsset> getSoldAssets() {
        return soldAssets;
    }
}
