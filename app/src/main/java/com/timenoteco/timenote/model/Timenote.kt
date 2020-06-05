package com.timenoteco.timenote.model

data class Timenote(val pic_user: String,
                    val pic: String,
                    val username: String,
                    val place: String,
                    val nbrLikes: String,
                    val seeComments: String,
                    val desc: String,
                    val liked: Boolean,
                    val year: String,
                    val month: String,
                    val date: String,
                    val title: String)