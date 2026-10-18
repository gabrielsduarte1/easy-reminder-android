package br.com.easyreminder.ui.reminder

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ReminderOptionsDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "ReminderOptionsDialog"
        const val REQUEST_KEY = "reminder_options_request"

        const val RESULT_ACTION = "action"
        const val RESULT_REMINDER_ID = "reminder_id"

        const val ACTION_EDIT = "edit"
        const val ACTION_DELETE = "delete"

        private const val ARG_TITLE = "title"
        private const val ARG_REMINDER_ID = "reminder_id"

        fun newInstance(title: String, reminderId: Int) =
            ReminderOptionsDialogFragment().apply {
                arguments = bundleOf(
                    ARG_TITLE to title,
                    ARG_REMINDER_ID to reminderId
                )
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = requireArguments().getString(ARG_TITLE).orEmpty()
        val reminderId = requireArguments().getInt(ARG_REMINDER_ID)
        val options = arrayOf("Editar", "Excluir")

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setItems(options) { _, which ->
                val action = if (which == 0) ACTION_EDIT else ACTION_DELETE
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        RESULT_ACTION to action,
                        RESULT_REMINDER_ID to reminderId
                    )
                )
            }
            .create()
    }
}