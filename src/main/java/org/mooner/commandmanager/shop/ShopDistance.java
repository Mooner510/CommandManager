package org.mooner.commandmanager.shop;

public class ShopDistance {
    private final double minBuy;
    private final double maxBuy;
    private final double minSell;
    private final double maxSell;

    public ShopDistance(double minSell, double maxSell, double minBuy, double maxBuy) {
        this.minSell = minSell;
        this.maxSell = maxSell;
        this.minBuy = minBuy;
        this.maxBuy = maxBuy;
    }

    public double getMaxBuy() {
        return maxBuy;
    }

    public double getMaxSell() {
        return maxSell;
    }

    public double getMinBuy() {
        return minBuy;
    }

    public double getMinSell() {
        return minSell;
    }

    public boolean checkBuy(double buy) {
        return buy <= 0 || (minBuy > 0 && minBuy <= buy) && (maxBuy > 0 && maxBuy >= buy);
    }

    public boolean checkSell(double sell) {
        return sell <= 0 || (minSell > 0 && minSell <= sell) && (maxSell > 0 && maxSell >= sell);
    }
}
