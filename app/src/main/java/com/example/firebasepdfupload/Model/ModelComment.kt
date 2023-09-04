package com.example.firebasepdfupload.Model

class ModelComment {
    var id=""
    var pdfId=""
    var timestamp=""
    var comment=""
    var uid=""

    //    empty constructor, required by firebase
    constructor()


    //    param constructor
    constructor(id: String, pdfId: String, timestamp: String, comment: String, uid: String) {
        this.id = id
        this.pdfId = pdfId
        this.timestamp = timestamp
        this.comment = comment
        this.uid = uid
    }
}