package app.com.application.main;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.com.application.OnItemClickListener;
import app.com.application.R;
import app.com.application.utils.DownloadHelper;

import static app.com.application.AppConstants.MMM_DD;

/**
 * Created by admin on 09/09/17.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {


    private final Context context;
    private List<DataModel> dataModels, filteredDataModels;
    private OnItemClickListener<DataModel> onItemClickListener;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MMM_DD, Locale.getDefault());
    boolean sortNewToOld = false;
    private Picasso.Builder builder;

    public DataAdapter(Context context, List<DataModel> dataModels, OnItemClickListener<DataModel> onItemClickListener) {

        this.context = context;
        this.dataModels = dataModels;
        this.filteredDataModels = dataModels;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DataAdapter.ViewHolder holder, int position) {
        final DataModel dataModel = filteredDataModels.get(position);
        holder.tvTitle.setText(dataModel.getSong());
        holder.tvPublisher.setText(dataModel.getArtists());
        holder.cvParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(dataModel);
            }
        });

        if (dataModel.getStatus().equals(Status.REMOTE)) {
            holder.ivDownload.setVisibility(View.VISIBLE);
            holder.ivDownload.setEnabled(true);
        } else if (dataModel.getStatus().equals(Status.LOCAL)) {
            holder.ivDownload.setVisibility(View.GONE);
        }
        if (dataModel.getStatus().equals(Status.DOWNLOADING)) {
            holder.ivDownload.setVisibility(View.GONE);
            holder.ivDownload.setClickable(false);
        }
        holder.ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataModel.getStatus().equals(Status.REMOTE))
                {
                    dataModel.setStatus(Status.DOWNLOADING);
                    holder.ivDownload.setVisibility(View.GONE);
                    holder.ivDownload.setEnabled(false);
                }


                onItemClickListener.onActionClicked(dataModel);
            }
        });
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {

            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

            }
        });
        builder.downloader(new OkHttpDownloader(context));
        builder.build().load(dataModel.getCoverImage()).fit().centerCrop().into(holder.ivPar);

    }


    @Override
    public int getItemCount() {
        return filteredDataModels.size();
    }


    public void update(List<DataModel> modelList) {
        filteredDataModels = modelList;
        notifyDataSetChanged();
    }


    /**
     * Filtering the data based on the filter type and value
     *
     * @param type
     * @param filter
     */
    public void filterData(int type, String filter) {
        List<DataModel> temp = new ArrayList<>();
        filteredDataModels = dataModels;
        filteredDataModels = temp;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvPublisher;
        CardView cvParent;
        ImageView ivPar;
        ImageView ivDownload;

        public ViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tv_title);
            tvPublisher = (TextView) v.findViewById(R.id.tv_artists);
            cvParent = v.findViewById(R.id.cv_parent);
            ivPar = v.findViewById(R.id.iv_par);
            ivDownload = v.findViewById(R.id.iv_download);
        }
    }


}
