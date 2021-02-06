package es.jesuitas.dam.whowroteit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;
    private final int NUMBER_OF_THREADS = 4;
    public static ExecutorService fetchBookExecutor;
    private BookViewModel mBookViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookInput = findViewById(R.id.bookInput);
        mTitleText = findViewById(R.id.titleText);
        mAuthorText = findViewById(R.id.authorText);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mBookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        // If Author changed, update View
        mBookViewModel.getAuthor().observe(this, author -> mAuthorText.setText(author));

        //If Title changed, update View
        mBookViewModel.getTitle().observe(this, title -> mTitleText.setText(title));

        // Create 4 Threads pool
        fetchBookExecutor =
                Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    }

    public void searchBooks(View view) {
        // Get the search string from the input field.
        String queryString = mBookInput.getText().toString();
        // Fetch Book
        mBookViewModel.fetchBook(queryString, view);
    }
}