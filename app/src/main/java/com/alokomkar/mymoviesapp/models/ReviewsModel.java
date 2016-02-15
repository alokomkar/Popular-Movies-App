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

public class ReviewsModel implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<ReviewsResult> reviewsResults = new ArrayList<ReviewsResult>();
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

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
     * The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     *
     * @param page
     * The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     *
     * @return
     * The results
     */
    public List<ReviewsResult> getReviewsResults() {
        return reviewsResults;
    }

    /**
     *
     * @param reviewsResults
     * The results
     */
    public void setReviewsResults(List<ReviewsResult> reviewsResults) {
        this.reviewsResults = reviewsResults;
    }

    /**
     *
     * @return
     * The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     *
     * @param totalPages
     * The total_pages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     *
     * @return
     * The totalResults
     */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     *
     * @param totalResults
     * The total_results
     */
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public static class ReviewsResult implements Parcelable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("author")
        @Expose
        private String author;
        @SerializedName("content")
        @Expose
        private String content;
        @SerializedName("url")
        @Expose
        private String url;

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
         * The author
         */
        public String getAuthor() {
            return author;
        }

        /**
         *
         * @param author
         * The author
         */
        public void setAuthor(String author) {
            this.author = author;
        }

        /**
         *
         * @return
         * The content
         */
        public String getContent() {
            return content;
        }

        /**
         *
         * @param content
         * The content
         */
        public void setContent(String content) {
            this.content = content;
        }

        /**
         *
         * @return
         * The url
         */
        public String getUrl() {
            return url;
        }

        /**
         *
         * @param url
         * The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.author);
            dest.writeString(this.content);
            dest.writeString(this.url);
        }

        public ReviewsResult() {
        }

        protected ReviewsResult(Parcel in) {
            this.id = in.readString();
            this.author = in.readString();
            this.content = in.readString();
            this.url = in.readString();
        }

        public static final Parcelable.Creator<ReviewsResult> CREATOR = new Parcelable.Creator<ReviewsResult>() {
            public ReviewsResult createFromParcel(Parcel source) {
                return new ReviewsResult(source);
            }

            public ReviewsResult[] newArray(int size) {
                return new ReviewsResult[size];
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
        dest.writeValue(this.page);
        dest.writeTypedList(reviewsResults);
        dest.writeValue(this.totalPages);
        dest.writeValue(this.totalResults);
    }

    public ReviewsModel() {
    }

    protected ReviewsModel(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.page = (Integer) in.readValue(Integer.class.getClassLoader());
        this.reviewsResults = in.createTypedArrayList(ReviewsResult.CREATOR);
        this.totalPages = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalResults = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ReviewsModel> CREATOR = new Parcelable.Creator<ReviewsModel>() {
        public ReviewsModel createFromParcel(Parcel source) {
            return new ReviewsModel(source);
        }

        public ReviewsModel[] newArray(int size) {
            return new ReviewsModel[size];
        }
    };
}