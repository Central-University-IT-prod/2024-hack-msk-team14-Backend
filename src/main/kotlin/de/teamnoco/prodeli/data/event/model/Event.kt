package de.teamnoco.prodeli.data.event.model

import de.teamnoco.prodeli.data.item.model.ItemGroup
import de.teamnoco.prodeli.data.user.model.User
import de.teamnoco.prodeli.util.ZERO_UUID
import jakarta.persistence.*
import java.math.BigInteger
import java.time.Instant
import java.util.*

/**
 * @author <a href="https://github.com/Neruxov">Neruxov</a>
 */
@Entity
@Table(name = "events")
data class Event(
    @Id
    @GeneratedValue
    val id: UUID = ZERO_UUID,

    val name: String,

    @ManyToOne
    @JoinColumn(name = "creator_id")
    val creator: User,

    val lockedDate: Instant?,

    @Transient
    val locked: Boolean = lockedDate != null,

    @OneToMany(fetch = FetchType.EAGER)
    val itemGroups: MutableList<ItemGroup>,

    @Enumerated(value = EnumType.STRING)
    val lendType: LendType,

) {
    @OneToMany(mappedBy = "eventId")
    var members: MutableList<EventMember> = mutableListOf()

    fun getTotalSum(): BigInteger {
        return itemGroups.flatMap { it.items }.sumOf {
            it.price * it.quantity.toBigInteger()
        }
    }

    enum class LendType {
        EQUALLY,
        OWNER_SELECT,
        MEMBERS_SELECT
    }
}