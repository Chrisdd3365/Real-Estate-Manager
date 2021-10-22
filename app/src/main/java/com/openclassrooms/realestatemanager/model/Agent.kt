package com.openclassrooms.realestatemanager.model

import android.graphics.Bitmap
import java.io.Serializable

class Agent : Serializable {

    var id : Int? = null
    var firstName : String? = null
    var lastName : String? = null
    var email : String? = null
    var phoneNumber : String? = null
    var avatar : Bitmap? = null

    override fun toString(): String {
        return "Agent ($id) $firstName $lastName, mail : $email, phone : $phoneNumber"
    }
}