package com.kzlabs.popularmovies.util;

import com.kzlabs.popularmovies.model.Comment;
import com.kzlabs.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 4/7/17.
 */

public final class JSONUtils {

    public static List<Comment> parseReviews(StringBuilder sb) {
        List<Comment> list = new ArrayList<>();
            try {
                JSONObject jsonResponse = new JSONObject(sb.toString());
                JSONArray commentArray = jsonResponse.getJSONArray("results");
                for (int i = 0; i < commentArray.length(); i++){
                    JSONObject jsonComment = (JSONObject) commentArray.get(i);
                    Comment comment = new Comment();
                    comment.setId(jsonComment.getString("id"));
                    comment.setAuthor(jsonComment.getString("author"));
                    comment.setContent(jsonComment.getString("content"));

                    list.add(comment);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return list;
    }

    public static List<Trailer> parseTrailers(StringBuilder sb) {
        List<Trailer> list = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(sb.toString());
            JSONArray commentArray = jsonResponse.getJSONArray("results");
            for (int i = 0; i < commentArray.length(); i++){
                JSONObject jsonComment = (JSONObject) commentArray.get(i);
                Trailer trailer = new Trailer();
                trailer.setId(jsonComment.getString("id"));
                trailer.setLanguage(jsonComment.getString("iso_639_1"));
                trailer.setCountry(jsonComment.getString("iso_3166_1"));
                trailer.setKey(jsonComment.getString("key"));
                trailer.setName(jsonComment.getString("name"));
                trailer.setSite(jsonComment.getString("site"));
                trailer.setSize(jsonComment.getInt("size"));
                trailer.setType(jsonComment.getString("type"));

                list.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
