package com.garethevans.church.opensongtablet.setprocessing;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.garethevans.church.opensongtablet.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

public class ImportSetItemViewHolder extends RecyclerView.ViewHolder {

    final CardView cardView;
    final MaterialTextView cardItem, cardTitle, cardFilename, cardFolder, cardExists;
    final MaterialCheckBox cardCheckBox;
    final RelativeLayout cardLayout;
    final FloatingActionButton cardEdit;

    public ImportSetItemViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.card_view);
        cardLayout = itemView.findViewById(R.id.cardview_layout);
        cardItem = itemView.findViewById(R.id.cardview_item);
        cardTitle = itemView.findViewById(R.id.cardview_songtitle);
        cardFilename = itemView.findViewById(R.id.cardview_songfilename);
        cardFolder = itemView.findViewById(R.id.cardview_folder);
        cardEdit = itemView.findViewById(R.id.cardview_edit);
        cardCheckBox = itemView.findViewById(R.id.cardview_checkbox);
        cardCheckBox.setVisibility(View.VISIBLE);
        cardExists = itemView.findViewById(R.id.cardview_exists);
    }
}
