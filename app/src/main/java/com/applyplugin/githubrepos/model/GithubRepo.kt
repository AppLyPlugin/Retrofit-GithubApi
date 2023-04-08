package com.applyplugin.githubrepos.model

data class GithubRepo(
    val name: String?,
    val url: String?,
    val owner: GithubUser
){
    override fun toString() = "$name = $url"
}
