package com.example.dunzoassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.example.dunzoassignment.adapter.PhotosRecycleAdapter;
import com.example.dunzoassignment.networking.Resource;
import com.example.dunzoassignment.networking.model.ApiResponse;
import com.example.dunzoassignment.networking.model.PhotoResponse;
import com.example.dunzoassignment.viewmodel.PhotoViewModel;
import com.example.dunzoassignment.viewmodel.ViewModelProviderFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";

    @Inject
    ViewModelProviderFactory providerFactory;

    @Inject
    RequestManager requestManager;

    private AppCompatTextView title;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private PhotoViewModel photoViewModel;
    private PhotosRecycleAdapter adapter;

    private int page = 1;
    private String query;
    private GridLayoutManager gridLayoutManager;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initRecyclerView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        searchView.setQueryHint("Search photos");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        photoViewModel = ViewModelProviders.of(this, providerFactory).get(PhotoViewModel.class);
    }

    private void initRecyclerView() {
        adapter = new PhotosRecycleAdapter(requestManager);
        gridLayoutManager = new GridLayoutManager(this, 12);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getSpanSize(position);
            }
        });

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int spacing = 4;
                outRect.top = spacing;
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.bottom = spacing;
            }
        });
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void initViews() {
        title = findViewById(R.id.titleLabel);
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.my_recycler_view);
        progressBar = findViewById(R.id.circular_progress_bar);
        searchView.setOnQueryTextListener(this);
    }


    /*
    This will decide when to make next paginated api call
     */
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
                if (!loading) {
                    int itemCount = adapter.getItemCount();
                    double thresholdPercent = Math.ceil(itemCount * 0.9d);
                    if (lastVisibleItemPosition > thresholdPercent || lastVisibleItemPosition == itemCount - 1) {
                        loading = true;
                        getPhotos();
                    }
                }
            }
        }
    };

    /*
    onNewIntent will receive Intent.ACTION_SEARCH intent when user searches a query
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY);
            this.page = 1;
            getPhotos();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Intent.ACTION_SEARCH);
            intent.putExtra(SearchManager.QUERY, query);
            startActivity(intent);
            searchView.clearFocus();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /*
    This method observes the state of view model and displays data accordingly on the screen.
     */

    private void getPhotos() {
        if (!TextUtils.isEmpty(query)) {
            photoViewModel.getPhotos(query, page, 10).observe(this, new Observer<Resource<ApiResponse>>() {
                @Override
                public void onChanged(Resource<ApiResponse> apiResource) {
                    if (apiResource != null) {
                        switch (apiResource.status) {

                            case LOADING: {
                                if (page == 1) {
                                    showProgressBar(true);
                                } else {
                                    adapter.addRefreshLayout();
                                }
                                break;
                            }

                            case SUCCESS: {
                                loading = false;
                                showProgressBar(false);
                                adapter.removeRefreshLayout();
                                ApiResponse apiResponse = apiResource.data;
                                if (apiResponse != null && apiResponse.getPhotoResponse() != null) {
                                    if (apiResponse.getPhotoResponse().getPhotos() != null
                                            && apiResponse.getPhotoResponse().getPhotos().size() > 0) {
                                        adapter.addPhotos(apiResponse.getPhotoResponse().getPhotos(), page);
                                    } else {
                                        if (page == 1) {
                                            adapter.addEmptyItem();
                                        }
                                    }
                                    page = apiResponse.getPhotoResponse().getPage() + 1;
                                }
                                break;
                            }

                            case ERROR: {
                                loading = false;
                                showProgressBar(false);
                                adapter.removeRefreshLayout();
                                Toast.makeText(MainActivity.this, apiResource.message, Toast.LENGTH_SHORT).show();
                                break;
                            }

                        }
                    }
                }
            });
        }
    }

    private void showProgressBar(boolean isVisible){
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
