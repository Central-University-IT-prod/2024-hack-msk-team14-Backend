package de.teamnoco.prodeli.data.debt.model

import de.teamnoco.prodeli.data.event.model.EventMember
import de.teamnoco.prodeli.data.item.model.EventItem
import de.teamnoco.prodeli.util.ZERO_UUID
import jakarta.persistence.*
import java.math.BigInteger
import java.util.*

@Entity
@Table(name = "debts")
data class Debt(
    @Id
    val id: UUID = ZERO_UUID,

    val sum: BigInteger,

    @ManyToOne(cascade = [CascadeType.ALL])
    val item: EventItem,

    @ManyToOne(cascade = [CascadeType.ALL])
    val member: EventMember,

    val paid: Boolean
)
