package com.xinglan.mgit.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.annotation.StringRes
import com.xinglan.android.views.SheimiDialogFragment
import com.xinglan.mgit.BuildConfig
import com.xinglan.mgit.R
import com.xinglan.mgit.databinding.DialogErrorBinding
import timber.log.Timber

class ErrorDialog : SheimiDialogFragment() {
    private var mThrowable: Throwable? = null
    private lateinit var layout: DialogErrorBinding

    @StringRes
    private var mErrorRes: Int = 0

    @StringRes
    var errorTitleRes: Int = 0
        get() = if (field != 0) field else R.string.dialog_error_title

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(rawActivity)
        val inflater = rawActivity.layoutInflater
        /*
        * 绑定类的名称是通过将 XML 文件的名称转换为驼峰式大小写，并在结尾处添加 Binding 一词来生成的。
        * 例如，假设某个布局文件的名称为 dialog_error.xml，所生成的绑定类的名称就为 DialogErrorBinding
        */
        layout = DialogErrorBinding.inflate(inflater)
        val details = when (mThrowable) {
            is Exception -> {
                (mThrowable as Exception).message
            }

            else -> ""
        }
        layout.errorMessage.setText(getString(mErrorRes) + "\n" + details)

        builder.setView(layout.root)

        // set button listener
        builder.setTitle(errorTitleRes)
        builder.setPositiveButton(
            getString(R.string.label_ok),
            DummyDialogListener()
        )
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
                    Timber.e(mThrowable);
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
