package com.timenoteco.timenote.model

import android.os.Parcel
import android.os.Parcelable

data class Preference(var category: Category?, var subCategories: List<SubCategory>, var index: Int): Parcelable {

    constructor(parcel: Parcel) : this(
       // parcel.readParcelable(Category::class.java.classLoader)!!,
        null,
        parcel.createTypedArrayList(SubCategory)!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //parcel.writeParcelable(category, flags)
        parcel.writeTypedList(subCategories)
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Preference> {
        override fun createFromParcel(parcel: Parcel): Preference {
            return Preference(parcel)
        }

        override fun newArray(size: Int): Array<Preference?> {
            return arrayOfNulls(size)
        }
    }
}