package com.example.vmpshare

import android.content.Context
import android.content.Intent

/**
 * Every outbound share in one place.
 *
 * The proposal is entirely here: the payload names the sender and carries a
 * Play install referrer, so the recipient knows who invited them and the
 * install can be attributed back to the share that caused it.
 */
object Share {

    private const val PLAY =
        "https://play.google.com/store/apps/details?id=com.vmpmessenger.com.android"

    /** What VMP sends today — kept for the before/after in the walkthrough. */
    fun currentInvite(): String =
        "I'm on VMP Messenger — join me and let's chat. Download: $PLAY"

    /** Proposed invite: sender identity, a reason to accept, and attribution. */
    fun invite(name: String, handle: String, number: String): String {
        val tag = handle.removePrefix("@")
        return """
            $name ($handle) invited you to VMP Messenger.

            My VMP number is $number — add me and we can chat and call free.

            $PLAY&referrer=inv_$tag
        """.trimIndent()
    }

    /** Proposed content share: the post travels, not just the app name. */
    fun post(author: String, handle: String, body: String, postId: String, sharer: String): String {
        val tag = sharer.removePrefix("@")
        return """
            "$body"
            — $author ($handle) on VMP

            $PLAY&referrer=post_${postId}_$tag
        """.trimIndent()
    }

    fun send(context: Context, text: String, chooserTitle: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}