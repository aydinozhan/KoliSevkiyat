package com.example.kolisevkiyat;

import android.os.Parcel;
import android.os.Parcelable;


public class Dto implements Parcelable {
    String BelgeNo;
    String EvrakNo;
    int CariRecNo;


    protected Dto(Parcel in) {
        BelgeNo = in.readString();
        EvrakNo = in.readString();
        CariRecNo = in.readInt();
    }

    public static final Creator<Dto> CREATOR = new Creator<Dto>() {
        @Override
        public Dto createFromParcel(Parcel in) {
            return new Dto(in);
        }

        @Override
        public Dto[] newArray(int size) {
            return new Dto[size];
        }
    };

    public Dto(String BelgeNo, String EvrakNo, int CariRecNo) {
        this.EvrakNo=EvrakNo;
        this.BelgeNo=BelgeNo;
        this.CariRecNo=CariRecNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(BelgeNo);
        dest.writeString(EvrakNo);
        dest.writeInt(CariRecNo);
    }
}
