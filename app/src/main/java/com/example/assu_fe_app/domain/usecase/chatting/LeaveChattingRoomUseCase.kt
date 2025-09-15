package com.example.assu_fe_app.domain.usecase.chatting

import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.domain.model.chatting.LeaveChattingRoomModel
import com.example.assu_fe_app.util.RetrofitResult
import retrofit2.Retrofit
import javax.inject.Inject

class LeaveChattingRoomUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(roomId: Long) : RetrofitResult<LeaveChattingRoomModel> {
        return repo.leaveChattingRoom(roomId)
    }
}