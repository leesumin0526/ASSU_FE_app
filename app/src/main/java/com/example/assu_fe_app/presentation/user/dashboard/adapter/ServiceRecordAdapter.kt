package com.example.assu_fe_app.presentation.user.dashboard.adapter

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.data.dto.usage.ServiceRecord
import com.example.assu_fe_app.databinding.ItemServiceRecordBinding
import com.example.assu_fe_app.presentation.user.review.writing.UserStarReviewActivity
import java.time.format.DateTimeFormatter
import kotlin.jvm.java
import com.example.assu_fe_app.R

class ServiceRecordAdapter : RecyclerView.Adapter<ServiceRecordAdapter.ServiceRecordViewHolder>() {

    private val serviceRecordList = mutableListOf<ServiceRecord>()

    fun setData(newList: List<ServiceRecord>) {
        serviceRecordList.clear()
        serviceRecordList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = serviceRecordList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceRecordViewHolder {
        val binding = ItemServiceRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceRecordViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ServiceRecordViewHolder, position: Int) {
        holder.bind(serviceRecordList[position])
    }

    // 내부 ViewHolder 클래스
    inner class ServiceRecordViewHolder(private val binding: ItemServiceRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(serviceRecord: ServiceRecord) {
            binding.tvMarket.text = serviceRecord.marketName
            binding.tvServiceExplain.text = serviceRecord.serviceContent
//
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            binding.tvServiceDatetime.text = serviceRecord.dateTime

            if (serviceRecord.isReviewd) {
                // 리뷰를 작성한 경우 (true)
                binding.btnServiceWriteReview.isEnabled = false // 버튼 비활성화
                binding.btnServiceWriteReview.setBackgroundResource(R.color.assu_box) // 비활성화 색상 적용
                binding.btnServiceWriteReview.setTextColor(ContextCompat.getColor(itemView.context, R.color.assu_font_sub))  // 텍스트 색상 변경
            } else {
                // 리뷰를 작성하지 않은 경우 (false)
                binding.btnServiceWriteReview.isEnabled = true // 버튼 활성화
                binding.btnServiceWriteReview.setBackgroundResource(R.color.assu_sub3) // 활성화 색상 적용
                binding.btnServiceWriteReview.setTextColor(ContextCompat.getColor(itemView.context, R.color.assu_font_main))  // 텍스트 색상 변경

                // 클릭 리스너 설정 (false일 때만)
                binding.btnServiceWriteReview.setOnClickListener {
                    val intent = Intent(itemView.context, UserStarReviewActivity::class.java).apply {
                        putExtra("storeName", serviceRecord.marketName)
                        putExtra("partnershipUsageId", serviceRecord.id) // storeId도 넘겨주는 것이 좋습니다.
                        putExtra("adminName", serviceRecord.adminName)
                        putExtra("content", serviceRecord.serviceContent)
                        putExtra("storeId", serviceRecord.storeId)
                        putExtra("partnerId", serviceRecord.partnerId)
                    }
                    itemView.context.startActivity(intent)
                }
            }


        }
    }
}