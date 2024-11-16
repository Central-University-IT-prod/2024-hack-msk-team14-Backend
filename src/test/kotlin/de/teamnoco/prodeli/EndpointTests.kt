package de.teamnoco.prodeli

import de.teamnoco.prodeli.data.event.dto.EventCreateRequest
import de.teamnoco.prodeli.data.event.dto.EventUpdateRequest
import de.teamnoco.prodeli.data.event.repository.EventMemberRepository
import de.teamnoco.prodeli.data.event.repository.EventRepository
import de.teamnoco.prodeli.data.item.repository.ItemGroupRepository
import de.teamnoco.prodeli.data.user.repository.UserRepository
import de.teamnoco.prodeli.web.request.auth.WebAppAuthRequest
import de.teamnoco.prodeli.web.response.StatusResponse
import de.teamnoco.prodeli.web.response.asResponseEntity
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EndpointTests {
    @Autowired
    private lateinit var eventMemberRepository: EventMemberRepository

    @Autowired
    private lateinit var itemGroupRepository: ItemGroupRepository

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    lateinit var client: TestRestTemplate

    @BeforeEach
    fun beforeEach() {
        itemGroupRepository.deleteAll()
        eventMemberRepository.deleteAll()
        eventRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @Order(1)
    fun `test ping`() {
        val entity = client.getForEntity<StatusResponse>("/api/ping")
        val expected = StatusResponse.Ok("Pong!").asResponseEntity()
        assertEquals(expected.statusCode, entity.statusCode)
        assertEquals(expected.body!!.statusCode, entity.body!!.statusCode)
        assertEquals(expected.body!!.message, entity.body!!.message)
    }

    @Test
    @Order(2)
    fun `test check`() {
        val entity = client.postForEntity<Map<String, Any>>("/api/check", mapOf("qrraw" to "t=20200924T1837&s=349.93&fn=9282440300682838&i=46534&fp=1273019065&n=1"))
        assertEquals(HttpStatusCode.valueOf(200), entity.statusCode)
    }

    @Test
    @Order(3)
    fun `test auth sign in`() {
        val entity = client.postForEntity<Map<String, Any>>("/api/auth/sign-in", WebAppAuthRequest("user=%7B%22id%22%3A5000472542%2C%22first_name%22%3A%22Akarmain%22%2C%22last_name%22%3A%22DevApp%22%2C%22username%22%3A%22akarmain%22%2C%22language_code%22%3A%22ru%22%2C%22allows_write_to_pm%22%3Atrue%7D&chat_instance=7253295287219311170&chat_type=private&auth_date=1731230130&hash=97d516259fb9a1b13ad5ce570e35a6cf41ff9496a26100304d598089153a509d", 5000472542, "Akarmain", "DevApp", "akarmain"))
        assertEquals(HttpStatusCode.valueOf(200), entity.statusCode)
        val body = entity.body!!
        assertNotNull(body["token"])
    }

    @Test
    @Order(4)
    fun `test user me`() {
        val firstEntity = client.postForEntity<Map<String, Any>>("/api/auth/sign-in", WebAppAuthRequest("user=%7B%22id%22%3A5000472542%2C%22first_name%22%3A%22Akarmain%22%2C%22last_name%22%3A%22DevApp%22%2C%22username%22%3A%22akarmain%22%2C%22language_code%22%3A%22ru%22%2C%22allows_write_to_pm%22%3Atrue%7D&chat_instance=7253295287219311170&chat_type=private&auth_date=1731230130&hash=97d516259fb9a1b13ad5ce570e35a6cf41ff9496a26100304d598089153a509d", 5000472542, "Akarmain", "DevApp", "akarmain"))
        val token = firstEntity.body!!["token"]!!
        val userEntity = client.exchange<Map<String, Any>>("/api/user/me", HttpMethod.GET, HttpEntity<Map<String, Any>>(HttpHeaders().apply {
            add("Authorization", "Bearer $token")
        }))

        assertEquals(HttpStatusCode.valueOf(200), userEntity.statusCode)
        assertEquals("akarmain", userEntity.body!!["username"])
    }

    @Test
    @Order(5)
    fun `test user me 401`() {
        val userEntity = client.exchange<Map<String, Any>>("/api/user/me", HttpMethod.GET)
        assertEquals(HttpStatusCode.valueOf(401), userEntity.statusCode)
    }

    @Test
    @Order(6)
    fun `test user getById`() {
        val firstEntity = client.postForEntity<Map<String, Any>>("/api/auth/sign-in", WebAppAuthRequest("user=%7B%22id%22%3A5000472542%2C%22first_name%22%3A%22Akarmain%22%2C%22last_name%22%3A%22DevApp%22%2C%22username%22%3A%22akarmain%22%2C%22language_code%22%3A%22ru%22%2C%22allows_write_to_pm%22%3Atrue%7D&chat_instance=7253295287219311170&chat_type=private&auth_date=1731230130&hash=97d516259fb9a1b13ad5ce570e35a6cf41ff9496a26100304d598089153a509d", 5000472542, "Akarmain", "DevApp", "akarmain"))
        val token = firstEntity.body!!["token"]!!
        val userEntity = client.exchange<Map<String, Any>>("/api/user/5000472542", HttpMethod.GET, HttpEntity<Map<String, Any>>(HttpHeaders().apply {
            add("Authorization", "Bearer $token")
        }))

        assertEquals(HttpStatusCode.valueOf(200), userEntity.statusCode)
        assertEquals("akarmain", userEntity.body!!["username"])
    }

    @Test
    @Order(7)
    fun `test event create`() {
        val firstEntity = client.postForEntity<Map<String, Any>>("/api/auth/sign-in", WebAppAuthRequest("user=%7B%22id%22%3A5000472542%2C%22first_name%22%3A%22Akarmain%22%2C%22last_name%22%3A%22DevApp%22%2C%22username%22%3A%22akarmain%22%2C%22language_code%22%3A%22ru%22%2C%22allows_write_to_pm%22%3Atrue%7D&chat_instance=7253295287219311170&chat_type=private&auth_date=1731230130&hash=97d516259fb9a1b13ad5ce570e35a6cf41ff9496a26100304d598089153a509d", 5000472542, "Akarmain", "DevApp", "akarmain"))
        val token = firstEntity.body!!["token"]!!
        val eventEntity = client.exchange<Map<String, Any>>("/api/event/", HttpMethod.POST, HttpEntity<EventCreateRequest>(
            EventCreateRequest("test"), HttpHeaders().apply {
                add("Authorization", "Bearer $token")
            }
        ))

        assertEquals(HttpStatusCode.valueOf(200), eventEntity.statusCode)
        assertEquals("test", eventEntity.body!!["name"])
    }

    @Test
    @Order(7)
    fun `test event my`() {
        val firstEntity = client.postForEntity<Map<String, Any>>("/api/auth/sign-in", WebAppAuthRequest("user=%7B%22id%22%3A5000472542%2C%22first_name%22%3A%22Akarmain%22%2C%22last_name%22%3A%22DevApp%22%2C%22username%22%3A%22akarmain%22%2C%22language_code%22%3A%22ru%22%2C%22allows_write_to_pm%22%3Atrue%7D&chat_instance=7253295287219311170&chat_type=private&auth_date=1731230130&hash=97d516259fb9a1b13ad5ce570e35a6cf41ff9496a26100304d598089153a509d", 5000472542, "Akarmain", "DevApp", "akarmain"))
        val token = firstEntity.body!!["token"]!!
        client.exchange<Map<String, Any>>("/api/event/", HttpMethod.POST, HttpEntity<EventCreateRequest>(
            EventCreateRequest("test"), HttpHeaders().apply {
                add("Authorization", "Bearer $token")
            }
        ))

        val myEventsEntity = client.exchange<List<Map<String, Any>>>("/api/event/my", HttpMethod.GET, HttpEntity<Map<String, Any>>(HttpHeaders().apply {
            add("Authorization", "Bearer $token")
        }))

        assertEquals(HttpStatusCode.valueOf(200), myEventsEntity.statusCode)
        assertEquals(1, myEventsEntity.body!!.size)
    }

    @Test
    @Order(8)
    fun `test event getById`() {
        val firstEntity = client.postForEntity<Map<String, Any>>("/api/auth/sign-in", WebAppAuthRequest("user=%7B%22id%22%3A5000472542%2C%22first_name%22%3A%22Akarmain%22%2C%22last_name%22%3A%22DevApp%22%2C%22username%22%3A%22akarmain%22%2C%22language_code%22%3A%22ru%22%2C%22allows_write_to_pm%22%3Atrue%7D&chat_instance=7253295287219311170&chat_type=private&auth_date=1731230130&hash=97d516259fb9a1b13ad5ce570e35a6cf41ff9496a26100304d598089153a509d", 5000472542, "Akarmain", "DevApp", "akarmain"))
        val token = firstEntity.body!!["token"]!!
        val eventEntity = client.exchange<Map<String, Any>>("/api/event/", HttpMethod.POST, HttpEntity<EventCreateRequest>(
            EventCreateRequest("test"), HttpHeaders().apply {
                add("Authorization", "Bearer $token")
            }
        ))

        val getEntity = client.exchange<Map<String, Any>>("/api/event/${eventEntity.body!!["id"]}", HttpMethod.GET, HttpEntity<Map<String, Any>>(HttpHeaders().apply {
            add("Authorization", "Bearer $token")
        }))

        assertEquals(HttpStatusCode.valueOf(200), getEntity.statusCode)
        assertEquals("test", getEntity.body!!["name"])
    }

    @Test
    @Order(9)
    fun `test event modify`() {
        val firstEntity = client.postForEntity<Map<String, Any>>("/api/auth/sign-in", WebAppAuthRequest("user=%7B%22id%22%3A5000472542%2C%22first_name%22%3A%22Akarmain%22%2C%22last_name%22%3A%22DevApp%22%2C%22username%22%3A%22akarmain%22%2C%22language_code%22%3A%22ru%22%2C%22allows_write_to_pm%22%3Atrue%7D&chat_instance=7253295287219311170&chat_type=private&auth_date=1731230130&hash=97d516259fb9a1b13ad5ce570e35a6cf41ff9496a26100304d598089153a509d", 5000472542, "Akarmain", "DevApp", "akarmain"))
        val token = firstEntity.body!!["token"]!!
        val eventEntity = client.exchange<Map<String, Any>>("/api/event/", HttpMethod.POST, HttpEntity<EventCreateRequest>(
            EventCreateRequest("test"), HttpHeaders().apply {
                add("Authorization", "Bearer $token")
            }
        ))

        val modifyEntity = client.exchange<Map<String, Any>>("/api/event/${eventEntity.body!!["id"]}", HttpMethod.PUT, HttpEntity(EventUpdateRequest("new_name"), HttpHeaders().apply {
            add("Authorization", "Bearer $token")
        }))

        assertEquals(HttpStatusCode.valueOf(200), modifyEntity.statusCode)
        assertEquals("new_name", modifyEntity.body!!["name"])
    }

    @Test
    @Order(10)
    fun `test event delete`() {
        val firstEntity = client.postForEntity<Map<String, Any>>("/api/auth/sign-in", WebAppAuthRequest("user=%7B%22id%22%3A5000472542%2C%22first_name%22%3A%22Akarmain%22%2C%22last_name%22%3A%22DevApp%22%2C%22username%22%3A%22akarmain%22%2C%22language_code%22%3A%22ru%22%2C%22allows_write_to_pm%22%3Atrue%7D&chat_instance=7253295287219311170&chat_type=private&auth_date=1731230130&hash=97d516259fb9a1b13ad5ce570e35a6cf41ff9496a26100304d598089153a509d", 5000472542, "Akarmain", "DevApp", "akarmain"))
        val token = firstEntity.body!!["token"]!!
        val eventEntity = client.exchange<Map<String, Any>>("/api/event/", HttpMethod.POST, HttpEntity<EventCreateRequest>(
            EventCreateRequest("test"), HttpHeaders().apply {
                add("Authorization", "Bearer $token")
            }
        ))

        val modifyEntity = client.exchange<Map<String, Any>>("/api/event/${eventEntity.body!!["id"]}", HttpMethod.DELETE, HttpEntity<Map<String, Any>>(HttpHeaders().apply {
            add("Authorization", "Bearer $token")
        }))

        assertEquals(HttpStatusCode.valueOf(200), modifyEntity.statusCode)

        val myEventsEntity = client.exchange<String>("/api/event/my", HttpMethod.GET, HttpEntity<Map<String, Any>>(HttpHeaders().apply {
            add("Authorization", "Bearer $token")
        }))

        println(userRepository.findAll())
        println(eventMemberRepository.findAll())
        assertEquals(HttpStatusCode.valueOf(200), myEventsEntity.statusCode)
        assertEquals("[]", myEventsEntity.body!!)
    }

    companion object {
        @JvmStatic
        val postgres = PostgreSQLContainer(
            "postgres:17-alpine"
        )

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            postgres.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            postgres.stop()
        }
    }
}