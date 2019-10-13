package io.legado.app.receiver

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import io.legado.app.ui.book.search.SearchActivity
import io.legado.app.ui.main.MainActivity
import org.jetbrains.anko.startActivity

class ReceivingSharedActivity : AppCompatActivity() {

    private val receivingType = "text/plain"

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initIntent()
    }

    private fun initIntent() {
        if (Intent.ACTION_SEND == intent.action && intent.type == receivingType) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                if (openUrl(it)) {
                    startActivity<SearchActivity>(Pair("key", it))
                }
                finish()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && Intent.ACTION_PROCESS_TEXT == intent.action
            && intent.type == receivingType
        ) {
            intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)?.let {
                if (openUrl(it)) {
                    startActivity<SearchActivity>(Pair("key", it))
                }
                finish()
            }
        }
    }

    private fun openUrl(text: String): Boolean {
        if (text.isBlank()) {
            return false
        }
        val urls = text.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val result = StringBuilder()
        for (url in urls) {
            if (url.matches("http.+".toRegex()))
                result.append("\n").append(url.trim { it <= ' ' })
        }
        return if (result.length > 1) {
            val intent = Intent()
            intent.setClass(this@ReceivingSharedActivity, MainActivity::class.java)
            this.startActivity(intent)
            false
        } else {
            true
        }
    }
}