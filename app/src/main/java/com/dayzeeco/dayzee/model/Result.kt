package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class Result (

    @SerializedName("address_components") val address_components : List<AddressComponents>,
    @SerializedName("adr_address") val adr_address : String,
    @SerializedName("formatted_address") val formatted_address : String,
    @SerializedName("formatted_phone_number") val formatted_phone_number : String,
    @SerializedName("geometry") val geometry : Geometry,
    @SerializedName("icon") val icon : String,
    @SerializedName("id") val id : String,
    @SerializedName("international_phone_number") val international_phone_number : String,
    @SerializedName("name") val name : String,
    @SerializedName("place_id") val place_id : String,
    @SerializedName("rating") val rating : Double,
    @SerializedName("reference") val reference : String,
    @SerializedName("reviews") val reviews : List<Reviews>,
    @SerializedName("types") val types : List<String>,
    @SerializedName("url") val url : String,
    @SerializedName("utc_offset") val utc_offset : Int,
    @SerializedName("vicinity") val vicinity : String,
    @SerializedName("website") val website : String
)