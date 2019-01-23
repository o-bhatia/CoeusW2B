package com.example.coeus.coeus_writetobyte.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coeus.coeus_writetobyte.R;
import com.example.coeus.coeus_writetobyte.realm.ScannedDataFile;
import com.example.coeus.coeus_writetobyte.utils.GlideApp;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by victor on 1/12/19.
 */

public class FilesInfoAdapter extends RealmRecyclerViewAdapter<ScannedDataFile, FilesInfoAdapter.FileInfoHolder> {

    private OrderedRealmCollection<ScannedDataFile> filesDataList;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FileInfoHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView scannedImage;
        public TextView nameTextView;
        public TextView dateTextView;

        public FileInfoHolder(View view) {
            super(view);
            scannedImage = view.findViewById(R.id.scanned_image);
            nameTextView = view.findViewById(R.id.file_name);
            dateTextView = view.findViewById(R.id.creation_date);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FilesInfoAdapter(Context context, OrderedRealmCollection<ScannedDataFile> filesDataList) {
        super(filesDataList, true);
        this.filesDataList = filesDataList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FileInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_info, parent, false);
        FileInfoHolder fileInfoHolder = new FileInfoHolder(itemView);
        return fileInfoHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FileInfoHolder fileInfoHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ScannedDataFile scannedDataFile = filesDataList.get(position);
        String photoUriString = scannedDataFile.getFilePath();

        if ((photoUriString != null) && !photoUriString.isEmpty() && (context != null)) {

            GlideApp.with(context)
                    .load(scannedDataFile.getFilePath())
                    .into(fileInfoHolder.scannedImage);

        }

        fileInfoHolder.nameTextView.setText(scannedDataFile.getFileName());
        fileInfoHolder.dateTextView.setText(scannedDataFile.getCreationDate());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filesDataList.size();
    }

}
