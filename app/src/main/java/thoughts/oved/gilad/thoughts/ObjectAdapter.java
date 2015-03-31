package thoughts.oved.gilad.thoughts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by gilad on 3/31/15.
 */
public class ObjectAdapter extends ArrayAdapter<ParseObject>{

        ArrayList<ParseObject> thoughts;

        private static class ViewHolder {
            private TextView titleView;
            private TextView upVotesView;
            private TextView downVotesView;
            private ViewHolder(View rootView) {
                titleView = (TextView) rootView.findViewById(R.id.helloText);
                upVotesView = (TextView) rootView.findViewById(R.id.item_swipe_left_indicator);
                downVotesView = (TextView) rootView.findViewById(R.id.item_swipe_right_indicator);
            }
        }

        public ObjectAdapter(Context context, int textViewResourceId, ArrayList<ParseObject> items) {
            super(context, textViewResourceId, items);
            thoughts = new ArrayList<>();
            thoughts = new ArrayList<ParseObject>();
            thoughts = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.item, parent, false);

                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ParseObject item = thoughts.get(position);
            if (item != null) {
                // My layout has only one TextView
                // do whatever you want with your string and long
                viewHolder.titleView.setText(item.getString("thought"));
                viewHolder.upVotesView.setText("" + item.getNumber("upVotes"));
                viewHolder.downVotesView.setText("" + ((int)item.getNumber("totalVotes") - (int)item.getNumber("upVotes")));
            }

            return convertView;
        }

}
