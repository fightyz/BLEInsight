package org.esec.mcg.bleinsight.viewholder;

import android.view.View;
import android.widget.TextView;

import org.esec.mcg.bleinsight.R;

/**
 * Created by yz on 2015/9/28.
 */
public class CharacteristicViewHolder extends ChildViewHolder {

    public TextView mDataTextView;

    public CharacteristicViewHolder(View itemView) {
        super(itemView);

        mDataTextView = (TextView) itemView.findViewById(R.id.list_item_vertical_child_textView);
    }

    public void bind(String childText) { mDataTextView.setText(childText); }
}
