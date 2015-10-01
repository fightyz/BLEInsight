package org.esec.mcg.bleinsight.viewholder;

import android.view.View;
import android.widget.TextView;

import org.esec.mcg.bleinsight.CharacteristicItemBean;
import org.esec.mcg.bleinsight.R;

/**
 * Created by yz on 2015/9/28.
 */
public class CharacteristicViewHolder extends ChildViewHolder {

    public TextView characteristicName;
    public TextView characteristicUuidValue;
    public TextView characteristicPropertiesValue;

    public CharacteristicViewHolder(View itemView) {
        super(itemView);

        characteristicName = (TextView) itemView.findViewById(R.id.characteristic_name);
        characteristicUuidValue = (TextView) itemView.findViewById(R.id.characteristic_uuid_value);
        characteristicPropertiesValue = (TextView) itemView.findViewById(R.id.characteristic_properties_value);
    }

    public void bind(CharacteristicItemBean characteristicItemBean) {
        characteristicName.setText(characteristicItemBean.getCharacteristicName());
        characteristicUuidValue.setText(characteristicItemBean.getCharacteristicUuid());
        characteristicPropertiesValue.setText(characteristicItemBean.getCharacteristicPropertires());
    }
}
