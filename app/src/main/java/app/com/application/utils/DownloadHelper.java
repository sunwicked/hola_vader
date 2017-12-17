package app.com.application.utils;

import android.content.Context;

import app.com.application.fetch.Fetch;
import app.com.application.fetch.listener.FetchListener;
import app.com.application.fetch.request.Request;

import app.com.application.OnDownloadListener;
import app.com.application.main.MainActivity;

/**
 * Created by admin on 17/12/17.
 */

public class DownloadHelper {


    private Context context;
    private String url;
    private String dirPath;
    private String fileName;
    private OnDownloadListener onDownloadListener;
    private Fetch fetch;
    private long downloadId;

    public DownloadHelper(Context context, String url, String dirPath, String fileName, OnDownloadListener onDownloadListener) {
        this.context = context;
        this.url = url;
        this.dirPath = dirPath;
        this.fileName = fileName+".mp3";
        this.onDownloadListener = onDownloadListener;
    }

    public void startDownload() {

        fetch = Fetch.newInstance(context);

        Request request = new Request(url, dirPath, fileName);
        request.setPriority(Fetch.PRIORITY_HIGH);
        downloadId = fetch.enqueue(request);

        if (downloadId != Fetch.ENQUEUE_ERROR_ID) {
            //Download was successfully queued for download.
        }

        fetch.addFetchListener(new FetchListener() {
            @Override
            public void onUpdate(long id, int status, int progress, long downloadedBytes, long fileSize, int error) {


                if (downloadId == id && status == Fetch.STATUS_DONE) {
                    onDownloadListener.onDownloadComplete(url);
                    release();
                } else if (error != Fetch.NO_ERROR) {
                    // An  erreur occurred

                    if (error == Fetch.ERROR_HTTP_NOT_FOUND) {
                        // handle  erreur
                    }

                }

            }
        });

    }


    private void release() {
        fetch.release();
    }

}
