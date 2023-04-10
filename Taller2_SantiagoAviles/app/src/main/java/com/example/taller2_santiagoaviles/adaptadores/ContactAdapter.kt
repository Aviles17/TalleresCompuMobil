package com.example.taller2_santiagoaviles.adaptadores

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.example.taller2_santiagoaviles.R

class ContactAdapter(context: Context?, cursor: Cursor?, flags: Int) : CursorAdapter(context, cursor, flags) {
    override fun newView(context: Context?, cursor: Cursor?, viewGroup: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.contactsrow, viewGroup,false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val id = view!!.findViewById<TextView>(R.id.Contactid)
        val name = view.findViewById<TextView>(R.id.ContactName)
        val idNum = cursor!!.getInt(0)
        val nameStr = cursor.getString(1)
        id.text = idNum.toString()
        name.text = nameStr
    }

}