package tk.uditsharma.clientapp.model;

import okhttp3.ResponseBody;
import retrofit2.http.Query;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

public interface UserDataAPI {

    String BASE_URL = "http://192.168.1.219:8080/RestfulWebServicePlaces/app/";

    @GET("authenticate/")
    Call<LoginData> login();

    @GET("users/userlist")
    Single<List<User>> getUserList();

    @FormUrlEncoded
    @POST("register/doregister")
    Call<RegData> register(@Field("name") String name, @Field("username") String uName, @Field("password") String pwd);

    @GET("places/userplaces")
    Single<List<AllPlacesResponse>> getPlaces(@Query("userid") String uId);

    @GET("places/getcomments")
    Single<List<CommentResponse>> getComments(@Query("placeid") String pId);

    @FormUrlEncoded
    @POST("places/addcomment")
    Single<ResponseBody> postComment(@Field("userid") String userId, @Field("comment") String comText, @Field("placeid") String placeId);

    @GET("places/deletecomment")
    Single<ResponseBody> deleteComment(@Query("userid") String uId, @Query("commentid") int cId);

    @FormUrlEncoded
    @POST("places/updatecomment")
    Single<ResponseBody> editComment(@Field("userid") String userId, @Field("comment") String comText, @Field("commentid") int comId);

    @GET("places/addplace")
    Single<ResponseBody> addPlace(@Query("userid") String uId, @Query("placeid") String pId, @Query("date") String cDate);

    @GET("places/deleteplace")
    Single<ResponseBody> deletePlace(@Query("userid") String uId, @Query("placeid") String pId, @Query("date") String cDate);

}
