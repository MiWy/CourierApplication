package com.tryouts.courierapplication.items;


public class PriceItem {
    private String mDescription;
    private String mPrice;
    private String mGenre;

    public PriceItem() {
        this("", "", "");
    }

    public PriceItem(String description, String price, String genre) {
        this.mDescription = description;
        this.mPrice = price;
        this.mGenre = genre;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getGenre() {
        return mGenre;
    }

    public void setGenre(String genre) {
        mGenre = genre;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setPrice(String price) {
        mPrice = price;
    }


}
