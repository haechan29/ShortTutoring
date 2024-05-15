package org.softwaremaestro.data.fake_login.legacy

import org.softwaremaestro.domain.fake_login.entity.LoginToken
import org.softwaremaestro.domain.fake_login.result.NetworkResult

interface IssueLoginTokenRepository {
    suspend fun issueToken(): NetworkResult<Unit>
}

interface IssueAccessTokenRepository: IssueLoginTokenRepository
interface IssueRefreshTokenRepository: IssueLoginTokenRepository