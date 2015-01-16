package social.soshop.ninniez.ribbitiv.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

import social.soshop.ninniez.ribbitiv.R;
import social.soshop.ninniez.ribbitiv.utils.ParseConstants;

/**
 * Created by Ninniez on 12/15/2014.
 */

//CREATE OUR OWN MESSAGE ADAPTER
public class MessageAdapter extends ArrayAdapter<ParseObject>{

    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter (Context context, List<ParseObject> messages){
        super(context, R.layout.message_item, messages); //very important
        mContext = context;
        mMessages = messages;

    }

    //VERY IMPORTANT METHOD TO INCLUDE
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);//delete because we don't actually care what happen in getView.
        ViewHolder holder;

        //Very important check, by not creating the view and inflate the view every single list item. WE HAVE TO CHECK IT IF IT IS ALREADY CALLED OR NOT.
        if (convertView ==null) {
            //the idea is to inflate the convertView to the holder?
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null); //parameter is null and it will get added to the list view by default.
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder); //very important if not code, that app will crash after scrolling. This is to set the TAG to be used in else.

        } else { //this is why we create holder to make it more efficient
            holder = (ViewHolder) convertView.getTag();

        }

        ParseObject message = mMessages.get(position);
        //ADDED MODIFY TO GET DATE SHWON IN MESSAGES
        Date createdAt = message.getCreatedAt(); //object from parse
            //best way to get time, for now. 17/12/14
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.getTime(),now, DateUtils.SECOND_IN_MILLIS).toString();
        holder.timeLabel.setText(convertedDate);

        //change the icon base on file type
        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
            holder.iconImageView.setImageResource(R.drawable.ic_picture);
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_video);

        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        //ADDED MODIFY TO GET DATE SHWON IN MESSAGES
        TextView timeLabel;
    }

    public void refill(List<ParseObject> message){
        message.clear();
        message.addAll(mMessages);
        notifyDataSetChanged();

    }
}
