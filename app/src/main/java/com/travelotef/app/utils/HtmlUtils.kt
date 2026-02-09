package com.travelotef.app.utils

import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 * Utility functions for handling HTML content from WooCommerce
 * WooCommerce product descriptions often contain HTML formatting
 */
object HtmlUtils {

    /**
     * Strip HTML tags from a string and return plain text
     */
    fun stripHtml(html: String?): String {
        if (html.isNullOrBlank()) return ""
        return fromHtml(html).toString().trim()
    }

    /**
     * Convert HTML string to Spanned (preserving formatting for TextViews)
     */
    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }
    }

    /**
     * Extract first image URL from HTML content
     */
    fun extractFirstImageUrl(html: String?): String? {
        if (html.isNullOrBlank()) return null
        val imgRegex = """<img[^>]+src\s*=\s*['""]([^'"">]+)['""][^>]*>""".toRegex()
        return imgRegex.find(html)?.groupValues?.getOrNull(1)
    }

    /**
     * Clean up WooCommerce short description
     * Removes common WooCommerce HTML wrappers
     */
    fun cleanDescription(html: String?): String {
        if (html.isNullOrBlank()) return ""
        return stripHtml(html)
            .replace("\n+".toRegex(), "\n")
            .replace("\s+".toRegex(), " ")
            .trim()
    }
}
