/**
 * Created by cognitive on 2/8/16.
 */
package com.alokomkar.mymoviesapp.apimodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieModel implements Parcelable {

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<MovieResult> movieResults = new ArrayList<MovieResult>();
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

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
    public List<MovieResult> getMovieResults() {
        return movieResults;
    }

    /**
     *
     * @param movieResults
     * The results
     */
    public void setMovieResults(List<MovieResult> movieResults) {
        this.movieResults = movieResults;
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

    public static class MovieResult implements Parcelable {

        @SerializedName("adult")
        @Expose
        private boolean adult;
        @SerializedName("backdrop_path")
        @Expose
        private String backdropPath;
        @SerializedName("genre_ids")
        @Expose
        private List<Integer> genreIds = new ArrayList<Integer>();
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("original_language")
        @Expose
        private String originalLanguage;
        @SerializedName("original_title")
        @Expose
        private String originalTitle;
        @SerializedName("overview")
        @Expose
        private String overview;
        @SerializedName("release_date")
        @Expose
        private String releaseDate;
        @SerializedName("poster_path")
        @Expose
        private String posterPath;
        @SerializedName("popularity")
        @Expose
        private Double popularity;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("video")
        @Expose
        private boolean video;
        @SerializedName("vote_average")
        @Expose
        private Double voteAverage;
        @SerializedName("vote_count")
        @Expose
        private Integer voteCount;

        /**
         *
         * @return
         * The adult
         */
        public boolean getAdult() {
            return adult;
        }

        /**
         *
         * @param adult
         * The adult
         */
        public void setAdult(boolean adult) {
            this.adult = adult;
        }

        /**
         *
         * @return
         * The backdropPath
         */
        public String getBackdropPath() {
            return backdropPath;
        }

        /**
         *
         * @param backdropPath
         * The backdrop_path
         */
        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        /**
         *
         * @return
         * The genreIds
         */
        public List<Integer> getGenreIds() {
            return genreIds;
        }

        /**
         *
         * @param genreIds
         * The genre_ids
         */
        public void setGenreIds(List<Integer> genreIds) {
            this.genreIds = genreIds;
        }

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
         * The originalLanguage
         */
        public String getOriginalLanguage() {
            return originalLanguage;
        }

        /**
         *
         * @param originalLanguage
         * The original_language
         */
        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }

        /**
         *
         * @return
         * The originalTitle
         */
        public String getOriginalTitle() {
            return originalTitle;
        }

        /**
         *
         * @param originalTitle
         * The original_title
         */
        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        /**
         *
         * @return
         * The overview
         */
        public String getOverview() {
            return overview;
        }

        /**
         *
         * @param overview
         * The overview
         */
        public void setOverview(String overview) {
            this.overview = overview;
        }

        /**
         *
         * @return
         * The releaseDate
         */
        public String getReleaseDate() {
            return releaseDate;
        }

        /**
         *
         * @param releaseDate
         * The release_date
         */
        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        /**
         *
         * @return
         * The posterPath
         */
        public String getPosterPath() {
            return posterPath;
        }

        /**
         *
         * @param posterPath
         * The poster_path
         */
        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        /**
         *
         * @return
         * The popularity
         */
        public Double getPopularity() {
            return popularity;
        }

        /**
         *
         * @param popularity
         * The popularity
         */
        public void setPopularity(Double popularity) {
            this.popularity = popularity;
        }

        /**
         *
         * @return
         * The title
         */
        public String getTitle() {
            return title;
        }

        /**
         *
         * @param title
         * The title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         *
         * @return
         * The video
         */
        public boolean getVideo() {
            return video;
        }

        /**
         *
         * @param video
         * The video
         */
        public void setVideo(boolean video) {
            this.video = video;
        }

        /**
         *
         * @return
         * The voteAverage
         */
        public Double getVoteAverage() {
            return voteAverage;
        }

        /**
         *
         * @param voteAverage
         * The vote_average
         */
        public void setVoteAverage(Double voteAverage) {
            this.voteAverage = voteAverage;
        }

        /**
         *
         * @return
         * The voteCount
         */
        public Integer getVoteCount() {
            return voteCount;
        }

        /**
         *
         * @param voteCount
         * The vote_count
         */
        public void setVoteCount(Integer voteCount) {
            this.voteCount = voteCount;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(adult ? (byte) 1 : (byte) 0);
            dest.writeString(this.backdropPath);
            dest.writeList(this.genreIds);
            dest.writeValue(this.id);
            dest.writeString(this.originalLanguage);
            dest.writeString(this.originalTitle);
            dest.writeString(this.overview);
            dest.writeString(this.releaseDate);
            dest.writeString(this.posterPath);
            dest.writeValue(this.popularity);
            dest.writeString(this.title);
            dest.writeByte(video ? (byte) 1 : (byte) 0);
            dest.writeValue(this.voteAverage);
            dest.writeValue(this.voteCount);
        }

        public MovieResult() {
        }

        protected MovieResult(Parcel in) {
            this.adult = in.readByte() != 0;
            this.backdropPath = in.readString();
            this.genreIds = new ArrayList<Integer>();
            in.readList(this.genreIds, List.class.getClassLoader());
            this.id = (Integer) in.readValue(Integer.class.getClassLoader());
            this.originalLanguage = in.readString();
            this.originalTitle = in.readString();
            this.overview = in.readString();
            this.releaseDate = in.readString();
            this.posterPath = in.readString();
            this.popularity = (Double) in.readValue(Double.class.getClassLoader());
            this.title = in.readString();
            this.video = in.readByte() != 0;
            this.voteAverage = (Double) in.readValue(Double.class.getClassLoader());
            this.voteCount = (Integer) in.readValue(Integer.class.getClassLoader());
        }

        public static final Parcelable.Creator<MovieResult> CREATOR = new Parcelable.Creator<MovieResult>() {
            public MovieResult createFromParcel(Parcel source) {
                return new MovieResult(source);
            }

            public MovieResult[] newArray(int size) {
                return new MovieResult[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.page);
        dest.writeTypedList(movieResults);
        dest.writeValue(this.totalPages);
        dest.writeValue(this.totalResults);
    }

    public MovieModel() {
    }

    protected MovieModel(Parcel in) {
        this.page = (Integer) in.readValue(Integer.class.getClassLoader());
        this.movieResults = in.createTypedArrayList(MovieResult.CREATOR);
        this.totalPages = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalResults = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<MovieModel> CREATOR = new Parcelable.Creator<MovieModel>() {
        public MovieModel createFromParcel(Parcel source) {
            return new MovieModel(source);
        }

        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };
}
