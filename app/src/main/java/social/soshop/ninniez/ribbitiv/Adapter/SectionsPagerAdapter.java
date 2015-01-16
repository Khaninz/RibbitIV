package social.soshop.ninniez.ribbitiv.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import social.soshop.ninniez.ribbitiv.ui.InboxFragment;
import social.soshop.ninniez.ribbitiv.R;
import social.soshop.ninniez.ribbitiv.ui.FriendsFragment;

/**
 * Created by Ninniez on 12/12/2014.
 */
/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        //define context to be used for required method
        mContext = context;
    }

    @Override
    //get fragment Item with its linked layout to be show
    public Fragment getItem(int position) {
        //implement code to switch between 2 fragments

        switch (position){
            case 0:
                return new InboxFragment();
            case 1:
                return new FriendsFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);

        }
        return null;
    }

    public int getIcon(int position){
        switch (position){
            case 0:
                return R.drawable.ic_tab_inbox;
            case 1:
                return R.drawable.ic_tab_friends;
        }

        return R.drawable.ic_tab_inbox; //just in case something goes wrong

    }
}
