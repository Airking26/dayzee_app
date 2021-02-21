package com.dayzeeco.dayzee.view.profileFlow.menuDirectory

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CursorAdapter
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.dayzeeco.dayzee.R

@SuppressLint("InlinedApi")
private val FROM_COLUMNS: Array<String> = arrayOf(
    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    } else {
        ContactsContract.Contacts.DISPLAY_NAME
    }
)

@SuppressLint("InlinedApi")
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    else
        ContactsContract.Contacts.DISPLAY_NAME
)

private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)
private const val CONTACT_ID_INDEX: Int = 0
private const val CONTACT_KEY_INDEX: Int = 1

@SuppressLint("InlinedApi")
private val SELECTION: String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
    else
        "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"


class Contacts: Fragment(), LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private val searchString: String = ""
    private val selectionArgs = arrayOf(searchString)
    lateinit var contactsList: ListView
    var contactId: Long = 0
    var contactKey: String? = null
    var contactUri: Uri? = null
    private var cursorAdapter: SimpleCursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_contacts, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.also {
            contactsList = it.findViewById(R.id.contact_list_view)
            // Gets a CursorAdapter
            cursorAdapter = SimpleCursorAdapter(
                it,
                R.layout.item_contact,
                null,
                FROM_COLUMNS,
                TO_IDS,
                0
            )
            // Sets the adapter for the ListView
            contactsList.adapter = cursorAdapter
            contactsList.onItemClickListener = this
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        selectionArgs[0] = "%$searchString%"
        return activity?.let {
            return CursorLoader(
                it,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                null
            )
        } ?: throw IllegalStateException()    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        cursorAdapter?.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        cursorAdapter?.swapCursor(null)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val cursor = (parent?.adapter as? CursorAdapter)?.cursor?.apply {
            moveToPosition(position)
            contactId = getLong(CONTACT_ID_INDEX)
            contactKey = getString(CONTACT_KEY_INDEX)
            contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey)
        }

        ShareCompat.IntentBuilder
            .from(requireActivity())
            .setText("Check out a new app: https://play.google.com/store/apps/details?id=<your-package>")
            .setType("text/plain")
            .setChooserTitle("Share with your friends")
            .startChooser();
    }
}