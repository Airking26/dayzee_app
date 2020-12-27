package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/*
Copyright (c) 2020 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */

enum class STATUS{
    PUBLIC,
    PRIVATE,
    BANNED,
    DELETED
}

@Parcelize
data class UserInfoDTO (
    @SerializedName("id") var id : String? = null,
    @SerializedName("email") var email : String? = null,
    @SerializedName("userName") var userName : String? = null,
    @SerializedName("picture") var picture : String? = null,
    @SerializedName("givenName") var givenName : String? = null,
    @SerializedName("familyName") var familyName : String? = null,
    @SerializedName("birthday") var birthday : String? =  null,
    @SerializedName("location") var location : Location? = null,
    @SerializedName("gender") var gender : String? = null,
    @SerializedName("description") var description : String? = null,
    @SerializedName("status") var status : Int = 0,
    @SerializedName("dateFormat") var dateFormat : Int,
    @SerializedName("followers") var followers : Int = 0,
    @SerializedName("following") var following : Int = 0,
    @SerializedName("isInFollowing") var isInFollowing: Boolean = false,
    @SerializedName("isInFollowers") var isInFollowers: Boolean = false,
    @SerializedName("socialMedias") var socialMedias : SocialMedias,
    @SerializedName("createdAt") var createdAt : String? = null,
    @SerializedName("certified") var certified: Boolean? = false,
    @SerializedName("isAdmin") var isAdmin: Boolean? = false
) : Parcelable