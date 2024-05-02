package org.softwaremaestro.data.mylogin

import org.softwaremaestro.domain.mylogin.entity.Request

interface RequestBuilder {
    fun create(): Request
}