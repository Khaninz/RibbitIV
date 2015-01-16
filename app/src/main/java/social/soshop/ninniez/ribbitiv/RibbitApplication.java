package social.soshop.ninniez.ribbitiv;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import social.soshop.ninniez.ribbitiv.utils.ParseConstants;

/**
 * Created by Ninniez on 12/12/2014.
 */
public class RibbitApplication extends Application {
    public void onCreate() {
        Parse.initialize(this, "WirdcuneVsQGpULYUx9XfCgKF9s2DjsQxIZa2Zel", "vi6TbL3bzMcnHdIekjsbx7yKP3bHhOlcV0roTyBX");



    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID,user.getObjectId());
        installation.saveInBackground();
    }

}
