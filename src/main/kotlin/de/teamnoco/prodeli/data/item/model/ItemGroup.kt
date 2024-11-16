package de.teamnoco.prodeli.data.item.model

import com.fasterxml.jackson.annotation.JsonIgnore
import de.teamnoco.prodeli.data.event.model.EventMember
import de.teamnoco.prodeli.util.ZERO_UUID
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "operation_groups")
data class ItemGroup(

    @Id
    @GeneratedValue
    val id: UUID = ZERO_UUID,

    val name: String,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "group_id")
    val items: MutableList<EventItem>,

    @JsonIgnore
    @ManyToOne(cascade = [CascadeType.ALL])
    val lender: EventMember

)
