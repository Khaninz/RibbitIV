package social.soshop.ninniez.ribbitiv.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import social.soshop.ninniez.ribbitiv.Adapter.UserAdapter;
import social.soshop.ninniez.ribbitiv.R;
import social.soshop.ninniez.ribbitiv.utils.ParseConstants;


public class EditFriendsActivity extends Activity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    private ProgressBar mProgressBar;

    //create member variable of list the contain ParseUser data.
    protected List<ParseUser> mUsers;
    //declare relation members to restore parse relations.
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    //change from listview to gridview
    protected GridView mGridView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);

        mProgressBar = (ProgressBar) findViewById(R.id.edit_friends_progressbar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mGridView = (GridView) findViewById(R.id.friendsGrid);

        //change from getListView() to only mGridView.
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

    }




    //create on resume method to refreah friends list every time.
    @Override
    protected void onResume() {
        super.onResume();

        //update relation and current user everytime the activity resume
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        mProgressBar.setVisibility(View.VISIBLE);

        //set query
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);

        //execute query
        query.findInBackground(new FindCallback<ParseUser>() {


            @Override
            public void done(List<ParseUser> users, ParseException e) {

                mProgressBar.setVisibility(View.INVISIBLE);

                if (e == null){
                    //success
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];

                    int i = 0;
                    for(ParseUser user: mUsers){
                        usernames[i] = user.getUsername();
                        i++;

                    }

                    //change back to use custom adapter
                    if (mGridView.getAdapter() == null) {

                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);

                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mUsers);
                    }

                    //add check mark
                    addFriendCheckMark();




                }else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(R.string.error_title);
                    builder.setPositiveButton(android.R.string.ok,null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    private void addFriendCheckMark() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if(e == null){
                    //list return - look for a match
                    for (int i = 0; i < mUsers.size(); i++ ){
                        ParseUser user = mUsers.get(i);

                        for(ParseUser friend : friends){
                            if(friend.getObjectId().equals(user.getObjectId())){
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }

                } else {
                    Log.e(TAG,e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //START-create what happen when item on list is click

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//
//        if (getListView().isItemChecked(position)){
//            //add friend
//            //add friends when the user is tapped on. LOCALLY
//            mFriendsRelation.add(mUsers.get(position));
//
//
//        } else {
//            //remove friend
//            //remove friends when the user is tapped on. LOCALLY
//            mFriendsRelation.remove(mUsers.get(position));
//            //START-save to backend.
//
//        }
//        //START-save to backend. no matter what add or remove
//        mCurrentUser.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e != null){
//                    Log.e(TAG, e.getMessage());
//                }
//
//            }
//        });
//
//
//
//    }
    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            //reference to image view
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
            if (mGridView.isItemChecked(position)){
                //add friend
                //add friends when the user is tapped on. LOCALLY
                mFriendsRelation.add(mUsers.get(position));
                //after checked we will show the overlay
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                //remove friend
                //remove friends when the user is tapped on. LOCALLY
                mFriendsRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
            }


            //START-save to backend. no matter what add or remove
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        Log.e(TAG, e.getMessage());
                    }

                }
            });
        }
    };
}
