package com.basics.android.udacity.booklist;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

// Classe utilitária com métodos para requistar e receber dados de livros da Google Books API.
public final class Utils {
    /**
     * Construtor privado vazio necessário para que ninguém consiga criar objetos desta classe, visto
     * que ela existe apenas para disponibilizar métodos e variáveis estáticas.
     */
    private Utils() {
    }

    public static final String LOG_TAG = Utils.class.getSimpleName();

    public static ArrayList<Book> fetchBookData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        ArrayList<Book> books = extractFeatureFromJson(jsonResponse);
        return books;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Book> extractFeatureFromJson(String searchResultJSON) {
        if (TextUtils.isEmpty(searchResultJSON)) {
            return null;
        }

        ArrayList<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(searchResultJSON);

            // Extrai do JSON um array com a lista de itens (livros) que representa os resultados
            // da pesquisa e, para cada item, extrai as propriedades necessárias.
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");
            for (int i = 0; i < bookArray.length(); i++) {
                JSONObject currentBook = bookArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                // Extrai o título do livro
                String title = volumeInfo.getString("title");

                // Extrai o subtítulo do livro caso exista
                String subtitle = "";
                if (volumeInfo.has("subtitle")) {
                    subtitle = volumeInfo.getString("subtitle");
                }

                // Extrai os autores do livro como uma string separada por virgulas
                String authors = volumeInfo.getJSONArray("authors").join(", ").replace("\"", "");

                float averageRating = 0;
                if (volumeInfo.has("averageRating")) {
                    averageRating = Float.parseFloat(volumeInfo.getString("averageRating"));
                }

                int ratingsCount = 0;
                if (volumeInfo.has("ratingsCount")) {
                    ratingsCount = volumeInfo.getInt("ratingsCount");
                }

                Book book = new Book(title, subtitle, authors, averageRating, ratingsCount);
                books.add(book);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        return books;
    }
}
