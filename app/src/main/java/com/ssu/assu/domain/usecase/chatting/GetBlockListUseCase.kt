package com.ssu.assu.domain.usecase.chatting

import com.ssu.assu.data.repository.chatting.ChattingRepository
import com.ssu.assu.domain.model.chatting.GetBlockListModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetBlockListUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<GetBlockListModel>> {
        return repo.getBlockList()
    }
}