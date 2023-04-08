package com.applyplugin.githubrepos.model

data class GithubComment(
    val body: String?,
    val id: String?
) {
    override fun toString(): String = "$body = $id"
}
