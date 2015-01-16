package social.soshop.ninniez.ribbitiv.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import social.soshop.ninniez.ribbitiv.R;
import social.soshop.ninniez.ribbitiv.utils.MD5Util;

/**
 * Created by Ninniez on 12/15/2014.
 */

//CREATE OUR OWN MESSAGE ADAPTER
public class UserAdapter extends ArrayAdapter<ParseUser>{

    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users){
        super(context, R.layout.user_item, users); //very important
        mContext = context;
        mUsers = users;

    }

    //VERY IMPORTANT METHOD TO INCLUDE
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);//delete because we don't actually care what happen in getView.
        ViewHolder holder;

        //Very important check, by not creating the view and inflate the view every single list item. WE HAVE TO CHECK IT IF IT IS ALREADY CALLED OR NOT.
        if (convertView ==null) {
            //the idea is to inflate the convertView to the holder?
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null); //parameter is null and it will get added to the list view by default.
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.checkImageView = (ImageView) convertView.findViewById(R.id.checkImageView);

            convertView.setTag(holder); //very important if not code, that app will crash after scrolling. This is to set the TAG to be used in else.

        } else { //this is why we create holder to make it more efficient
            holder = (ViewHolder) convertView.getTag();

        }

        ParseUser user = mUsers.get(position);
        //get email
        String email = user.getEmail().toLowerCase(); //also set to lower case as md5 require.
        if (email.equals("")){
            //email is empty
            holder.userImageView.setImageResource(R.drawable.avatar_empty); //set in holder for much more efficient use.
        } else {
            //grab image from avatar
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";

            //use picasso
            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty) //when 404 is return, the place holder image will be used.
                    .into(holder.userImageView);
        }


        //change the icon base on file type
//        if (user.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
//            holder.userImageView.setImageResource(R.drawable.ic_picture);
//        } else {
//            holder.userImageView.setImageResource(R.drawable.ic_video);
//
//        }
        holder.nameLabel.setText(user.getUsername());

        //check that users in adapter is friends or not.
        GridView gridView = (GridView)parent;
        if (gridView.isItemChecked(position)){
            //friend is checked
            holder.checkImageView.setVisibility(View.VISIBLE);
        } else {
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView userImageView;
        TextView nameLabel;

        //add holder for the checked ImageView
        ImageView checkImageView;


    }

    public void refill(List<ParseUser> users){
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();

    }
}
