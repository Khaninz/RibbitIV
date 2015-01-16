package social.soshop.ninniez.ribbitiv.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import social.soshop.ninniez.ribbitiv.utils.ParseConstants;
import social.soshop.ninniez.ribbitiv.R;
import social.soshop.ninniez.ribbitiv.Adapter.SectionsPagerAdapter;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    //create variable for reference as a request code.
    public static final int TAKE_PHOT0_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    //Uri = uniform resource identifier, to store out path for saving image file
    protected Uri mMediaUri;



    public static final int FILE_SIZE_LIMIT = 1024*1024*10; //10MB Byte>KB>MB


    //create member variable for listener in camera dialog to shorten the code.
    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0: //take picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mMediaUri == null){ //if nothing in media
                        //display an error
                        Toast.makeText(MainActivity.this,getString(R.string.error_external_storage),Toast.LENGTH_LONG).show();
                    } else {
                        //using camera
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        //start an activity and exit with result return to us.
                        startActivityForResult(takePhotoIntent, TAKE_PHOT0_REQUEST);
                    }
                    break;
                case 1: //Take video
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if (mMediaUri == null) { //if nothing in media
                        //display an error
                        Toast.makeText(MainActivity.this, getString(R.string.error_external_storage), Toast.LENGTH_LONG).show();
                    } else {
                        //using video
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri); //specify path uri
                        //configure the video recording
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10); //set duration limit
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);//specify video quality
                        //start an activity and exit with result return to us.
                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }
                        break;
                case 2: //Choose picture
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT); //get content of any type by default
                    //set type of file to be get
                    choosePhotoIntent.setType("image/*"); //mime? type
                    startActivityForResult(choosePhotoIntent,PICK_PHOTO_REQUEST);

                    break;
                case 3: //Choose video
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT); //get content of any type by default
                    //set type of file to be get
                    chooseVideoIntent.setType("video/*"); //mime? type
                    Toast.makeText(MainActivity.this,getString(R.string.video_file_size_warning),Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);

                    break;
            }
        }

        private Uri getOutputMediaFileUri(int mediaType) {
            //To be safe, you should check that the SDCard is mounted
            //using Environement.getExternalStrorageState() before doing this
            if (isExternalStorageAvailable()){
                //get Uri

                //1.Get the external Storage directory
                String appName = MainActivity.this.getString(R.string.app_name); //get app name to be used as folder/dir name
                File mediaStorageDir =
                        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                appName);

                //2.Create our sud-directory
                if (! mediaStorageDir.exists()){//if storage media does not exist
                    //so create one
                    if (! mediaStorageDir.mkdirs()){//if make fail
                        Log.e(TAG,"Failed to create directory");
                        return null;
                    }
                }
                //3.Create a file name (simply using time stamp
                //4.Create the file
                File mediaFile;
                Date now = new Date();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US ).format(now); //seems long but it's actually a code from a sample in developer site

                String path = mediaStorageDir.getPath() + File.separator;
                //create different file name for video and image
                if (mediaType == MEDIA_TYPE_IMAGE){
                    mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
                } else if (mediaType == MEDIA_TYPE_VIDEO){
                    mediaFile = new File(path + "VID_" + timeStamp + ".mp4");
                } else {
                    return null;
                }

                Log.d(TAG,"File:" + Uri.fromFile(mediaFile) );

                //5.Return the file's Uri
                return Uri.fromFile(mediaFile);


            } else {
                return null;
            }


        }

        private boolean isExternalStorageAvailable() {
            String state = Environment.getExternalStorageState();

            if(state.equals(Environment.MEDIA_MOUNTED)){
                return true;
            } else {
                return false;
            }
        }

    };

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(mSectionsPagerAdapter.getIcon(i))
                            .setTabListener(this));


        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //check if anyone is loggin in.
        ParseUser currentUser = new ParseUser().getCurrentUser();
        if (currentUser == null) {

            navigateToLogin();
        } else {

            Log.i(TAG, currentUser.getUsername());
        }
    }



    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        //add flag to intent, so user can't use 'back' to the main activity
        //read more about Flag in the api intent api document
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
        case R.id.action_logout:
            ParseUser.logOut();
            navigateToLogin();
            break; //must be add in every case
        case R.id.action_edit_friends:
            Intent intent = new Intent(this, EditFriendsActivity.class);
            startActivity(intent);
            break;
        case R.id.action_photo:

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(R.array.camera_choices,mDialogListener);
            AlertDialog dialog = builder.create();
            dialog.show();
            break;



        }

        return super.onOptionsItemSelected(item);
    }

    //method to get result from intent for result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK){ //if result ok
            //add it to gallery

            //pick photo will use the same uri as the data, so we need to specify the same location.?
            if(requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST){
                if (data == null){ // check normal error
                    Toast.makeText(this,getString(R.string.gerneral_error),Toast.LENGTH_LONG).show();
                } else {

                    mMediaUri = data.getData();
                }
                //check the video size after get the media data
                if (requestCode == PICK_VIDEO_REQUEST){
                    //make sure the file size is less than 10MB
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);//pull the number of byte
                        fileSize = inputStream.available(); //data of byte available
                    } catch (FileNotFoundException e) {

                        Toast.makeText(this,getString(R.string.error_opening_file),Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e) {
                        Toast.makeText(this,getString(R.string.error_opening_file),Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally { //finally : will get execute no matter what happen

                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            //Intentionally blank
                        }
                    }

                    if (fileSize >= FILE_SIZE_LIMIT){
                        //file size is over limit
                        Toast.makeText(this,getString(R.string.error_file_size_too_large),Toast.LENGTH_LONG).show();
                        //nothing happen next so return.
                        return;
                    }
                }

            }
            else { // as pick photo is already in a gallery, taking new file need to be broadcast to let gallery catch it.
                //broadcast intent
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //specify the path for the file
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }
        //start recipients activity when result is ok and no error(if error just return to normal activity.
        Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
        recipientsIntent.setData(mMediaUri);
            //also set file type information via the intent
            String fileType;
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOT0_REQUEST){//these are img files
                fileType = ParseConstants.TYPE_IMAGE;
            } else {
                fileType = ParseConstants.TYPE_VIDEO;
            }
        recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE,fileType);

        startActivity(recipientsIntent);


        } else if (resultCode != RESULT_CANCELED){
            Toast.makeText(this,getString(R.string.gerneral_error), Toast.LENGTH_LONG).show();
        }



    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }







}
