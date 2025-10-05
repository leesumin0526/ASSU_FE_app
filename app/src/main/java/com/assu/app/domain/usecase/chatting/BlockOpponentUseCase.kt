package com.assu.app.domain.usecase.chatting

import com.assu.app.data.dto.chatting.request.BlockRequestDto
import com.assu.app.data.repository.chatting.ChattingRepository
import com.assu.app.domain.model.chatting.BlockOpponentModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class BlockOpponentUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(req: BlockRequestDto): RetrofitResult<BlockOpponentModel> {
        return repo.blockOpponent(req)
    }
}