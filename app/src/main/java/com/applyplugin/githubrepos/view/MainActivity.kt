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
                // Load PullRequests
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
                // Load comments
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
        viewModel.tokenLd.observe(this, Observer {token ->
            if(token.isNotEmpty()){
                this.token = token
                loadReposButton.isEnabled = true
                Toast.makeText(this@MainActivity, "Authentication Successful", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.errorLd.observe(this, Observer {message ->
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

    }

    fun onPostComment(view: View) {

    }

}
