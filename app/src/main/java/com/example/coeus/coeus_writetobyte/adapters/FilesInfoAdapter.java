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
 * Description: This class is used to store and refer to scanned data files
 * that are stored within the realm database. Using the file path of each
 * data file, the views within MyFiles fragment can be updated.
 *
 * Author: Ojas Bhatia
 *
 * Last updated: January 10, 2019
 */

public class FilesInfoAdapter extends RealmRecyclerViewAdapter<ScannedDataFile, FilesInfoAdapter.FileInfoHolder> {

    private OrderedRealmCollection<ScannedDataFile> filesDataList;
    private Context context;

    //Provide a reference to the views for each data item.
    //Complex data items may need more than one view per item, and
    //provide access to all the views for a data item in MyFiles
    public static class FileInfoHolder extends RecyclerView.ViewHolder {
        //each data item stored as a string in this case
        public ImageView scannedImage;
        public TextView nameTextView;
        public TextView dateTextView;

       //this method initializes the view within MyFiles (image, title and date)
        public FileInfoHolder(View view) {
            super(view);
            scannedImage = view.findViewById(R.id.scanned_image);
            nameTextView = view.findViewById(R.id.file_name);
            dateTextView = view.findViewById(R.id.creation_date);
        }
    }

    //constructor (assigns datalist)
    public FilesInfoAdapter(Context context, OrderedRealmCollection<ScannedDataFile> filesDataList) {
        super(filesDataList, true);
        this.filesDataList = filesDataList;
        this.context = context;
    }

    //this method creates new views (invoked by the layout manager)
    @Override
    public FileInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_info, parent, false);
        FileInfoHolder fileInfoHolder = new FileInfoHolder(itemView);
        return fileInfoHolder;
    }

    //this method is used to update the MyFiles object view (once a new doc is scanned)
    @Override
    public void onBindViewHolder(FileInfoHolder fileInfoHolder, int position) {
        // get element from dataset at this position and
        // replace the contents of the view with that element
        ScannedDataFile scannedDataFile = filesDataList.get(position);
        String photoUriString = scannedDataFile.getFilePath();

        //checking if there is an associated photo with the new file
        if ((photoUriString != null) && !photoUriString.isEmpty() && (context != null)) {

            GlideApp.with(context)
                    .load(scannedDataFile.getFilePath())
                    .into(fileInfoHolder.scannedImage);

        }

        fileInfoHolder.nameTextView.setText(scannedDataFile.getFileName());
        fileInfoHolder.dateTextView.setText(scannedDataFile.getCreationDate());

    }

    //Accessor: Returns the size of the datalist (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filesDataList.size();
    }

}
