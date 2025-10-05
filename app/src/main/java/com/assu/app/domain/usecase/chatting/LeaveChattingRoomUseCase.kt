package com.assu.app.domain.usecase.chatting

import com.assu.app.data.repository.chatting.ChattingRepository
import com.assu.app.domain.model.chatting.LeaveChattingRoomModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class LeaveChattingRoomUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(roomId: Long) : RetrofitResult<LeaveChattingRoomModel> {
        return repo.leaveChattingRoom(roomId)
    }
}