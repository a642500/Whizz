package toxz.me.whizz.spy;

import java.io.OutputStream;
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

    public class Photo implements Spy.Swag {
        private final long mTime = System.currentTimeMillis();

        public OutputStream getContent() {
            return null;
        }

        @Override
        public String getContentType() {
            return "image/*";
        }

        public long getContentTime() {
            return mTime;
        }
    }
}
