package ru.skillbranch.skillarticles.ui.custom.markdown

import android.text.Spannable
import android.text.Spanned

interface IMarkdownView
{
    var fontSize: Float
    val spannableContent: Spannable
}