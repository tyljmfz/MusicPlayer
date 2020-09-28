package com.example.y84104146.mediaplayer;

import android.os.Bundle;

public class WishList {
    private boolean isLike;

    public WishList(boolean isLike){
        this.isLike = isLike;
    }

    public void setLikeing(boolean isLike){

        this.isLike = isLike;

    }

    public boolean getLikeing(){
        return isLike;
    }
}
