package me.sheimi.sgit

import android.app.Application
import android.content.Context
//import com.manichord.mgit.transport.MGitHttpConnectionFactory
import me.sheimi.android.utils.SecurePrefsException
import me.sheimi.android.utils.SecurePrefsHelper
import me.sheimi.sgit.preference.PreferenceHelper
//import org.acra.config.dialog
//import org.acra.config.mailSender
//import org.acra.data.StringFormat
//import org.acra.ktx.initAcra
//import org.bouncycastle.jce.provider.BouncyCastleProvider
//import org.conscrypt.Conscrypt
import org.eclipse.jgit.transport.CredentialsProvider
//import timber.log.Timber
import java.security.Security

/**
 * Custom Application Singleton
 */
open class MGitApplication : Application() {
    var securePrefsHelper: SecurePrefsHelper? = null
    var prefenceHelper: PreferenceHelper? = null


    companion object {
        private lateinit var mContext: Context
        private lateinit var mCredentialsProvider: CredentialsProvider
        val context: Context
            get() = mContext

        @JvmStatic fun getContext(): MGitApplication {
            return mContext as MGitApplication
        }

        @JvmStatic fun getJschCredentialsProvider(): CredentialsProvider {
            return mCredentialsProvider
        }

        init {
          //  MGitHttpConnectionFactory.install()
           // Security.addProvider(BouncyCastleProvider())
          //  Security.addProvider(Conscrypt.newProvider())
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
      //  setAppVersionPref()
        prefenceHelper = PreferenceHelper(this)
        try {
            securePrefsHelper = SecurePrefsHelper(this)
            mCredentialsProvider = AndroidJschCredentialsProvider(securePrefsHelper)
        } catch (e: SecurePrefsException) {
         //   Timber.e(e)
        }
    }

    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)

            }
    }
