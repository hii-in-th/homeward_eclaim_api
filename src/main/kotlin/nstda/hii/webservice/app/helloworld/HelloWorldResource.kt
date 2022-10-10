package nstda.hii.webservice.app.helloworld

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import nstda.hii.webservice.app.webcache.Cache
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZonedDateTime

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class HelloWorldResource {

    // http://localhost:8080/v1/hello
    @GET
    @Cache(maxAge = 5)
    fun hello(): HelloMessage {
        val zoneDateTime = ZonedDateTime.now()
        val offsetDateTime = zoneDateTime.toOffsetDateTime()
        val localDateTime = zoneDateTime.toLocalDateTime()
        val instant = zoneDateTime.toInstant()

        return HelloMessage(
            "Hello World",
            zoneDateTime,
            localDateTime.toLocalTime(),
            localDateTime,
            localDateTime.toLocalDate(),
            offsetDateTime,
            offsetDateTime.toOffsetTime(),
            instant
        )
    }

    data class HelloMessage(
        val hello: String,
        val javaZonedDateTime: ZonedDateTime,
        val javaLocalTime: LocalTime,
        val javaDateTime: LocalDateTime,
        val javaDate: LocalDate,
        val javaOffsetDateTime: OffsetDateTime,
        val javaOffsetTime: OffsetTime,
        val javaInstant: Instant
    )
}
