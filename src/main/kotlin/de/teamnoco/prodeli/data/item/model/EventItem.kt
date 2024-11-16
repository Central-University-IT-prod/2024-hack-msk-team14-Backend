package de.teamnoco.prodeli.data.item.model

import jakarta.persistence.*
import java.math.BigInteger
import java.util.*

@Entity
@Table(name = "operations")
data class EventItem(

    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id")
    val groupId: UUID,

    val name: String,

    val price: BigInteger,

    val quantity: Int,

)
