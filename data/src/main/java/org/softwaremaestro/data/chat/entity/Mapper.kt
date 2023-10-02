package org.softwaremaestro.data.chat.entity

import android.util.Log
import com.google.gson.Gson
import org.softwaremaestro.data.chat.model.ChatRoomDto
import org.softwaremaestro.data.common.utils.parseToLocalDateTime
import org.softwaremaestro.domain.chat.entity.ChatRoomVO
import org.softwaremaestro.domain.chat.entity.MessageBodyVO
import org.softwaremaestro.domain.chat.entity.MessageVO
import org.softwaremaestro.domain.chat.entity.QuestionState
import org.softwaremaestro.domain.chat.entity.RoomType
import java.time.LocalDateTime
import java.time.ZoneId

class Mapper {

    fun asDomain(chatRoomEntity: ChatRoomEntity): ChatRoomVO {
        chatRoomEntity.apply {
            return ChatRoomVO(
                id = id,
                title = title,
                subTitle = subTitle,
                roomType = RoomType.TEACHER,
                roomImage = image,
                questionId = questionId,
                isSelect = isSelect,
                startDateTime = startDateTime,
                description = description ?: "undefined",
                questionState =
                if (status == ChatRoomType.PROPOSED_NORMAL.type ||
                    status == ChatRoomType.PROPOSED_SELECT.type
                )
                    QuestionState.PROPOSED else QuestionState.RESERVED,
            )
        }
    }

    fun asDomain(messageEntity: MessageEntity): MessageVO {
        messageEntity.apply {
            var bodyVO: MessageBodyVO?
            val gson = Gson()
            bodyVO = try {
                when (format) {
                    "text" -> gson.fromJson(body, MessageBodyVO.Text::class.java)
                    "problem-image" -> gson.fromJson(body, MessageBodyVO.ProblemImage::class.java)

                    "appoint-request" -> gson.fromJson(
                        body,
                        MessageBodyVO.AppointRequest::class.java
                    )

                    "request-decline" -> MessageBodyVO.RequestDecline

                    "reserve-confirm" -> gson.fromJson(
                        body,
                        MessageBodyVO.ReserveConfirm::class.java
                    )

                    else -> MessageBodyVO.Text(body)
                }
            } catch (e: Exception) {
                MessageBodyVO.Text(body)
            }
            return MessageVO(
                time = sendAt,
                bodyVO = bodyVO,
                isMyMsg = isMyMsg,
            )
        }
    }

    fun asDomain(chatRoomWithMessages: ChatRoomWithMessages): ChatRoomVO {
        chatRoomWithMessages.apply {
            Log.d("chatRoomWithMessages Mapper ", this.toString())
            return ChatRoomVO(
                id = chatRoomEntity.id,
                title = chatRoomEntity.title,
                roomType = if (chatRoomEntity.opponentId != null) RoomType.TEACHER else RoomType.QUESTION,
                roomImage = chatRoomEntity.image,
                questionId = chatRoomEntity.questionId,
                isSelect = chatRoomEntity.isSelect,
                subTitle = chatRoomEntity.subTitle,
                questionState =
                if (chatRoomEntity.status == ChatRoomType.PROPOSED_NORMAL.type ||
                    chatRoomEntity.status == ChatRoomType.PROPOSED_SELECT.type
                )
                    QuestionState.PROPOSED else QuestionState.RESERVED,
                messages = messages.map { it.asDomain() },
                opponentId = chatRoomEntity.opponentId,
                description = chatRoomEntity.description ?: "undefined",
            )
        }
    }

    fun asEntity(dto: ChatRoomDto): ChatRoomEntity {
        var status =
            when (dto.isSelect == true) {
                true -> when (dto.questionState) {
                    "pending" -> ChatRoomType.PROPOSED_SELECT.type
                    "reserved" -> ChatRoomType.RESERVED_SELECT.type
                    else -> ChatRoomType.PROPOSED_SELECT.type
                }

                false -> {
                    when (dto.questionState) {
                        "pending" -> ChatRoomType.PROPOSED_NORMAL.type
                        "reserved" -> ChatRoomType.RESERVED_NORMAL.type
                        else -> ChatRoomType.PROPOSED_NORMAL.type
                    }
                }
            }
        Log.d("ChatRoomDto", "to entity Mapper${dto} ${status}")

        return ChatRoomEntity(
            id = dto.id ?: dto.questionId ?: "undefined",
            title = dto.title!!,
            image = dto.roomImage,
            status = status,
            startDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")),
            opponentId = dto.opponentId,
            questionId = dto.questionId,
            subTitle =
            if (dto.questionState != "reserved")
                "${dto.questionInfo?.problem?.schoolLevel} ${dto.questionInfo?.problem?.schoolSubject}"
            else "${dto.reservedStart?.parseToLocalDateTime()?.toKoreanString()}",
            isSelect = dto.isSelect ?: false,
            description = dto.questionInfo?.problem?.description ?: "undefined",
        )
    }
}

fun ChatRoomEntity.asDomain(): ChatRoomVO {
    return Mapper().asDomain(this)
}

fun MessageEntity.asDomain(): MessageVO {
    return Mapper().asDomain(this)
}

fun ChatRoomWithMessages.asDomain(): ChatRoomVO {
    return Mapper().asDomain(this)
}

fun ChatRoomDto.asEntity(): ChatRoomEntity {
    return Mapper().asEntity(this)
}

fun LocalDateTime.toKoreanString(): String {
    return "${this.monthValue}월 ${this.dayOfMonth}일 ${this.hour}시 ${
        if (this.minute != 0) "${minute}분" else ""
    }"
}