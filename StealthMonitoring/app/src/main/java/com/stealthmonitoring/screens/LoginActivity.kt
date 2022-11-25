package com.stealthmonitoring.screens

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.stealthmonitoring.BuildConfig
import com.stealthmonitoring.api.ApiImplementation
import com.stealthmonitoring.databinding.ActivityLoginBinding
import com.stealthmonitoring.model.LoginModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    val tag: String = javaClass.simpleName

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.insetsController?.hide(WindowInsets.Type.statusBars())

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        isNetworkAvailable(this)
        binding.passwordToggle.setOnClickListener {
            togglePassVisibility(binding.inputPassword, binding.passwordToggle)
        }
        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        if (BuildConfig.DEBUG) {
            val name = "mgtest"
            binding.inputUsername.setText(name)
            val pass = "Password123!"
            binding.inputPassword.setText(pass)
        }
    }

    private fun togglePassVisibility(editText: EditText, indicator: ImageView) {
        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            indicator.setImageDrawable(
                ContextCompat.getDrawable(
                    editText.context,
                    com.stealthmonitoring.R.drawable.visibility
                )
            )
            indicator.imageTintList =
                ContextCompat.getColorStateList(
                    editText.context,
                    com.stealthmonitoring.R.color.black
                )
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            indicator.setImageDrawable(
                ContextCompat.getDrawable(
                    editText.context,
                    com.stealthmonitoring.R.drawable.visibility_off
                )
            )
            indicator.imageTintList =
                ContextCompat.getColorStateList(
                    editText.context,
                    com.stealthmonitoring.R.color.black
                )
        }
        editText.setSelection(editText.text.length)
    }

    private fun loginUser() {

        if (binding.inputUsername.text.toString().isEmpty()) {
            val toast = Toast.makeText(this, "Please enter user name", Toast.LENGTH_LONG)
            toast.show()
        } else if (binding.inputPassword.text.toString().isEmpty()) {
            val toast = Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG)
            toast.show()
        } else {
            binding.progressBar.visibility = View.VISIBLE
            val jsonParams: MutableMap<String, String> = HashMap()
            jsonParams["userNameOrEmailAddress"] = binding.inputUsername.text.toString()
            jsonParams["password"] = binding.inputPassword.text.toString()

            ApiImplementation.authenticateUser(this@LoginActivity, jsonParams, object :
                Callback<LoginModel?> {
                override fun onResponse(
                    call: Call<LoginModel?>,
                    response: Response<LoginModel?>
                ) {
                    val loginModel = response.body()?.result
                    if (loginModel != null) {

                        val snackBar = findViewById<View>(android.R.id.content)
                        Snackbar.make(snackBar, "Login Successful!", Snackbar.LENGTH_LONG)
                            .setDuration(3000).show()
                        //Store access token
                        val sharedPreferences: SharedPreferences =
                            getSharedPreferences("sharedPrefFile", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("Token", loginModel.accessToken)
                        editor.apply()

                        Handler().postDelayed({
                            binding.progressBar.visibility = View.GONE
                            val intent = Intent(this@LoginActivity, HomePageActivity::class.java)
                            intent.putExtra("ID", "1005")
                            val bundleAnimation = ActivityOptions.makeCustomAnimation(
                                applicationContext,
                                com.stealthmonitoring.R.transition.right_in,
                                com.stealthmonitoring.R.transition.right_out
                            ).toBundle()
                            startActivity(intent, bundleAnimation)
                        }, 1000)
                    } else {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            Toast.makeText(
                                this@LoginActivity,
                                jObjError.getJSONObject("error").getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                            val snackBar = findViewById<View>(android.R.id.content)
                            Snackbar.make(snackBar, e.message!!, Snackbar.LENGTH_LONG).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<LoginModel?>, t: Throwable) {
                    Log.e(tag, "Error$t")
                }
            })
        }
    }
}