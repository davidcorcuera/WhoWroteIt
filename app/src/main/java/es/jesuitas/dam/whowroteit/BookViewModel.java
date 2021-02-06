package es.jesuitas.dam.whowroteit;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    public BookViewModel(Application application){
        super(application);
        mContext = getApplication();
        mRepository = new BookRepository(mContext);
    }

    LiveData<String> getAuthor(){
        mAuthor = mRepository.getAuthor();
        return mAuthor;
    }

    LiveData<String> getTitle(){
        mTitle = mRepository.getTitle();
        return mTitle;
    }

    void fetchBook(String queryString, View view) {
        InputMethodManager inputManager = (InputMethodManager)
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null ) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()
                && queryString.length() != 0) {
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
}
