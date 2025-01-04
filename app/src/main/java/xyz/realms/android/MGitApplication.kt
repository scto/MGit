package xyz.realms.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import xyz.realms.android.preference.PreferenceHelper
import xyz.realms.android.utils.SecurePrefsHelper
import xyz.realms.mgit.R
import xyz.realms.mgit.common.exceptions.SecurePrefsException
import xyz.realms.mgit.transport.AndroidJschCredentialsProvider
import xyz.realms.mgit.transport.MGitHttpConnectionFactory
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.conscrypt.BuildConfig
import org.conscrypt.Conscrypt
import org.eclipse.jgit.transport.CredentialsProvider
import timber.log.Timber
import java.security.Security

/**
 * Custom Application Singleton
 */
open class MGitApplication : Application() {
    var securePrefsHelper: SecurePrefsHelper? = null
    var prefenceHelper: PreferenceHelper? = null


    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var mContext: Context
        private lateinit var mCredentialsProvider: CredentialsProvider
        val context: Context
            get() = mContext

        @JvmStatic
        fun getContext(): MGitApplication {
            return mContext as MGitApplication
        }

        @JvmStatic
        fun getJschCredentialsProvider(): CredentialsProvider {
            return mCredentialsProvider
        }

        init {
            MGitHttpConnectionFactory.install()
            Security.addProvider(BouncyCastleProvider())
            Security.addProvider(Conscrypt.newProvider())
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        setAppVersionPref()
        prefenceHelper = PreferenceHelper(this)
        try {
            securePrefsHelper =
                SecurePrefsHelper(this)
            mCredentialsProvider =
                AndroidJschCredentialsProvider(
                    securePrefsHelper
                )
        } catch (e: SecurePrefsException) {
            Timber.e(e)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            // each plugin you chose above can be configured in a block like this:
            dialog {
                text = getString(R.string.dialog_error_send_report)
                //opening this block automatically enables the plugin.
            }
            mailSender {
                withMailTo(getString(R.string.crash_report_email))
            }
        }
    }

    private fun setAppVersionPref() {
        val sharedPreference = getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val version = BuildConfig.VERSION_NAME
        sharedPreference
            .edit()
            .putString(getString(R.string.preference_key_app_version), version)
            .apply()
    }
}
