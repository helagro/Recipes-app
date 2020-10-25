package com.mycompaney.hlag.foodtools2;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {
    public int id;
    public long moded, time;
    public double prt;
    public String name, shop, content;

    Recipe(int _id, String _name, String _shop, String _content, Long _moded, Long _time, Double _prt){
        id = _id;
        name = _name;
        content = _content;
        shop = _shop;
        moded = _moded;
        time = _time;
        prt = _prt;
    }

    private Recipe(Parcel in) {
        id = in.readInt();
        moded = in.readLong();
        time = in.readLong();
        name = in.readString();
        content = in.readString();
        shop = in.readString();
        prt = in.readDouble();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String toString(){
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeLong(moded);
        parcel.writeLong(time);
        parcel.writeString(name);
        parcel.writeString(content);
        parcel.writeString(shop);
        parcel.writeDouble(prt);
    }
}
