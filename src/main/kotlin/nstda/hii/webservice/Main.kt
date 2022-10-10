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

package nstda.hii.webservice

import nstda.hii.webservice.webconfig.server
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option

/**
 * Main application
 */
class Main(val args: Array<String>) {

    @Option(name = "-port", usage = "port destination to start nstda.hii.webservice.webconfig.server")
    private var port = 8080

    init {
        try {
            CmdLineParser(this).parseArgument(*args)
        } catch (cmd: CmdLineException) {
        }
    }

    fun start() {
        val server = server("0.0.0.0", port)
        logger.info { "running port $port" }
        server.setRequestLog { request, response ->
            logger.info {
                var message =
                    "HttpLog\t" + "Time:${System.currentTimeMillis()}\t" + "Status:${response.status}\t" + "Proto:${request.method}::" + request.originalURI
                request.headerNames.toList().forEach { key ->
                    if (key != "Authorization") message += "\t$key:${request.getHeader(key)}"
                }
                message += "\tInputIpAddress:${request.remoteAddr}"
                message
            }
        }
        server.start()
        server.join()
    }

    companion object {
        private val logger = getLogger()

        @JvmStatic
        fun main(args: Array<String>) {
            Main(args).start()
        }
    }
}
