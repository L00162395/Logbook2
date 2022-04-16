package com.lyit.csd;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserTest {

  // please input your API key in string key
  String key = "Wr4J5M8opl3mlzbElkxUg3OUZ0np1RcT65McuaC8";
  User user = new User(key);

  @Test
  public void testPurchaseAsset_and_findPortfolioType_helperMethod() throws IOException, InterruptedException {

    // from requirements user have assets when User instance is created
    int cryptoLength = user.getUserPortfolio().getCrypto().size();
    int stockLength = user.getUserPortfolio().getStock().size();
    int assetNamesLength = user.getUserPortfolio().getAllAssetNames().size();
    int assetsPortfolioLength = user.getUserPortfolio().getAssetsInPortfolio().size();

    // test if amount is negative
    user.purchaseAsset("AAPL", -1);
    Assert.assertEquals(user.getUserPortfolio().getStock().size(), stockLength);

    // test asset symbol which is not on stock market
    user.purchaseAsset("LYIT", 20);
    Assert.assertEquals(user.getUserPortfolio().getStock().size(), stockLength);
    Assert.assertEquals(user.getUserPortfolio().getCrypto().size(), cryptoLength);

    // test EQUITY asset
    user.purchaseAsset("AAPL", 5);
    Assert.assertEquals(user.getUserPortfolio().getStock().size(), stockLength + 1);
    stockLength += 1;
    Assert.assertEquals(user.getUserPortfolio().getAllAssetNames().size(), assetNamesLength);
    Asset lastStockAsset = user.getUserPortfolio().getStock().get(stockLength - 1);
    Assert.assertEquals(lastStockAsset.getAssetSymbol(), "AAPL");
    //Assert.assertEquals(lastStockAsset.getAmount(), 5);

    // test crypto asset
    user.purchaseAsset("BTC-USD", 0.001);
    Assert.assertEquals(user.getUserPortfolio().getCrypto().size(), cryptoLength + 1);
    cryptoLength += 1;
    Asset lastCryptoAsset = user.getUserPortfolio().getCrypto().get(cryptoLength - 1);
    Assert.assertEquals(lastCryptoAsset.getAssetSymbol(), "BTC-USD");
    Assert.assertEquals(lastCryptoAsset.getAmount(), 0.001);

    // check if assets is adding to extra information variables
    user.purchaseAsset("ZS", 2);
    assetNamesLength++;
    assetsPortfolioLength++;
    Assert.assertEquals(user.getUserPortfolio().getAssetsInPortfolio().size(), assetsPortfolioLength);
    Assert.assertEquals(user.getUserPortfolio().getAllAssetNames().size(), assetNamesLength);


    // test if not enough funds
    user.purchaseAsset("BTC-USD", 100);
    Assert.assertEquals(user.getUserPortfolio().getCrypto().size(), cryptoLength);
  }

  @Test
  public void testSellAsset_and_findPortfolioType_helperMethod() throws IOException, InterruptedException {

    user = new User(key);
    int cryptoLength = user.getUserPortfolio().getCrypto().size();
    int stockLength = user.getUserPortfolio().getStock().size();
    int assetNamesLength = user.getUserPortfolio().getAllAssetNames().size();
    int assetsPortfolioLength = user.getUserPortfolio().getAssetsInPortfolio().size();

    // test asset symbol which is not on stock market
    Assert.assertFalse(user.sellAsset("LYIT", 1));

    // test if amount is negative
    user.sellAsset("AAPL", -1);
    Assert.assertEquals(user.getUserPortfolio().getStock().size(), stockLength);

    // test stock asset
    user.sellAsset("TSLA", 10);
    stockLength--;
    assetNamesLength--;
    assetsPortfolioLength--;
    assertEquals(stockLength, user.getUserPortfolio().getStock().size());
    assertEquals(assetNamesLength, user.getUserPortfolio().getAllAssetNames().size());
    assertEquals(assetsPortfolioLength, user.getUserPortfolio().getAssetsInPortfolio().size());

    // test crypto asset and fully sold condition
    user.sellAsset("BTC-USD", 0.0445881);
    Assert.assertTrue(user.getUserPortfolio().getCrypto().isEmpty());

    // test if sell is profitable
    user.sellAsset("TSLA", 10);
    user.sellAsset("NVDA", 12);
    Asset asset = new Asset("AAPL", "Apple Inc.", "EQUITY", 1625487172, 100, 5);
    user.getUserPortfolio().getStock().add(asset);
    user.sellAsset("AAPL", 7);
    assertEquals(user.getUserPortfolio().getStock().get(0).getAmount(), 18);

  }
    @Test
    public void testAddfunds_and_Withdrawfunds_helperMethod() {

      //Testing Add funds
      double availableFunds = user.getAvailableFunds() + 1;
      user.addFunds(1);
      Assert.assertEquals(availableFunds, user.getAvailableFunds());
      user.addFunds(-1);
      Assert.assertEquals(availableFunds, user.getAvailableFunds());

      //Test Withdraw funds
      user = new User(key);
      Assert.assertFalse(user.withdrawFunds(10_001));
      Assert.assertTrue(user.withdrawFunds(9_999));
  }

  @Test
  public void testListAllInvestments_helperMethod() {
    user = new User(key);

    // Initial test where testing method with preloaded assets
    Portfolio portfolio = new Portfolio();

    String actual = portfolio.getStock().toString() + portfolio.getCrypto().toString();
    String expected = user.listAllInvestments();

    assertEquals(actual, expected);
  }
}