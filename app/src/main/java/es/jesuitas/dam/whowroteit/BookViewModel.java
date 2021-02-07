package es.jesuitas.dam.whowroteit;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BookViewModel extends AndroidViewModel {

    private BookRepository mRepository;
    private MutableLiveData<String> mAuthor;
    private MutableLiveData<String> mTitle;
    private Context mContext;

    public BookViewModel(Application application) {
        super(application);
        mContext = getApplication();
        mRepository = new BookRepository(mContext);
    }

    LiveData<String> getAuthor() {
        mAuthor = mRepository.getAuthor();
        return mAuthor;
    }

    LiveData<String> getTitle() {
        mTitle = mRepository.getTitle();
        return mTitle;
    }

    void fetchBook(String queryString, View view) {
        InputMethodManager inputManager = (InputMethodManager)
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if (networkConnected() && queryString.length() != 0) {
            MainActivity.fetchBookExecutor.execute(() -> mRepository.fetchBook(queryString));
            mAuthor.setValue("");
            mTitle.setValue(mContext.getString(R.string.loading));
        } else {
            if (queryString.length() == 0) {
                mAuthor.setValue("");
                mTitle.setValue(mContext.getString(R.string.no_search_term));
            } else {
                mAuthor.setValue("");
                mTitle.setValue(mContext.getString(R.string.no_network));
            }
        }
    }


    private boolean networkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // NetworkInfo deprecated in API 29
                // and getActiveNetwork() added in API 23
                Network nw = connMgr.getActiveNetwork();
                if (nw == null) return false;
                NetworkCapabilities actNw = connMgr.getNetworkCapabilities(nw);
                return actNw != null && (actNw
                        .hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
            } else {
                NetworkInfo nwInfo = null;
                nwInfo = connMgr.getActiveNetworkInfo();
                return nwInfo != null && nwInfo.isConnected();
            }
        }
        return false;
    }
}
