package com.tryouts.courierapplication.items;

import java.util.ArrayList;
import java.util.List;

/**
 * PriceList implementation is really raw. If you want to use it, you should probably change it.
 * This is just to show an example in PricelistFragment.
 */

public class PriceList {

    private static PriceList sPriceList;
    private List<PriceItem> mPriceItems;

    public static PriceList get() {
        if(sPriceList == null) {
            sPriceList = new PriceList();
        }
        return sPriceList;
    }

    private PriceList() {
        mPriceItems = new ArrayList<>();
        List<String> mPricelistList = createDescriptions();
        List<String> mPricelistPrices = createPrices();
        for(int i=0; i<15;i++) {
            PriceItem priceItem = new PriceItem();
            priceItem.setDescription(mPricelistList.get(i));
            priceItem.setPrice(mPricelistPrices.get(i));
            if(i<=6) {
                priceItem.setGenre("Odległość");
            } else {
                priceItem.setGenre("Usługi dodatkowe");
            }
            mPriceItems.add(priceItem);
        }
    }

    public List<PriceItem> getPrices() {
        return mPriceItems;
    }

    public PriceItem getPriceItemByGenre(String genre) {
        for(PriceItem item : mPriceItems) {
            if(item.getGenre().equals(genre)) {
                return item;
            }
        }
        return null;
    }

    //RAW METHODS FOR STRINGS IMPLEMENTATION. Don't use them outside debugging process.
    private List<String> createDescriptions() {
        List<String> mPricelistList = new ArrayList<>();
        mPricelistList.add("Centrum");
        mPricelistList.add("Do 5km");
        mPricelistList.add("Od 5 do 10km");
        mPricelistList.add("Od 10 do 15km");
        mPricelistList.add("Od 15km do granic administracyjnych");
        mPricelistList.add("Do 10km od granic administracyjnych");
        mPricelistList.add("Do 30km od granic administracyjnych");
        mPricelistList.add("Express - doręczenie do 1 godziny");
        mPricelistList.add("Superexpress - doręczenie poniżej 1 godziny");
        mPricelistList.add("Usługi w nadgodzinach (18:00-8:00) oraz w soboty i niedziele");
        mPricelistList.add("Ekspres drogowy");
        mPricelistList.add("Powiadomienie SMS o doręczeniu");
        mPricelistList.add("Nadanie na PKS/PKP");
        mPricelistList.add("Oczekiwanie kuriera powyżej 10 minut");
        mPricelistList.add("Paczka powyżej 5kg");
        return mPricelistList;
    }
    private List<String> createPrices() {
        List<String> mPricelistPrices = new ArrayList<>();
        mPricelistPrices.add("8 PLN");
        mPricelistPrices.add("12 PLN");
        mPricelistPrices.add("16 PLN");
        mPricelistPrices.add("20 PLN");
        mPricelistPrices.add("23 PLN");
        mPricelistPrices.add("30 PLN");
        mPricelistPrices.add("45 PLN");
        mPricelistPrices.add("+50%% do ceny standardowej");
        mPricelistPrices.add("+130%% do ceny standardowej");
        mPricelistPrices.add("+100%% do zamówionej usługi");
        mPricelistPrices.add("20 PLN + 1,20PLN za kilometr (w obie strony)");
        mPricelistPrices.add("1 PLN");
        mPricelistPrices.add("zamówiona usługa + opłata PKS/PKP");
        mPricelistPrices.add("5 PLN za każde 10 minut");
        mPricelistPrices.add("+0,70/kg");
        return mPricelistPrices;
    }

}
