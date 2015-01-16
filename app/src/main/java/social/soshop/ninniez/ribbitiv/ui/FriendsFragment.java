package social.soshop.ninniez.ribbitiv.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import social.soshop.ninniez.ribbitiv.Adapter.UserAdapter;
import social.soshop.ninniez.ribbitiv.R;
import social.soshop.ninniez.ribbitiv.utils.ParseConstants;

/**
 * Created by Ninniez on 12/12/2014.
 */
public class FriendsFragment extends android.support.v4.app.Fragment{

    public static final String TAG = FriendsFragment.class.getSimpleName();

    //create member variable of list the contain ParseUser data.
    protected List<ParseUser> mFriends;
    //declare relation members to restore parse relations.
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    private ProgressBar mProgressBar;
    protected GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //similar to set content view method in normal activity
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);


        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);
        //created empty text when view is empyy. can not longer use the same way as in inbox fragment.
        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);




        return rootView;
    }

    @Override
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

                    if (mGridView.getAdapter() == null) {

                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);

                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage());
                    builder.setTitle(R.string.error_title);
                    builder.setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            }
        });
    }
}
