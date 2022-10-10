/*
* Copyright (c) 2019 NSTDA
*   National Science and Technology Development Agency, Thailand
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package nstda.hii.webservice.app

import com.fatboyindustrial.gsonjavatime.InstantConverter
import com.fatboyindustrial.gsonjavatime.LocalDateConverter
import com.fatboyindustrial.gsonjavatime.LocalDateTimeConverter
import com.fatboyindustrial.gsonjavatime.LocalTimeConverter
import com.fatboyindustrial.gsonjavatime.OffsetDateTimeConverter
import com.fatboyindustrial.gsonjavatime.OffsetTimeConverter
import com.fatboyindustrial.gsonjavatime.ZonedDateTimeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.Produces
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyReader
import jakarta.ws.rs.ext.MessageBodyWriter
import jakarta.ws.rs.ext.Provider
import nstda.hii.webservice.app.GsonJerseyProvider.Companion.hiiGson
import nstda.hii.webservice.getLogger
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZonedDateTime

/**
 * ตั้งค่าตัวแปลง Object ที่ใช้ระหว่างการส่งข้อมูลไป Client ให้กลายเป็น JSON
 */
@Provider
@Produces(MediaType.APPLICATION_JSON, "application/vnd.geo+json")
@Consumes(MediaType.APPLICATION_JSON, "application/vnd.geo+json")
class GsonJerseyProvider : MessageBodyWriter<Any>, MessageBodyReader<Any> {
    val logger = getLogger()
    override fun isReadable(
        type: Class<*>?,
        genericType: Type?,
        annotations: Array<out Annotation>?,
        mediaType: MediaType?
    ): Boolean {
        return true
    }

    @Throws(IOException::class)
    override fun readFrom(
        type: Class<Any>,
        genericType: Type,
        annotations: Array<Annotation>,
        mediaType: MediaType,
        httpHeaders: MultivaluedMap<String, String>,
        entityStream: InputStream
    ): Any? {
        try {
            InputStreamReader(
                entityStream,
                UTF_8
            ).use {
                try {
                    return hiiGson.fromJson<Any>(it, genericType)
                } catch (ex: java.lang.NumberFormatException) {
                    logger.info { "Json error ${ex.message}" }
                    val errormess = BadRequestException("JSON error ${ex.message}")
                    errormess.stackTrace = ex.stackTrace
                    throw errormess
                }
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            // Log exception
        }

        return null
    }

    override fun isWriteable(
        type: Class<*>,
        genericType: Type,
        annotations: Array<Annotation>,
        mediaType: MediaType
    ) = true

    override fun getSize(
        `object`: Any,
        type: Class<*>,
        genericType: Type,
        annotations: Array<Annotation>,
        mediaType: MediaType
    ): Long = -1

    @Throws(IOException::class, WebApplicationException::class)
    override fun writeTo(
        `object`: Any,
        type: Class<*>,
        genericType: Type,
        annotations: Array<Annotation>,
        mediaType: MediaType,
        httpHeaders: MultivaluedMap<String, Any>,
        entityStream: OutputStream
    ) {
        OutputStreamWriter(entityStream, UTF_8).use { writer ->
            hiiGson.toJson(`object`, genericType, writer)
        }
    }

    companion object {
        private val UTF_8 = "UTF-8"
        val hiiGson: Gson by lazy {
            GsonBuilder()
                .adapterFor<LocalDate>(LocalDateConverter())
                .adapterFor<LocalDateTime>(LocalDateTimeConverter())
                .adapterFor<LocalTime>(LocalTimeConverter())
                .adapterFor<OffsetDateTime>(OffsetDateTimeConverter())
                .adapterFor<OffsetTime>(OffsetTimeConverter())
                .adapterFor<ZonedDateTime>(ZonedDateTimeConverter())
                .adapterFor<Instant>(InstantConverter())
                .create()
        }
    }
}

private inline fun <reified T> GsonBuilder.adapterFor(adapter: Any): GsonBuilder {
    return registerTypeAdapter(typeTokenOf<T>(), adapter)
}

inline fun <reified T> typeTokenOf(): Type = object : TypeToken<T>() {}.type

fun Any.toJson(): String = hiiGson.toJson(this)
