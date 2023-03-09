package dev.limebeck.libs.model

data class Ordering<R: Enum<R>>(
        val field: R,
        val direction: Direction
){
    enum class Direction{
        ASC,
        DESC
    }
}