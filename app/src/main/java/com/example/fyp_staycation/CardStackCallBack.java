package com.example.fyp_staycation;


import androidx.recyclerview.widget.DiffUtil;

import com.example.fyp_staycation.classes.Locations;

import java.util.List;

class CardStackCallback extends DiffUtil.Callback {

    private List<Locations> old, new1;

    public CardStackCallback(List<Locations> old, List<Locations> baru) {
        this.old = old;
        this.new1 = new1;
    }

    @Override
    public int getOldListSize() {
        return old.size();
    }

    @Override
    public int getNewListSize() {
        return new1.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition).getImage() == new1.get(newItemPosition).getImage();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition) == new1.get(newItemPosition);
    }
}