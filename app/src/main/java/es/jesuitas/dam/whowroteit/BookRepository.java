package es.jesuitas.dam.whowroteit;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONObject;

public class BookRepository {

    private MutableLiveData<String> mAuthor = new MutableLiveData<>();
    private MutableLiveData<String> mTitle = new MutableLiveData<>();
    private Context mContext;

    BookRepository(Context context) {
        this.mContext = context;
    }

    MutableLiveData<String> getAuthor(){
        return mAuthor;
    }

    MutableLiveData<String> getTitle(){
        return mTitle;
    }

    void fetchBook(String s) {
        String responseString;
        // Get Book
        responseString = NetworkUtils.getBookInfo(s);

        // Parse JSON response
        try {
            //Create JSON object
            JSONObject jsonObject = new JSONObject(responseString);
            // Turned into JSONArray
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            int i = 0;
            String title = null;
            String authors = null;

            while (i < itemsArray.length() &&
                    (authors == null && title == null)) {
                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Move to the next item.
                i++;
            }

            if (title != null && authors != null) {
                // Can't use setValue on background threads
                mTitle.postValue(title);
                mAuthor.postValue(authors);
            } else {
                mTitle.postValue(mContext.getString(R.string.no_results));
                mAuthor.postValue("");
            }


        } catch (Exception e) {
            // If hasn't received a proper JSON string,
            // update the UI to show failed results.
            mTitle.postValue(mContext.getString(R.string.no_results));
            mAuthor.postValue("");
        }
    }
}
