package com.example.assu_fe_app

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min
import com.example.assu_fe_app.databinding.ActivityPartnerQrSaveBinding

class QrSaveFragment : Fragment() {

    private var _binding: ActivityPartnerQrSaveBinding? = null
    private val binding get() = _binding!!

    private val sessionId = 12345L
    private val adminId = 9L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityPartnerQrSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupClicks()
    }

    private fun initViews() {
        val content = buildQrContent(sessionId, adminId)

        binding.ivQrImage.post {
            val boxSize = min(binding.ivQrImage.width, binding.ivQrImage.height)
                .coerceAtLeast(600)
            val preview = createQrBitmap(
                contents = content,
                sizePx = boxSize,
                margin = 1,
                ecLevel = ErrorCorrectionLevel.M
            )
            binding.ivQrImage.setImageBitmap(preview)
        }
    }

    private fun setupClicks() {
        binding.btnSaveCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // "QR 저장하기" 영역 클릭 -> 고해상도 생성 후 갤러리 저장
        binding.layoutSaveQrImage.setOnClickListener {
            val content = buildQrContent(sessionId, adminId)
            val hi = createQrBitmap(
                contents = content,
                sizePx = 2048,               // 인쇄 고려 고해상도
                margin = 2,
                ecLevel = ErrorCorrectionLevel.H
            )
            lifecycleScope.launch {
                val uri = saveToGallery(hi, "partner_qr_${System.currentTimeMillis()}.png")
                Toast.makeText(
                    requireContext(),
                    if (uri != null) "QR 이미지가 저장되었습니다." else "저장에 실패했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun buildQrContent(sessionId: Long, adminId: Long): String {
        return "assu://certify?sessionId=$sessionId&adminId=$adminId"
    }

    private fun createQrBitmap(
        contents: String,
        sizePx: Int,
        margin: Int,
        ecLevel: ErrorCorrectionLevel
    ): Bitmap {
        val hints = hashMapOf<EncodeHintType, Any>(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to margin,
            EncodeHintType.ERROR_CORRECTION to ecLevel
        )
        val matrix: BitMatrix = MultiFormatWriter().encode(
            contents,
            BarcodeFormat.QR_CODE,
            sizePx,
            sizePx,
            hints
        )
        val w = matrix.width
        val h = matrix.height
        val pixels = IntArray(w * h)
        var offset = 0
        val black = Color.BLACK
        val white = Color.WHITE
        for (y in 0 until h) {
            for (x in 0 until w) {
                pixels[offset + x] = if (matrix[x, y]) black else white
            }
            offset += w
        }
        return Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888)
    }

    // MediaStore에 저장 (Android 10+ 무권한, 이하만 권한 필요)
    private suspend fun saveToGallery(bitmap: Bitmap, fileName: String): Uri? =
        withContext(Dispatchers.IO) {
            val resolver = requireContext().contentResolver
            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ASSU")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = resolver.insert(collection, values) ?: return@withContext null
            try {
                resolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                }
                uri
            } catch (e: Exception) {
                resolver.delete(uri, null, null)
                null
            }
        }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}