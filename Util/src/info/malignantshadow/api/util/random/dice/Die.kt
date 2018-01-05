package info.malignantshadow.api.util.random.dice

import java.util.Random

class Die {

    var faces: Int = -1
    private val _r = Random()

    var shownFace: Int = -1
        set(value) {
            require(value in 0..faces) { "Shown face cannot be less than 0 or greater than available faces" }
            field = value
        }

    constructor(faces: Int) {
        require (faces >= 1) {"Cannot have a Die with less than one face" }
        this.faces = faces
    }

    constructor() : this(6)

    fun roll(): Int {
        shownFace = _r.nextInt(faces) + 1
        return shownFace
    }

}