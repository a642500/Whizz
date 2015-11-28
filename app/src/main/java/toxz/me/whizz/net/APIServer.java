package toxz.me.whizz.net;

import com.squareup.okhttp.RequestBody;

import retrofit.Call;
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
}
