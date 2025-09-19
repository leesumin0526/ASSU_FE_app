package com.example.assu_fe_app.presentation.common.location.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.UserRole
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.data.dto.partnership.OpenContractArgs
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.ItemAdminPartnerLocationSearchResultItemBinding
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import javax.inject.Inject

class AdminPartnerLocationAdapter(
    private val role: UserRole,
    private val myName: String? = null,
    private val onOpenContract: (OpenContractArgs) -> Unit,
    private val onAskChat: (LocationAdminPartnerSearchResultItem) -> Unit
) :
    ListAdapter<LocationAdminPartnerSearchResultItem, AdminPartnerLocationAdapter.ViewHolder>(DiffCallback) {
    inner class ViewHolder(
        private val binding: ItemAdminPartnerLocationSearchResultItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LocationAdminPartnerSearchResultItem, isLastItem: Boolean) {
            binding.tvItemAdminPartnerLocationSearchResultItemShopName.text = item.shopName

            if (item.partnered) {
                binding.tvItemAdminPartnerLocationSearchResultItemPartnered.visibility = View.VISIBLE
                binding.tvItemAdminPartnerLocationSearchResultItemTerm.text = item.term
                binding.tvItemAdminPartnerLocationSearchResultItemContact.text = "제휴 계약서 보기"
            } else {
                binding.tvItemAdminPartnerLocationSearchResultItemPartnered.visibility = View.GONE
                binding.tvItemAdminPartnerLocationSearchResultItemTerm.text = item.address
                binding.tvItemAdminPartnerLocationSearchResultItemContact.text = "문의하기"
            }

            loadProfile(item.profileUrl)

            binding.viewItemAdminPartnerLocationSearchResultItemDivider.visibility =
                if (isLastItem) View.GONE else View.VISIBLE

            binding.tvItemAdminPartnerLocationSearchResultItemContact.setOnClickListener {
                val itemIsPartnered = item.partnered

                if (itemIsPartnered) {
                    val pid = item.partnershipId
                    if (pid != null) {
                        val (partnerName, adminName) = when (role) {
                            UserRole.ADMIN   -> item.shopName to myName
                            UserRole.PARTNER -> myName to item.shopName
                            else             -> item.shopName to myName
                        }

                        onOpenContract(
                            OpenContractArgs(
                                partnershipId = pid,
                                latitude = item.latitude,
                                longitude = item.longitude,
                                partnerName = partnerName,
                                adminName = adminName,
                                term = item.term,
                                profileUrl = item.profileUrl
                            )
                        )
                    } else {
                        android.util.Log.w("contract", "partnershipId is null; cannot open contract")
                    }
                } else {
                    onAskChat(item)
                }
            }
        }

        private fun loadProfile(imageUrl: String?) {
            val iv = binding.ivItemAdminPartnerLocationSearchResultItemImage

            val fallbackRes = when (role) {
                UserRole.ADMIN   -> R.drawable.img_partner
                UserRole.PARTNER -> R.drawable.img_ssu
                else             -> R.drawable.img_ssu
            }

            if (imageUrl.isNullOrBlank()) {
                iv.setImageResource(fallbackRes); return
            }

            // presigned 쿼리/프래그먼트 제거 후 확장자 체크
            val path = imageUrl.substringBefore('?').substringBefore('#')
            if (path.endsWith(".svg", ignoreCase = true)) {
                iv.setImageResource(fallbackRes); return
            }

            Glide.with(iv.context)
                .load(imageUrl)
                .placeholder(fallbackRes)
                .error(fallbackRes)
                .into(iv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminPartnerLocationSearchResultItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isLast = position == currentList.size - 1
        holder.bind(getItem(position), isLast)
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LocationAdminPartnerSearchResultItem>() {
            override fun areItemsTheSame(
                oldItem: LocationAdminPartnerSearchResultItem,
                newItem: LocationAdminPartnerSearchResultItem
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(
                oldItem: LocationAdminPartnerSearchResultItem,
                newItem: LocationAdminPartnerSearchResultItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}