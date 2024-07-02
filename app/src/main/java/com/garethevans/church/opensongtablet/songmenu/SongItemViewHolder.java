package com.garethevans.church.opensongtablet.songmenu;

import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.garethevans.church.opensongtablet.R;

public class SongItemViewHolder extends RecyclerView.ViewHolder {

    final TextView itemTitle;
    final TextView itemAuthor;
    final TextView itemFolderNamePair;
    final CheckBox itemChecked;
    final FrameLayout itemCheckedFrame;
    final LinearLayout itemCard;

    SongItemViewHolder(View v) {
        super(v);
        itemCard = v.findViewById(R.id.songClickSpace);
        itemTitle = v.findViewById(R.id.cardview_title);
        itemAuthor = v.findViewById(R.id.cardview_author);
        itemFolderNamePair = v.findViewById(R.id.cardview_foldernamepair);
        itemChecked = v.findViewById(R.id.cardview_setcheck);
        itemCheckedFrame = v.findViewById(R.id.cardview_setcheck_frame);
    }
}
