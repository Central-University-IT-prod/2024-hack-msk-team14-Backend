package de.teamnoco.prodeli.data.event.model

import de.teamnoco.prodeli.data.debt.model.Debt
import de.teamnoco.prodeli.data.item.model.ItemGroup
import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.util.ZERO_UUID
import jakarta.persistence.*
import java.util.*

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@Entity
@IdClass(EventMember.MemberId::class)
@Table(name = "event_members")
data class EventMember(
    @Id
    @Column(name = "user_id")
    val userId: Long,

    @Id
    @Column(name = "event_id")
    val eventId: UUID,

    @OneToMany(mappedBy = "member")
    val debts: MutableList<Debt>,

    @OneToMany(mappedBy = "lender")
    val lendItemGroups: MutableList<ItemGroup>,

    @MapsId
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User
) {

    data class MemberId(
        val userId: Long = 0,
        val eventId: UUID = ZERO_UUID
    )
}

fun User.toEventMember(event: Event) = EventMember(
    userId = telegramId,
    eventId = event.id,
    debts = mutableListOf(),
    lendItemGroups = mutableListOf(),
    user = this
)