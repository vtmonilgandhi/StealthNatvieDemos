package com.stealthmonitoring.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ApiClient {
    companion object {
        private var baseUrl = "https://portal-gw-dev.stealthmonitoring.net"
        private var retrofit: Retrofit? = null
        private lateinit var appcontext: Context

        //Create retrofit object
        fun getClient(context: Context): Retrofit? {
            appcontext = context

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getUnsafeOkHttpClient()!!.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }

        private fun getUnsafeOkHttpClient(): OkHttpClient.Builder? {
            return try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate?> {
                            return arrayOf()
                        }
                    }
                )
                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                // val builder = OkHttpClient.Builder().addInterceptor(interceptor)

                val builder = OkHttpClient.Builder()

                //Get SharedPreference data (Token)
                val prefs = appcontext.getSharedPreferences("sharedPrefFile", Context.MODE_PRIVATE)
                val status = prefs.getString("Token", "")

                //Add header data
                builder.addInterceptor(Interceptor { chain ->
                    val request: Request =
                        chain.request().newBuilder()
                            .addHeader(
                                "Authorization",
                                "Bearer $status"
                            )
                            .build()
                    chain.proceed(request)
                }).addInterceptor(interceptor)
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }
                builder
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}