package com.example.assu_fe_app.domain.usecase.chatting

import com.example.assu_fe_app.data.dto.chatting.request.BlockRequestDto
import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.domain.model.chatting.BlockOpponentModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class BlockOpponentUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(req: BlockRequestDto): RetrofitResult<BlockOpponentModel> {
        return repo.blockOpponent(req)
    }
}