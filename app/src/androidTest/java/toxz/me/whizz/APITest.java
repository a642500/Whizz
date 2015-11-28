package toxz.me.whizz;

import android.test.AndroidTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

import retrofit.Retrofit;
import toxz.me.whizz.net.APIServer;
import toxz.me.whizz.spy.PhotoSpy;

/**
 * Created by Carlos on 11/27/15.
 */

@RunWith(JUnit4.class)
public class APITest extends AndroidTestCase {
    public static final String ID = "test";
    private APIServer mServer = new Retrofit.Builder().baseUrl("http://localhost:4567/").build().create(APIServer.class);

    @Test
    public void testUpload() throws IOException {
        mServer.upload(ID, new PhotoSpy.Photo(new File("/Users/Carlos/AndroidStudioProjects/Whizz/test/test.png")).getContent()).execute();
    }


}
