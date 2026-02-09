package com.travelotef.app.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.travelotef.app.R

/**
 * Extension functions for common UI operations
 */

// View visibility extensions
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.showIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

// ImageView extensions with Glide
fun ImageView.loadImage(url: String?) {
    if (url.isNullOrBlank()) {
        setImageResource(R.drawable.ic_home)
        return
    }
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .centerCrop()
        .into(this)
}

fun ImageView.loadImageRounded(url: String?, cornerRadius: Int = 16) {
    if (url.isNullOrBlank()) {
        setImageResource(R.drawable.ic_home)
        return
    }
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .centerCrop()
        .into(this)
}

// Price formatting
fun String?.formatPrice(currencySymbol: String = "₪"): String {
    if (this.isNullOrBlank()) return ""
    return try {
        val price = this.toDouble()
        if (price == price.toLong().toDouble()) {
            "$currencySymbol${price.toLong()}"
        } else {
            "$currencySymbol${"%.2f".format(price)}"
        }
    } catch (e: NumberFormatException) {
        "$currencySymbol$this"
    }
}

// Rating formatting
fun Float.formatRating(): String {
    return if (this > 0) "⭐ ${"%.1f".format(this)}" else ""
}

// String extensions
fun String?.orDefault(default: String = ""): String {
    return if (this.isNullOrBlank()) default else this
}
