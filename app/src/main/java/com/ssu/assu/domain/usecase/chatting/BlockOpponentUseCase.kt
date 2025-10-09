package com.ssu.assu.domain.usecase.chatting

import com.ssu.assu.data.dto.chatting.request.BlockRequestDto
import com.ssu.assu.data.repository.chatting.ChattingRepository
import com.ssu.assu.domain.model.chatting.BlockOpponentModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class BlockOpponentUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(req: BlockRequestDto): RetrofitResult<BlockOpponentModel> {
        return repo.blockOpponent(req)
    }
}