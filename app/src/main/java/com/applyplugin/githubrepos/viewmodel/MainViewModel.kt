package com.applyplugin.githubrepos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applyplugin.githubrepos.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class MainViewModel : ViewModel() {

    private val compositeDisposable= CompositeDisposable()

    val tokenLd = MutableLiveData<String>()
    val errorLd = MutableLiveData<String>()
    val reposLd = MutableLiveData<List<GithubRepo>>()
    val prsLd = MutableLiveData<List<GithubPR>>()
    val commentsLd = MutableLiveData<List<GithubComment>>()
    val postCommentsLd = MutableLiveData<Boolean>()

    fun getToken(clienId: String, clientSecret: String, code: String){
        compositeDisposable.add(
            GithubService.getUnauthorizeApi().getAuthToken(clienId, clientSecret, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<GithubToken>(){
                    override fun onSuccess(t: GithubToken) {
                        tokenLd.value = t.accessToken
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        errorLd.value = "Cannot Load Token"
                    }

                })
        )
    }

    fun onLoadRepositories(token: String){
        compositeDisposable.add(
            GithubService.getAuthorizedApi(token).getAllRepos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<GithubRepo>>(){
                    override fun onSuccess(t: List<GithubRepo>) {
                        reposLd.value = t
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        errorLd.value = "Cannot Load Repositoties"
                    }

                })
        )
    }

    fun onLoadPRs(token: String, owner: String?, repository: String?){
        if(!owner.isNullOrEmpty() && !repository.isNullOrEmpty()) {
            compositeDisposable.add(
                GithubService.getAuthorizedApi(token).getPRs(owner, repository)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object: DisposableSingleObserver<List<GithubPR>>(){
                        override fun onSuccess(t: List<GithubPR>) {
                            prsLd.value = t
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            errorLd.value = "Cannot Load PRs"
                        }

                    })
            )
        }
    }

    fun onLoadComments(token: String, owner: String?, repository: String?, pullNumber: String?){
        if(!owner.isNullOrEmpty() && !repository.isNullOrEmpty() && !pullNumber.isNullOrEmpty()) {
            compositeDisposable.add(
                GithubService.getAuthorizedApi(token).getCommnets(owner, repository, pullNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object: DisposableSingleObserver<List<GithubComment>>(){
                        override fun onSuccess(t: List<GithubComment>) {
                            commentsLd.value = t
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            errorLd.value = "Cannot Load Comments"
                        }

                    })
            )
        }
    }

    fun onPostComment(token: String, repository: GithubRepo, pullNumber: String?, content: GithubComment){
        if(!repository.owner.login.isNullOrEmpty() && !repository.name.isNullOrEmpty() && !pullNumber.isNullOrEmpty()) {
            compositeDisposable.add(
                GithubService.getAuthorizedApi(token).postComment(repository.owner.login, repository.name, pullNumber, content)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object: DisposableSingleObserver<ResponseBody>(){
                        override fun onSuccess(t: ResponseBody) {
                            postCommentsLd.value = true
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            errorLd.value = "Cannot create comment"
                        }

                    })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}