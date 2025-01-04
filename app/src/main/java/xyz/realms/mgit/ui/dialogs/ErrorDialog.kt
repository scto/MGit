package xyz.realms.mgit.ui.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.annotation.StringRes
import xyz.realms.mgit.ui.fragments.SheimiDialogFragment
import xyz.realms.mgit.R
import xyz.realms.mgit.databinding.DialogErrorBinding
import org.conscrypt.BuildConfig
import timber.log.Timber

class ErrorDialog : SheimiDialogFragment() {
    private var mThrowable: Throwable? = null

    @StringRes
    private var mErrorRes: Int = 0

    @StringRes
    var errorTitleRes: Int = 0
        get() = if (field != 0) field else R.string.dialog_error_title

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)/*
        * DateBinding示例
        *
        * 绑定类的名称是通过将 XML 文件的名称转换为驼峰式大小写，并在结尾处添加 Binding 一词来生成的。
        * 例如，假设某个布局文件的名称为 dialog_error.xml，所生成的绑定类的名称就为 DialogErrorBinding
        */
        val inflater = rawActivity.layoutInflater
        val builder = AlertDialog.Builder(rawActivity)
        val layout: DialogErrorBinding = DialogErrorBinding.inflate(inflater)
        val details = when (mThrowable) {
            is Exception -> {
                (mThrowable as Exception).message
            }

            else -> ""
        }
        layout.errorMessage.text = getString(mErrorRes) + "\n" + details

        builder.setView(layout.root)

        // set button listener
        builder.setTitle(errorTitleRes)
        builder.setPositiveButton(getString(R.string.label_ok), DummyDialogListener())
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE) as Button
        positiveButton.setOnClickListener {
            if (BuildConfig.DEBUG) {
                // when debugging just log the exception
                if (mThrowable != null) {
                    Timber.e(mThrowable)
                } else {
                    Timber.e(if (mErrorRes != 0) getString(mErrorRes) else "")
                }
            }
            dismiss()
        }
    }

    fun setThrowable(throwable: Throwable?) {
        mThrowable = throwable
    }

    fun setErrorRes(@StringRes errorRes: Int) {
        mErrorRes = errorRes
    }
}
