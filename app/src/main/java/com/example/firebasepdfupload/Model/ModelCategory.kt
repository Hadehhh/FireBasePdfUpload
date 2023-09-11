package com.example.firebasepdfupload.Model

class ModelCategory {
    //    variables, must watch as  in firebase
//    from categoryAdd Activity
    var categoryId: String = ""
    var category: String = ""
    var timestamp: Long = 0
    var uid: String = ""

    //    empty constructor,required by firebase
    constructor()

    //    parametrized constructor
    constructor(categoryId: String, category: String, timestamp: Long, uid: String) {
        this.categoryId = categoryId
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }
}