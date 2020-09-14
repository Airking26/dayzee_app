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

@Parcelize
data class UserInfoDTO (
    @SerializedName("id") var id : String,
    @SerializedName("email") var email : String,
    @SerializedName("userName") var userName : String,
    @SerializedName("picture") var picture : String,
    @SerializedName("givenName") var givenName : String,
    @SerializedName("familyName") var familyName : String,
    @SerializedName("birthday") var birthday : String,
    @SerializedName("location") var location : Location,
    @SerializedName("gender") var gender : String,
    @SerializedName("description") var description : String,
    @SerializedName("status") var status : String,
    @SerializedName("dateFormat") var dateFormat : String,
    @SerializedName("followers") var followers : Int,
    @SerializedName("following") var following : Int,
    @SerializedName("isInFollowing") var isInFollowing: Boolean,
    @SerializedName("isInFollowers") var isInFollowers: Boolean,
    @SerializedName("socialMedias") var socialMedias : SocialMedias,
    @SerializedName("createdAt") var createdAt : String
) : Parcelable