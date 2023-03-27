package com.wso2.choreo.integration.tests.byoc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestHelper {

    static class Movie {

        Movie(int id, String name, int year, double ratings) {
            this.id = id;
            this.name = name;
            this.year = year;
            this.ratings = ratings;
        }
        int id;
        String name;
        int year;
        double ratings;
    }

    public static String getExpectedResponse() {
        Movie[] movies = {
                new Movie(1, "The Shawshank Redemption", 1994, 9.2),
                new Movie(2, "The God Father", 1972, 9.2),
                new Movie(3, " The Dark Knight", 2008, 5.2),
                new Movie(4, "The Godfather Part II", 1974, 7.5),
                new Movie(5, "12 Angry Men", 1957, 8.1)
        };

        return new Gson().toJson(movies);
    }


}
