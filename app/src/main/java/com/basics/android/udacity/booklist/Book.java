package com.basics.android.udacity.booklist;

public class Book {

    private String mTitle;
    private String mSubtitle;
    private String mAuthors;
    private float mAverageRating;
    private int mRatingsCount;

    /**
     * Contrói um novo objeto {@link Book}.
     *
     * @param title         é o título do livro
     * @param subtitle      é o subtítulo do livro
     * @param authors       são os autores do livro
     * @param averageRating é a média de avaliação do livro por usuários
     * @param ratingsCount  é o total de avaliações de usuários
     */
    public Book(String title, String subtitle, String authors, float averageRating, int ratingsCount) {
        mTitle = title;
        mSubtitle = subtitle;
        mAuthors = authors;
        mAverageRating = averageRating;
        mRatingsCount = ratingsCount;
    }


    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public float getAverageRating() {
        return mAverageRating;
    }

    public int getRatingsCount() {
        return mRatingsCount;
    }
}