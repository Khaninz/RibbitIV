package social.soshop.ninniez.ribbitiv.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import social.soshop.ninniez.ribbitiv.Adapter.MessageAdapter;
import social.soshop.ninniez.ribbitiv.R;
import social.soshop.ninniez.ribbitiv.utils.ParseConstants;

/**
 * Created by Ninniez on 12/12/2014.
 */
public class InboxFragment extends android.support.v4.app.ListFragment{

    protected List<ParseObject> mMessages;

    private ProgressBar mProgressBar;

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //similar to set content view method in normal activity
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.swipeRefresh1,
                R.color.swipeRefresh2,
                R.color.swipeRefresh3,
                R.color.swipeRefresh4);


        return rootView;






    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Toast.makeText(getActivity(),"We are refreshing!",Toast.LENGTH_LONG).show();
            retrieveMessages();
        }
    };


    @Override
    public void onResume() {

        mProgressBar.setVisibility(View.VISIBLE);

        super.onResume();
        retrieveMessages();


    }

    private void retrieveMessages() {
        //scan/query for message, show it in list. by matching the current user id with the recipients ids.
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);

        //query throught message class in parse to query only message that match the current user id.
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addAscendingOrder(ParseConstants.KEY_CREATED_AT);//arrange the query with the created time.



        //start find/query the message class
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                mProgressBar.setVisibility(View.INVISIBLE);

                //we will end the resume after finish refreshing, but we have to make sure that refreshing is actually happen before we end it.
                if (mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);

                }

                if (e==null){
                    //we found messages
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;

                    }

                    if(getListView().getAdapter()==null) {//use condition to only load the adapter once, not every time on resume
                        //previously adapter is being called every time on resume, result is the view will set to top.
                        MessageAdapter adapter =
                                new MessageAdapter(getListView().getContext(), mMessages);

//                      ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);

                        setListAdapter(adapter);
                    } else {
                        //refill the adapter
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);//get file type
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);// get the file in parse object
        Uri fileUri = Uri.parse(file.getUrl());//get the uri of the file.

        //check whether to send image or the video intent
        if(messageType.equals(ParseConstants.TYPE_IMAGE)){
            //view the image
            Intent intent = new Intent(getActivity(),ViewImageActivity.class); //we can also use getActivity() as another way to get the context.
            intent.setData(fileUri);
            startActivity(intent);
        } else {
            //view the video
            //use an intent to use another app to play video
            Intent intent = new Intent(Intent.ACTION_VIEW,fileUri);
            intent.setDataAndType(fileUri,"video/*");
            startActivity(intent);
        }

        //Delete it
       List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);

        if (ids.size() == 1){
            //so we know this is the last recipient, so we can delete them.
            message.deleteInBackground();
        }else {
            //remove the recipient and save
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS,idsToRemove);
            message.saveInBackground();
        }

    }
}
