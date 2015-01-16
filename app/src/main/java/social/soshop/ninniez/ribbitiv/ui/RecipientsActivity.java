package social.soshop.ninniez.ribbitiv.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import social.soshop.ninniez.ribbitiv.Adapter.UserAdapter;
import social.soshop.ninniez.ribbitiv.R;
import social.soshop.ninniez.ribbitiv.utils.FileHelper;
import social.soshop.ninniez.ribbitiv.utils.ParseConstants;


public class RecipientsActivity extends Activity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();

    //create member variable of list the contain ParseUser data.
    protected List<ParseUser> mFriends;
    //declare relation members to restore parse relations.
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    private ProgressBar mProgressBar;
    protected Button mSendButton;

    //member variable to store path/data/etc. from the intent that start this activity
    protected Uri mMediaUri;
    protected String mFileTYpe;

    //change to gridview
    protected GridView mGridView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);


        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        mSendButton = (Button) findViewById(R.id.sendToRecipientsButton);

        mMediaUri = getIntent().getData();
        mFileTYpe = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);



    }
    public void onResume() {
        super.onResume();

        mProgressBar.setVisibility(View.VISIBLE);
        //update relation and current user everytime the activity resume
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);


        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];

                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;

                    }

                    //change back to use custom adapter
                    if (mGridView.getAdapter() == null) {

                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);

                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(R.string.error_title);
                    builder.setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
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

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//        //show when there is item checked, hide when none is checked.
//        if(l.getCheckedItemCount()>0) {
//            mSendButton.setVisibility(View.VISIBLE);
//        } else{
//            mSendButton.setVisibility(View.INVISIBLE);
//        }
//         mSendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ParseObject message = createMessage();
//                if (message == null){
//                    //there was an error
//                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
//                    builder.setMessage(getString(R.string.error_selecting_file));
//                    builder.setTitle(getString(R.string.error_selecting_file_title));
//                    builder.setPositiveButton(android.R.string.ok,null);
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                } else {
//                    send(message);
//                    finish(); //finish the activity, close the activity and go back to previous the send to here which is MainActivity
//                }
//            }
//        });
//
//    }

    private void send(ParseObject message) {

        message.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if(e == null) {
                    //Success
                    Toast.makeText(RecipientsActivity.this,getString(R.string.success_message),Toast.LENGTH_LONG).show();
                    sendPushNotifications();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(getString(R.string.error_sending_message));
                    builder.setTitle(getString(R.string.error_selecting_file_title));
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected void sendPushNotifications() {
        //set up the query to push notification
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientsIds());
        //send push notification
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message , ParseUser.getCurrentUser().getUsername()));

        push.sendInBackground();
    }

    protected ParseObject createMessage() {
        //create brand new parse object.
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES); //create new class by just adding a new name
        message.put(ParseConstants.KEY_SENDER_IDS,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientsIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileTYpe);

        //attach video/image itself to the ParseObject and upload it. USE HELPER CLASS from the Github: FileHelper and ImageResizer.
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if(fileBytes == null){
            return null; //prevent crash and let other user try different files
        } else{
            if(mFileTYpe.equals(ParseConstants.TYPE_IMAGE)){
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this,mMediaUri,mFileTYpe);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE,file);
            return message;
        }


    }

    protected ArrayList<String> getRecipientsIds() {
        ArrayList recipientsIds = new ArrayList<String>();
        //get id of all friends in the list
        for(int i = 0 ; i < mGridView.getCount(); i++){
            //if item is checked so the user is the recipients
            if(mGridView.isItemChecked(i)){
                recipientsIds.add(mFriends.get(i).getObjectId());
            }
        }

        return recipientsIds;
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //only add image view when recipients is checked by use very similar code from EditFriendsActivity
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView); //important to set "view.findViewbyId" for each specific view not "findViewById" only
            if (mGridView.isItemChecked(position)){
                //add recipients
                //after checked we will show the overlay
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                //remove recipients
                checkImageView.setVisibility(View.INVISIBLE);
            }


            if(mGridView.getCheckedItemCount()>0) {
                mSendButton.setVisibility(View.VISIBLE);
            } else{
                mSendButton.setVisibility(View.INVISIBLE);
            }
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseObject message = createMessage();
                    if (message == null){
                        //there was an error
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                        builder.setMessage(getString(R.string.error_selecting_file));
                        builder.setTitle(getString(R.string.error_selecting_file_title));
                        builder.setPositiveButton(android.R.string.ok,null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        send(message);
                        finish(); //finish the activity, close the activity and go back to previous the send to here which is MainActivity
                    }
                }
            });


        }
    };
}
