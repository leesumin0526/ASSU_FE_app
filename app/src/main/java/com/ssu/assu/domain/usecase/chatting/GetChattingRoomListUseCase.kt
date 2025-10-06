package com.ssu.assu.domain.usecase.chatting

import com.ssu.assu.data.repository.chatting.ChattingRepository
import com.ssu.assu.domain.model.chatting.GetChattingRoomListModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetChattingRoomListUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<GetChattingRoomListModel>> {
        return repo.getChattingRoomList()
    }
}