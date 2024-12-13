package com.c242ps518.tanami.ui.main.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.lifecycle.lifecycleScope
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.pref.LangPreference
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LanguageBottomSheet: BottomSheetDialogFragment() {
    private var onLanguageSelected: ((String) -> Unit)? = null

    fun setOnLanguageSelectedListener(listener: (String) -> Unit) {
        onLanguageSelected = listener
    }

    private lateinit var langPreference: LangPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_language, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupLanguage)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        langPreference = LangPreference(requireContext())
        val selected = langPreference.getLanguage()
        when (selected) {
            "en" -> radioGroup.check(R.id.rbEnglish)
            "id" -> radioGroup.check(R.id.rbIndonesian)
            else -> radioGroup.check(R.id.rbEnglish)
        }

        btnSave.setOnClickListener {
            val selectedLanguage = when (radioGroup.checkedRadioButtonId) {
                R.id.rbEnglish -> "en"
                R.id.rbIndonesian -> "id"
                else -> "en"
            }
            onLanguageSelected?.invoke(selectedLanguage)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(true)
        }
    }

    companion object {
        const val TAG = "LanguageBottomSheet"
    }
}