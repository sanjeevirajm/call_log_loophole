package com.example.call_log_loophole

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.ContactsContract
import com.example.call_log_loophole.R
import com.example.call_log_loophole.db.UserDatabase
import com.example.call_log_loophole.db.UserRepository
import com.example.call_log_loophole.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CallLogContentProvider : ContentProvider() {

    private var userRepository: UserRepository? = null

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    private lateinit var authorityUri: Uri

    override fun onCreate(): Boolean {
        context?.let {
            val userDao = UserDatabase.getDatabase(it).userDao()
            userRepository = UserRepository(userDao)
            val authority = it.getString(R.string.call_log_loophole_authority)
            authorityUri = Uri.parse("content://$authority")

            uriMatcher.apply {
                addURI(authority, "phone_lookup/*", PHONE_LOOKUP)
            }
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        when (uriMatcher.match(uri)) {
            DIRECTORIES -> {
                val label = context?.getString(R.string.app_name) ?: return null
                val cursor = MatrixCursor(projection)
                projection?.map { column ->
                    when (column) {
                        ContactsContract.Directory.ACCOUNT_NAME,
                        ContactsContract.Directory.ACCOUNT_TYPE,
                        ContactsContract.Directory.DISPLAY_NAME -> label
                        ContactsContract.Directory.TYPE_RESOURCE_ID -> R.string.app_name
                        ContactsContract.Directory.EXPORT_SUPPORT -> ContactsContract.Directory.EXPORT_SUPPORT_SAME_ACCOUNT_ONLY
                        ContactsContract.Directory.SHORTCUT_SUPPORT -> ContactsContract.Directory.SHORTCUT_SUPPORT_NONE
                        else -> null
                    }
                }?.let { cursor.addRow(it) }
                return cursor
            }
            PHONE_LOOKUP -> {
                val phoneNumber = uri.pathSegments[1]
                runBlocking(Dispatchers.IO) {
                    userRepository!!.insert(User(phoneNumber))
                }
            }
        }
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException()
    }

    companion object {
        private const val DIRECTORIES = 1
        private const val PHONE_LOOKUP = 2
        private const val PRIMARY_PHOTO = 3

        private const val PRIMARY_PHOTO_URI = "photo/primary_photo"
    }
}
