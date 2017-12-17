package app.com.application.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.com.application.AppConstants;
import app.com.application.OnDownloadListener;
import app.com.application.OnItemClickListener;
import app.com.application.R;
import app.com.application.player.PlayerActivity;
import app.com.application.network.FetchDataAPi;
import app.com.application.network.NetworkCallback;
import app.com.application.storage.Storage;
import app.com.application.utils.DownloadHelper;
import app.com.application.utils.NetworkUtils;
import butterknife.Bind;
import butterknife.ButterKnife;

import static app.com.application.AppConstants.EXTRA_DATA_MODEL;
import static app.com.application.AppConstants.NETWORK_FETCH_ALL;
import static app.com.application.AppConstants.PREF_DATA_LIST;
import static app.com.application.utils.CommonUtils.getPathName;

public class MainActivity extends AppCompatActivity implements NetworkCallback, OnItemClickListener<DataModel>, FilterFragment.FragmentClickListener, OnDownloadListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_data)
    RecyclerView rvData;


    DataAdapter dataAdapter;
    List<DataModel> dataModels = new ArrayList<>();
    @Bind(R.id.btn_show_all)
    Button btnShowAll;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Storage storage;
    private static final int STORAGE_PERMISSION_CODE = 50;

    boolean showFilter = false;
    private FilterFragment bottomSheetDialogFragment;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(AppConstants.APP_PREFERENCE, Context.MODE_PRIVATE);
        gson = new Gson();
        storage = new Storage(sharedPreferences, gson);

        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataAdapter.update(dataModels);
                btnShowAll.setVisibility(View.GONE);
            }
        });
        setUpAdapter();

        if (NetworkUtils.isNetworkAvailable(this)) {
            FetchDataAPi.getList(this, NETWORK_FETCH_ALL);
        } else {
            Snackbar.make(rvData, R.string.info_no_internet, Snackbar.LENGTH_LONG).show();
        }


        createStorageFolder();

    }

    private void createStorageFolder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , STORAGE_PERMISSION_CODE);
        }
        else
        {

        }
    }

    private void setUpAdapter() {
        final List<DataModel> dataFromCache = storage.getDataFromLocal(AppConstants.PREF_DATA_LIST);
        if (null != dataFromCache) {
            dataModels = dataFromCache;
        }
        dataAdapter = new DataAdapter(this, dataModels, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvData.setLayoutManager(mLayoutManager);
        rvData.setItemAnimator(new DefaultItemAnimator());
        rvData.setAdapter(dataAdapter);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            createRootFolder();
        } else {
            createStorageFolder();
        }
    }

    private void createRootFolder() {
        File fetchDir = new File(getPathName());
        fetchDir.mkdirs();
    }


    @Override
    public void onResponse(Object o, int requestType) {
        switch (requestType) {
            case NETWORK_FETCH_ALL:
                final List<DataModel> dataModels = (List<DataModel>) o;
                dataAdapter.update(dataModels);
//save locally

                storage.saveDataToLocal(PREF_DATA_LIST, dataModels);

                break;


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            // action with ID action_filter was selected
            case R.id.action_filter:
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                break;
            // action with ID action_sort was selected
            case R.id.action_sort:
                break;
        }
        return true;
    }

    @Override
    public void onFailure(Throwable t) {

        Snackbar.make(rvData, R.string.info_feed_failed, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(@NonNull DataModel item) {
        Intent nextScreenIntent = new Intent(this, PlayerActivity.class);
        nextScreenIntent.putExtra(EXTRA_DATA_MODEL, item);
        startActivity(nextScreenIntent);


    }

    @Override
    public void onItemClick(@NonNull DataModel item, int type) {

    }

    @Override
    public void onActionClicked(@NonNull DataModel item) {
        DownloadHelper downloadHelper = new DownloadHelper(this
                , item.getUrl(),
                getPathName()
                , item.getSong(), this);
        downloadHelper.startDownload();

        for (int i = 0; i < dataModels.size(); i++) {
            if (dataModels.get(i).equals(item.getUrl())) {
                dataModels.get(i).setStatus(Status.DOWNLOADING);
                break;
            }
        }

    }


    @Override
    public void onFilterItemClicked(int position, int type) {
        btnShowAll.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDownloadComplete(String url) {

        for (int i = 0; i < dataModels.size(); i++) {
            if (dataModels.get(i).getUrl().equals(url)) {
                dataModels.get(i).setStatus(Status.LOCAL);
                break;
            }
        }
        dataAdapter.update(dataModels);
    }
}
