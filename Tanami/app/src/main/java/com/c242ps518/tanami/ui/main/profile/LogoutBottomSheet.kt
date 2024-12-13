package com.c242ps518.tanami.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.c242ps518.tanami.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LogoutBottomSheet : BottomSheetDialogFragment() {

    private var onLogoutConfirmed: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_logout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnConfirmLogout = view.findViewById<Button>(R.id.btnConfirmLogout)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnConfirmLogout.setOnClickListener {
            onLogoutConfirmed?.invoke()
            dismiss()
        }
    }

    companion object {
        const val TAG = "LogoutBottomSheet"

        fun newInstance(onLogoutConfirmed: () -> Unit): LogoutBottomSheet {
            val fragment = LogoutBottomSheet()
            fragment.onLogoutConfirmed = onLogoutConfirmed
            return fragment
        }
    }
}