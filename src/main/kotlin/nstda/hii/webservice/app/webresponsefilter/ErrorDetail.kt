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

package nstda.hii.webservice.app.webresponsefilter

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import nstda.hii.webservice.getLogger

/**
 * เวลาเกิด Error จะส่ง Object นี้ response ไปยัง client
 */
class ErrorDetail(
    val code: Int,
    val message: String?,
    t: Throwable
) {
    val tType = t::class.java.simpleName

    init {
        val logger = getLogger()
        logger.debug("${t.message}", t)
    }

    companion object {
        fun build(ex: WebApplicationException): Response {
            val err = ErrorDetail(ex.response.status, ex.message, ex)
            return Response.status(err.code).entity(err).type(MediaType.APPLICATION_JSON_TYPE).build()
        }

        fun build(ex: WebApplicationException, t: Throwable): Response {
            val err = ErrorDetail(ex.response.status, ex.message, t)
            return Response.status(err.code).entity(err).type(MediaType.APPLICATION_JSON_TYPE).build()
        }
    }
}
