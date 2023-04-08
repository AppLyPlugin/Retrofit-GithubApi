package com.applyplugin.githubrepos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.applyplugin.githubrepos.model.GithubService
import com.applyplugin.githubrepos.model.GithubToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {

    private val compositeDisposable= CompositeDisposable()

    val tokenLd = MutableLiveData<String>()
    val errorLd = MutableLiveData<String>()

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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}