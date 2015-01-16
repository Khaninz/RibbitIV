package social.soshop.ninniez.ribbitiv.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import social.soshop.ninniez.ribbitiv.R;


public class ViewImageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView mImageView = (ImageView) findViewById(R.id.imageView);
        Uri        imageUri = getIntent().getData();

        //android sdk does not allow us to get the image directly from the web or Uri, getImageUri() only apply for the image on the device.
        //so we will use Picasso library to cache and get image from the web.
        Picasso.with(this).load(imageUri.toString()).into(mImageView);

        Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 10*1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_image, menu);
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
}
