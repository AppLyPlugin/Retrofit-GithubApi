package com.applyplugin.githubrepos.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.applyplugin.githubrepos.R
import com.applyplugin.githubrepos.model.GithubComment
import com.applyplugin.githubrepos.model.GithubPR
import com.applyplugin.githubrepos.model.GithubRepo
import com.applyplugin.githubrepos.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        repositoriesSpinner.isEnabled = false
        repositoriesSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("No repositories available")
        )
        repositoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent?.selectedItem is GithubRepo) {
                    val currentRepo = parent.selectedItem as GithubRepo
                    token?.let {
                        viewModel.onLoadPRs(it, currentRepo.owner.login, currentRepo.name)
                    }
                }
            }
        }


        prsSpinner.isEnabled = false
        prsSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("Please select repository")
        )
        prsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (parent?.selectedItem is GithubPR) {
                    val githubPR = parent.selectedItem as GithubPR
                    val currentRepo = repositoriesSpinner.selectedItem as GithubRepo
                    token?.let {
                        viewModel.onLoadComments(
                            it,
                            githubPR.user?.login,
                            currentRepo.name,
                            githubPR.number
                        )
                    }
                }

            }
        }


        commentsSpinner.isEnabled = false
        commentsSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("Please select PR")
        )


        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.tokenLd.observe(this, Observer { token ->
            if (token.isNotEmpty()) {
                this.token = token
                loadReposButton.isEnabled = true
                Toast.makeText(this@MainActivity, "Authentication Successful", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@MainActivity, "Authentication Failed", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        viewModel.reposLd.observe(this, Observer { repoList ->
            if (!repoList.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    repoList
                )
                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = true
            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf(
                        "User has no repositories"
                    )
                )
                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = false
            }

        })

        viewModel.prsLd.observe(this, Observer { prList ->
            if (!prList.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    prList
                )
                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = true
            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf(
                        "User has no PRs"
                    )
                )
                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = false
            }

        })

        viewModel.commentsLd.observe(this, Observer { commentList ->
            if (!commentList.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    commentList
                )
                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = true
                commentET.isEnabled = true
                postCommentButton.isEnabled = true
            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf(
                        "PR has no Comments"
                    )
                )
                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = false
                commentET.isEnabled = true
                postCommentButton.isEnabled = true
            }

        })

        viewModel.postCommentsLd.observe(this, Observer { success ->
            if (success) {
                commentET.setText("")
                Toast.makeText(this@MainActivity, "Comment posted", Toast.LENGTH_SHORT).show()
                token?.let {
                    val currentRepo = repositoriesSpinner.selectedItem as GithubRepo
                    val currentPR = prsSpinner.selectedItem as GithubPR
                    viewModel.onLoadComments(it, currentPR.user?.login, currentRepo.name, currentPR.number)
                }
            }else{
                Toast.makeText(this@MainActivity, "Cannot post comment", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.errorLd.observe(this, Observer { message ->
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        })
    }

    fun onAuthenticate(view: View) {
        val oauthUrl = getString(R.string.oauthUrl)
        val clientId = getString(R.string.clientId)
        val callbackUrl = getString(R.string.callbackUrl)
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("$oauthUrl?client_id=$clientId&scope=repo&redirect_uri=$callbackUrl")
        )
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data
        val callbackUrl = getString(R.string.callbackUrl)
        if (uri != null && uri.toString().startsWith(callbackUrl)) {
            val code = uri.getQueryParameter("code")
            code?.let {
                val clientId = getString(R.string.clientId)
                val clientSecret = getString(R.string.clientSecret)
                viewModel.getToken(clientId, clientSecret, code)
            }
        }
    }

    fun onLoadRepos(view: View) {
        token?.let {
            viewModel.onLoadRepositories(it)
        }
    }

    fun onPostComment(view: View) {
        val comment = commentET.text.toString()
        if (comment.isNotEmpty()) {
            val currentRepo = repositoriesSpinner.selectedItem as GithubRepo
            val currentPr = prsSpinner.selectedItem as GithubPR
            token?.let {
                viewModel.onPostComment(
                    it,
                    currentRepo,
                    currentPr.number,
                    GithubComment(comment, null)
                )
            }
        } else {
            Toast.makeText(this@MainActivity, "Please enter a comment", Toast.LENGTH_SHORT).show()
        }
    }

}
