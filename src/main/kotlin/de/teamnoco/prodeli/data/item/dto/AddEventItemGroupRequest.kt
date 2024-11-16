package de.teamnoco.prodeli.data.item.dto

import java.math.BigInteger

data class AddEventItemGroupRequest(
    val name: String,
    val items: List<RequestEventItem>
) {
    data class RequestEventItem(
        val name: String,
        val quantity: Int,
        val price: BigInteger
    )
}
