package toxz.me.whizz.net;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import toxz.me.whizz.net.model.ApiModel;

/**
 * Api delegate by retrofit
 * Created by Carlos on 11/27/15.
 */
public interface APIServer {
    @Multipart
    @POST("user/{id}/uploads")
    Call<ApiModel> upload(@Path("id") String id, @Part("file") RequestBody file);


    private void test() {
        APIServer server = new Retrofit.Builder().baseUrl("129.128.32.12").build().create(APIServer.class);
        server.upload("87493583475", RequestBody.create(MediaType.parse("image/png"), new File("/sdcard/DICM/test.png")));
    }
}
