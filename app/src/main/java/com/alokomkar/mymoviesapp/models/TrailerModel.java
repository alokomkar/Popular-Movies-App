package com.alokomkar.mymoviesapp.models;

/**
 * Created by cognitive on 2/11/16.
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrailerModel implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<TrailerResult> trailerResults = new ArrayList<TrailerResult>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The results
     */
    public List<TrailerResult> getTrailerResults() {
        return trailerResults;
    }

    /**
     *
     * @param trailerResults
     * The results
     */
    public void setTrailerResults(List<TrailerResult> trailerResults) {
        this.trailerResults = trailerResults;
    }

    public static class TrailerResult implements Parcelable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("iso_639_1")
        @Expose
        private String iso6391;
        @SerializedName("key")
        @Expose
        private String key;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("site")
        @Expose
        private String site;
        @SerializedName("size")
        @Expose
        private Integer size;
        @SerializedName("type")
        @Expose
        private String type;

        /**
         *
         * @return
         * The id
         */
        public String getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The iso6391
         */
        public String getIso6391() {
            return iso6391;
        }

        /**
         *
         * @param iso6391
         * The iso_639_1
         */
        public void setIso6391(String iso6391) {
            this.iso6391 = iso6391;
        }

        /**
         *
         * @return
         * The key
         */
        public String getKey() {
            return key;
        }

        /**
         *
         * @param key
         * The key
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         * The site
         */
        public String getSite() {
            return site;
        }

        /**
         *
         * @param site
         * The site
         */
        public void setSite(String site) {
            this.site = site;
        }

        /**
         *
         * @return
         * The size
         */
        public Integer getSize() {
            return size;
        }

        /**
         *
         * @param size
         * The size
         */
        public void setSize(Integer size) {
            this.size = size;
        }

        /**
         *
         * @return
         * The type
         */
        public String getType() {
            return type;
        }

        /**
         *
         * @param type
         * The type
         */
        public void setType(String type) {
            this.type = type;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.iso6391);
            dest.writeString(this.key);
            dest.writeString(this.name);
            dest.writeString(this.site);
            dest.writeValue(this.size);
            dest.writeString(this.type);
        }

        public TrailerResult() {
        }

        protected TrailerResult(Parcel in) {
            this.id = in.readString();
            this.iso6391 = in.readString();
            this.key = in.readString();
            this.name = in.readString();
            this.site = in.readString();
            this.size = (Integer) in.readValue(Integer.class.getClassLoader());
            this.type = in.readString();
        }

        public static final Parcelable.Creator<TrailerResult> CREATOR = new Parcelable.Creator<TrailerResult>() {
            public TrailerResult createFromParcel(Parcel source) {
                return new TrailerResult(source);
            }

            public TrailerResult[] newArray(int size) {
                return new TrailerResult[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeTypedList(trailerResults);
    }

    public TrailerModel() {
    }

    protected TrailerModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.trailerResults = in.createTypedArrayList(TrailerResult.CREATOR);
    }

    public static final Parcelable.Creator<TrailerModel> CREATOR = new Parcelable.Creator<TrailerModel>() {
        public TrailerModel createFromParcel(Parcel source) {
            return new TrailerModel(source);
        }

        public TrailerModel[] newArray(int size) {
            return new TrailerModel[size];
        }
    };
}