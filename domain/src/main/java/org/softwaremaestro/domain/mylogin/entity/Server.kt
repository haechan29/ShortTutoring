package org.softwaremaestro.domain.mylogin.entity

import org.softwaremaestro.domain.mylogin.entity.dto.RequestDto
import org.softwaremaestro.domain.mylogin.entity.dto.ResponseDto
import org.softwaremaestro.domain.mylogin.entity.result.NetworkResult

interface Server {
    suspend fun <Req: Request<RequestDto>, Res: Response<ResponseDto>> send(request: Req): Res
}

interface Request<out Dto: RequestDto>
interface Response<out Dto: ResponseDto> {
    val body: NetworkResult<Dto>
}