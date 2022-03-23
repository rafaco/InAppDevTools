/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * This is a modified source from project Chuck, which is available under
 * Apache License, Version 2.0 at https://github.com/jgilfelt/chuck
 *
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
 * Copyright (C) 2017 Jeff Gilfelt.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 *  ChangeLog:
 *     - Added previous attribution notice and this changelog
 *     - Renamed from SampleApiService to HttpBinService
 *     - Make all methods public
 *     - Include simulation method
 *     - Namespace changed
 */

package org.inappdevtools.library.logic.external.chuck;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class HttpBinService {

    static final String BASE_URL = "https://httpbin.org";

    public static HttpbinApi getInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(HttpbinApi.class);
    }

    public static class Data {
        final String thing;
        public Data(String thing) {
            this.thing = thing;
        }
    }

    public interface HttpbinApi {
        @GET("/get")
        Call<Void> get();
        @POST("/post")
        Call<Void> post(@Body Data body);
        @PATCH("/patch")
        Call<Void> patch(@Body Data body);
        @PUT("/put")
        Call<Void> put(@Body Data body);
        @DELETE("/delete")
        Call<Void> delete();
        @GET("/status/{code}")
        Call<Void> status(@Path("code") int code);
        @GET("/stream/{lines}")
        Call<Void> stream(@Path("lines") int lines);
        @GET("/stream-bytes/{bytes}")
        Call<Void> streamBytes(@Path("bytes") int bytes);
        @GET("/delay/{seconds}")
        Call<Void> delay(@Path("seconds") int seconds);
        @GET("/redirect-to")
        Call<Void> redirectTo(@Query("url") String url);
        @GET("/redirect/{times}")
        Call<Void> redirect(@Path("times") int times);
        @GET("/relative-redirect/{times}")
        Call<Void> redirectRelative(@Path("times") int times);
        @GET("/absolute-redirect/{times}")
        Call<Void> redirectAbsolute(@Path("times") int times);
        @GET("/image")
        Call<Void> image(@Header("Accept") String accept);
        @GET("/gzip")
        Call<Void> gzip();
        @GET("/xml")
        Call<Void> xml();
        @GET("/encoding/utf8")
        Call<Void> utf8();
        @GET("/deflate")
        Call<Void> deflate();
        @GET("/cookies/set")
        Call<Void> cookieSet(@Query("k1") String value);
        @GET("/basic-auth/{user}/{passwd}")
        Call<Void> basicAuth(@Path("user") String user, @Path("passwd") String passwd);
        @GET("/drip")
        Call<Void> drip(@Query("numbytes") int bytes, @Query("duration") int seconds, @Query("delay") int delay, @Query("code") int code);
        @GET("/deny")
        Call<Void> deny();
        @GET("/cache")
        Call<Void> cache(@Header("If-Modified-Since") String ifModifiedSince);
        @GET("/cache/{seconds}")
        Call<Void> cache(@Path("seconds") int seconds);
    }

    public static void simulation() {
        HttpBinService.HttpbinApi api = HttpBinService.getInstance();
        Callback<Void> cb = new Callback<Void>() {
            @Override public void onResponse(Call call, Response response) {}
            @Override public void onFailure(Call call, Throwable t) { t.printStackTrace(); }
        };
        api.get().enqueue(cb);
        api.post(new HttpBinService.Data("posted")).enqueue(cb);
        api.patch(new HttpBinService.Data("patched")).enqueue(cb);
        api.put(new HttpBinService.Data("put")).enqueue(cb);
        api.delete().enqueue(cb);
        api.status(201).enqueue(cb);
        api.status(401).enqueue(cb);
        api.status(500).enqueue(cb);
        api.delay(9).enqueue(cb);
        api.delay(15).enqueue(cb);
        api.redirectTo("https://http2.akamai.com").enqueue(cb);
        api.redirect(3).enqueue(cb);
        api.redirectRelative(2).enqueue(cb);
        api.redirectAbsolute(4).enqueue(cb);
        api.stream(500).enqueue(cb);
        api.streamBytes(2048).enqueue(cb);
        api.image("image/png").enqueue(cb);
        api.gzip().enqueue(cb);
        api.xml().enqueue(cb);
        api.utf8().enqueue(cb);
        api.deflate().enqueue(cb);
        api.cookieSet("v").enqueue(cb);
        api.basicAuth("me", "pass").enqueue(cb);
        api.drip(512, 5, 1, 200).enqueue(cb);
        api.deny().enqueue(cb);
        api.cache("Mon").enqueue(cb);
        api.cache(30).enqueue(cb);
    }
}
