package com.example.assu_fe_app.domain.usecase.chatting

import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.domain.model.chatting.GetBlockListModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetBlockListUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(): RetrofitResult<List<GetBlockListModel>> {
        return repo.getBlockList()
    }
}