package de.teamnoco.prodeli.data.user.model

import com.fasterxml.jackson.annotation.JsonIgnore
import de.teamnoco.prodeli.util.model.SerializableModel
import jakarta.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import kotlin.jvm.Transient

@Entity
@Table(name = "users")
data class User(

    @Id val telegramId: Long,

    val firstName: String,

    val lastName: String = "",

    @get:JvmName("_getUsername")
    val username: String?,

    @JsonIgnore
    val currentToken: String,

    @Transient
    @JsonIgnore
    var isNew: Boolean = false

) : UserDetails, SerializableModel {

    @JsonIgnore
    override fun isEnabled() = super.isEnabled()

    @JsonIgnore
    override fun getAuthorities() = mutableSetOf(SimpleGrantedAuthority(AuthorityType.USER.name))

    @JsonIgnore
    override fun getPassword(): String = currentToken

    override fun getUsername(): String = username ?: telegramId.toString()

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean = true

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean = true

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    enum class AuthorityType {
        USER
    }

    override fun serialize(): Map<String, Any> = mutableMapOf(
        "telegramId" to telegramId,
        "firstName" to firstName,
        "lastName" to lastName,
        "isNew" to isNew
    ).apply {
        if (username != null) {
            put("username", username!!)
        }
    }

}
