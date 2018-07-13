/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.util

import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.zanata.model.HApplicationConfiguration

/**
 * @author Patrick Huang [pahuang@redhat.com](mailto:pahuang@redhat.com)
 */
object SampleDataResourceClient {

    private val EMPTY_ENTITY = Entity.text("")

    private fun createRequest(path: String): Invocation.Builder {
        return ResteasyClientBuilder()
                .build()
                .target(PropertiesHolder
                        .getProperty(Constants.zanataInstance
                                .value()) + "rest/test/data/sample" + path)
                .request(MediaType.APPLICATION_XML_TYPE)
                // having null username will bypass
                // ZanataRestSecurityInterceptor
                // clientRequest.header("X-Auth-User", null);
                .header("X-Auth-Token", PropertiesHolder
                        .getProperty(Constants.zanataApiKey.value()))

    }

    fun deleteExceptEssentialData() {
        val response = createRequest("").delete()
        assertThat(response.status).isEqualTo(200)
        response.close()
    }

    fun makeSampleUsers() {
        createRequest("/users").put(EMPTY_ENTITY).close()
    }

    fun allowAnonymousUser(allow: Boolean) {
        createRequest("/allowAnonymousUser/$allow").put(EMPTY_ENTITY).close()
    }

    /**
     * @param username
     * username to join language team
     * @param localesCSV
     * locale ids separated by comma
     */
    fun userJoinsLanguageTeam(username: String,
                              localesCSV: String) {
        ResteasyClientBuilder().build()
                .target(
                        PropertiesHolder.getProperty(Constants.zanataInstance
                                .value()) + "rest/test/data/sample/accounts/u/"
                                + username + "/languages")
                .queryParam("locales", localesCSV)
                .request(MediaType.APPLICATION_XML_TYPE)
                .header("X-Auth-Token",
                        PropertiesHolder.getProperty(Constants.zanataApiKey
                                .value()))
                .put(EMPTY_ENTITY).close()
    }

    fun makeSampleProject() {
        createRequest("/project").put(EMPTY_ENTITY).close()
    }

    fun setRateLimit(active: Int, concurrent: Int) {
        val restUrl = PropertiesHolder.getProperty(Constants.zanataInstance
                .value()) + "rest/configurations/c/"

        var response = ResteasyClientBuilder().build()
                .target(restUrl + HApplicationConfiguration.KEY_MAX_ACTIVE_REQ_PER_API_KEY)
                .queryParam("configValue", active)
                .request(MediaType.APPLICATION_XML_TYPE)
                .header("X-Auth-Token",
                        PropertiesHolder.getProperty(Constants.zanataApiKey
                                .value()))
                .header("X-Auth-User", "admin")
                .put(null)
        Assertions.assertThat(response.status).isLessThan(300)
        response.close()

        response = ResteasyClientBuilder().build()
                .target(restUrl + HApplicationConfiguration.KEY_MAX_CONCURRENT_REQ_PER_API_KEY)
                .queryParam("configValue", concurrent)
                .request(MediaType.APPLICATION_XML_TYPE)
                .header("X-Auth-Token",
                        PropertiesHolder.getProperty(Constants.zanataApiKey
                                .value()))
                .header("X-Auth-User", "admin")
                .put(null)
        Assertions.assertThat(response.status).isLessThan(300)
        response.close()
    }

    fun makeSampleLanguages() {
        createRequest("/languages").put(EMPTY_ENTITY).close()
    }

    fun addLanguage(localeId: String, pluralForms: String?) {
        val path = "/languages/l/" + localeId + if (pluralForms != null) "?pluralForms=$pluralForms" else ""
        createRequest(path).put(EMPTY_ENTITY).close()
    }

}
