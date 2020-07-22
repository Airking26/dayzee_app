package com.timenoteco.timenote.model

data class Timenote(
    var pic_user: String?,
    var pic: MutableList<String>?,
    var username: String?,
    var place: String?,
    var nbrLikes: String?,
    var seeComments: String?,
    var desc: String?,
    var liked: Boolean?,
    var year: String?,
    var month: String?,
    var date: String?,
    var title: String?,
    var dateIn: String?,
    var price: Long?,
    var url: String?,
    var status: StatusTimenote)