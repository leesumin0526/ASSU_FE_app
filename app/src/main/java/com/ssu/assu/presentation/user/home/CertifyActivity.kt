package com.ssu.assu.presentation.user.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R

class CertifyActivity : AppCompatActivity() {

    // 예시 데이터 클래스
    data class Person(val isCertified: Boolean)

    // Activity 내부에 Adapter 정의
    inner class CertifyAdapter(private val data: List<Person>) :
        RecyclerView.Adapter<CertifyAdapter.CertifyVH>() {

        inner class CertifyVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivStatus: ImageView = itemView.findViewById(R.id.iv_person_status)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertifyVH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_certify_person, parent, false)
            return CertifyVH(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: CertifyVH, position: Int) {
            val person = data[position]
            holder.ivStatus.setImageResource(
                if (person.isCertified)
                    R.drawable.ic_certify_activated_people
                else
                    R.drawable.ic_certify_deactivated_people
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_certify)

        // inset 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.certify)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // RecyclerView 세팅
        val rv = findViewById<RecyclerView>(R.id.rv_certify_people)
        rv.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // 실제 데이터로 교체하세요
        val sampleList = listOf(
            Person(true),
            Person(false),
            Person(true),
            Person(true),
            Person(false)
        )
        rv.adapter = CertifyAdapter(sampleList)
    }
}