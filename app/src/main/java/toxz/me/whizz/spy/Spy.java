package toxz.me.whizz.spy;

import java.io.OutputStream;
import java.util.List;

/**
 * Created by Carlos on 11/26/15.
 */
public interface Spy {
    List<Swag> getAllSwag();

    void registerSpyListener(OnNewSwagListener listener);

    interface Swag {
        OutputStream getContent();

        String getContentType();

        long getContentTime();
    }

    interface OnNewSwagListener {
        void onNew(Swag swag);
    }
}
