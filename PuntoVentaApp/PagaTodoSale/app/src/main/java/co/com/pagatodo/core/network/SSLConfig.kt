package co.com.pagatodo.core.network

import android.content.Context
import android.util.Log
import co.com.pagatodo.core.BuildConfig
import co.com.pagatodo.core.R
import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*


class SSLConfig {

    fun addSSLToHttpClientBuilder(clientBuilder: OkHttpClient.Builder, context: Context): OkHttpClient.Builder? {

        val certificateType = "X.509"
        val alias = "ca"
        val protocol = "TLS"

        try {
            val certificateFactory = CertificateFactory.getInstance(certificateType)
            val caInput = context.resources.openRawResource(R.raw.datacenter)

            val certificate = caInput.use {
                certificateFactory?.generateCertificate(it)
            }

            // Create a KeyStore containing our trusted CAs
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType).apply {
                load(null, null)
                setCertificateEntry(alias, certificate)
            }


            // Create a TrustManager that trusts the CAs inputStream our KeyStore
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
                init(keyStore)
            }

            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                val originalTrusteManager = (trustManagerFactory.trustManagers[0] as X509TrustManager)
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return originalTrusteManager.acceptedIssuers
                }
            })

            // Create an SSLContext that uses our TrustManager
            val sslContext = SSLContext.getInstance(protocol).apply {
                init(null, trustManagerFactory.trustManagers, null)
            }

            clientBuilder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            clientBuilder.hostnameVerifier(object: HostnameVerifier {
                override fun verify(hostname: String?, session: SSLSession?): Boolean {
                    return (hostname == BuildConfig.IP_ADDRESS)
                }
            })

        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        return clientBuilder
    }
}