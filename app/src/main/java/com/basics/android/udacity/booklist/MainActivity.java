package com.basics.android.udacity.booklist;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // URL para retornar dados de livros da Google Books API
    private static final String GBOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    // Elementos da interface
    private EditText searchEditText;
    private TextView mEmptyStateTextView;
    private ProgressBar mLoadingProgressBar;

    private BookAdapter mAdapter;
    private ArrayList<Book> mBooksResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        // Habilita o botão de pesquisar (lupa) no soft keyboard para inicializar a busca.
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        // Por ser um botão redundante ao do soft keyboard, invoca o mesmo método.
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch();
            }
        });

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.load_status);
        mAdapter = new BookAdapter(this, mBooksResults);

        ListView bookListView = (ListView) findViewById(R.id.list);
        bookListView.setEmptyView(mEmptyStateTextView);
        bookListView.setAdapter(mAdapter);

        // Restaura o estado da ListView sem precisar realizar uma nova query na internet em caso de
        // recriação da Activity. Ex: girar o telefone
        if (savedInstanceState != null) {
            mBooksResults = (ArrayList<Book>) savedInstanceState.getSerializable("books");
            mAdapter.addAll(mBooksResults);
            // Evita abrir automaticamente o soft keyboard quando a activity é recriada.
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    /**
     * Salva os dados da ListView quando a Activity é interrompida. Ex: girar o celular.
     * A idéia é não precisar realizar uma nova query na internet para recuperar os dados
     * perdidos.
     */
    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putSerializable("books", mBooksResults);
    }

    // Método para ocultar manualmente o soft keyboard
    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Encontra a view que está em foco no momento para que possamos obter o token de janela correto.
        View view = activity.getCurrentFocus();
        // Se nenhuma view estiver em foco, cria uma nova para que possamos obter o token dela.
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Método para inicializar a pesquisa na Google Books API
    private void performSearch() {
        hideKeyboard(this);

        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mEmptyStateTextView.setText(" ");
        mAdapter.clear();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Checa se há conexão internet e realiza a pesquisa. Caso contrário, exibe um erro.
        if (networkInfo != null && networkInfo.isConnected()) {
            SearchBooksTask task = new SearchBooksTask();
            task.execute(GBOOKS_REQUEST_URL + searchEditText.getText().toString());
        } else {
            mLoadingProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    // Tarefa que realiza a pesquisa na internet em background.
    private class SearchBooksTask extends AsyncTask<String, Void, ArrayList<Book>> {
        protected ArrayList<Book> doInBackground(String... urls) {
            mBooksResults = Utils.fetchBookData(urls[0]);
            return mBooksResults;
        }

        protected void onPostExecute(ArrayList<Book> books) {
            mLoadingProgressBar.setVisibility(View.GONE);
            // Informa a mensagem que será exibida caso a pesquisa não retorne nenhum resultado.
            mEmptyStateTextView.setText(R.string.no_books);

            // Se a pesquisa retornar resultados, atualiza a ListView
            if (books != null && !books.isEmpty()) {
                mAdapter.addAll(books);
            }
        }
    }
}
