package com.udbstudents.tseapp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.udbstudents.tseapp.R

class FN {
    companion object {
        fun alertProcesses(context: Context, message:String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.warning)
            builder.setMessage(message)
            builder.setPositiveButton(R.string.accept_btn) { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        fun showDetailsMessage(context: Context, sender:String? = null, time:String? = null, message: String? = null){
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.details_message))
            builder.setMessage("${sender} \n ${message} \n ${time}")
            builder.setPositiveButton(R.string.accept_btn) { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

}