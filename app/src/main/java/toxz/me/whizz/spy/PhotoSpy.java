package toxz.me.whizz.spy;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.List;

/**
 * Created by Carlos on 11/26/15.
 */
public class PhotoSpy implements Spy {


    @Override
    public List<Swag> getAllSwag() {
        return null;
    }

    @Override
    public void registerSpyListener(OnNewSwagListener listener) {

    }

    public static class Photo implements Spy.Swag {
        private final long mTime = System.currentTimeMillis();
        private final File mFile;

        public Photo(File file) {
            this.mFile = file;
        }

        public RequestBody getContent() {
            return RequestBody.create(MediaType.parse("image/*"), mFile);
        }

        public long getContentTime() {
            return mTime;
        }
    }
}
