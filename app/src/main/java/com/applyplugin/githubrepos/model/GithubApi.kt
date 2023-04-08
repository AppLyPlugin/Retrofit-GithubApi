package com.applyplugin.githubrepos.model

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface GithubApi {

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("https://github.com/login/oauth/access_token")
    fun getAuthToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Single<GithubToken>

}