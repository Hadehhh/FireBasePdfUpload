package com.example.firebasepdfupload.Model

class ModelPdf {
    //    variables
    var uid:String=""
    var id:String=""
    var title:String=""
    var description:String=""
    var categoryId: String=""
    var url:String=""
    var timestamp:Long=0
    var isBookmark=false

    //    empty constructor (required by firebase)
    constructor()
    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        categoryId: String,
        url: String,
        timestamp: Long,
        isBookmark:Boolean
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.categoryId = categoryId
        this.url = url
        this.timestamp = timestamp
        this.isBookmark=isBookmark
    }
//    parametrized constructor



}