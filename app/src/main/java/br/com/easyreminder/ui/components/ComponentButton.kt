package br.com.easyreminder.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import br.com.easyreminder.R
import br.com.easyreminder.databinding.ComponentButtonBinding

class ComponentButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    var binding = ComponentButtonBinding.inflate(LayoutInflater.from(context), this, true)
    init {
        context.obtainStyledAttributes(attrs, R.styleable.ComponentButton).apply {
            binding.buttonSave.text = getString(R.styleable.ComponentButton_android_text)
            binding.buttonSave.isAllCaps = getBoolean(R.styleable.ComponentButton_android_textAllCaps, false)
        }
    }
}
