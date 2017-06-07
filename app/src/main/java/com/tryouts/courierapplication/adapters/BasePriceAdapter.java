package com.tryouts.courierapplication.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tryouts.courierapplication.items.PriceItem;
import com.tryouts.courierapplication.R;
import com.zhukic.sectionedrecyclerview.SectionedRecyclerAdapter;

import java.util.List;


public abstract class BasePriceAdapter extends SectionedRecyclerAdapter<BasePriceAdapter.SubheaderViewHolder, BasePriceAdapter.PriceItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(int adapterPosition, int positionInCollection);
    }

    List<PriceItem> priceList;

    public static class SubheaderViewHolder extends RecyclerView.ViewHolder {

        public TextView subheaderText;

        public SubheaderViewHolder(View itemView) {
            super(itemView);
            this.subheaderText = (TextView) itemView.findViewById(R.id.pricelist_recyclerview_header);


        }

    }

    public static class PriceItemViewHolder extends RecyclerView.ViewHolder {

        TextView mDescription;
        TextView mPrice;

        public PriceItemViewHolder(View itemView) {
            super(itemView);
            this.mDescription = (TextView) itemView.findViewById(R.id.pricelist_item_description);
            this.mPrice = (TextView) itemView.findViewById(R.id.pricelist_item_price);
        }
    }

    BasePriceAdapter(List<PriceItem> itemList) {
        super();
        this.priceList = itemList;
    }

    @Override
    public PriceItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new PriceItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_pricelist_recyclerview, parent, false));
    }

    @Override
    public SubheaderViewHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        return new SubheaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_header, parent, false));
    }

    @Override
    public int getCount() {
        return priceList.size();
    }

}