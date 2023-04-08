package com.applyplugin.githubrepos.model

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface GithubApi {

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("https://github.com/login/oauth/access_token")
    fun getAuthToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Single<GithubToken>

    @GET("user/repos")
    fun getAllRepos(): Single<List<GithubRepo>>

    @GET("/repos/{owner}/{repo}/pulls")
    fun getPRs(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Single<List<GithubPR>>

    @GET("/repos/{owner}/{repo}/issues/{issue_number}/comments")
    fun getCommnets(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") pullNumber: String
    ): Single<List<GithubComment>>

    @POST("/repos/{owner}/{repo}/issues/{issue_number}/comments")
    fun postComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") pullNumber: String,
        @Body comment: GithubComment
    ): Single<ResponseBody>

}