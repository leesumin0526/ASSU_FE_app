package com.assu.app.domain.usecase.chatting

import com.assu.app.data.repository.chatting.ChattingRepository
import com.assu.app.domain.model.chatting.GetBlockListModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetBlockListUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<GetBlockListModel>> {
        return repo.getBlockList()
    }
}