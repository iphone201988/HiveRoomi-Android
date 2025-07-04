package com.tech.hive.ui.for_room_mate.home.sample

data class Spot(
    val id: Long = counter++,
    val name: String,
    val profession: String,
    val money: String,
    val parties: String,
    val url: Int
) {
    companion object {
        private var counter = 0L
    }
}
