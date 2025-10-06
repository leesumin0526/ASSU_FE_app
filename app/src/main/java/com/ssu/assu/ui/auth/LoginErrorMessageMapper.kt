package com.ssu.assu.ui.auth

import android.util.Log
import com.ssu.assu.util.RetrofitResult
import org.json.JSONObject

object LoginErrorMessageMapper {
    
    /**
     * 로그인 관련 서버 에러를 사용자 친화적인 메시지로 변환
     * @param fail RetrofitResult.Fail 객체
     * @return 사용자에게 표시할 친화적인 메시지
     */
    fun getLoginErrorMessage(fail: RetrofitResult.Fail): String {
        val httpStatusCode = fail.code
        val serverMessage = fail.message
        val resultMessage = fail.result ?: ""
        
        Log.d("LoginErrorMessageMapper", "로그인 에러 변환 - HTTP code: '$httpStatusCode', message: '$serverMessage', result: '$resultMessage'")
        
        // 서버 메시지에서 실제 에러 코드와 결과 메시지 추출
        val extractedData = extractErrorDataFromMessage(serverMessage)
        val actualErrorCode = extractedData.first
        val extractedResultMessage = extractedData.second
        
        Log.d("LoginErrorMessageMapper", "추출된 에러 코드: '$actualErrorCode'")
        Log.d("LoginErrorMessageMapper", "추출된 결과 메시지: '$extractedResultMessage'")
        
        return when {
            // 네트워크 에러
            serverMessage.contains("네트워크") || 
            serverMessage.contains("network", ignoreCase = true) ||
            serverMessage.contains("offline", ignoreCase = true) -> 
                "네트워크 연결을 확인해주세요."
            
            // ===== 로그인 관련 에러 =====
            actualErrorCode == "MEMBER_4001" -> 
                "이메일을 확인해주세요."
            
            // Bad credentials 체크를 COMMON500보다 먼저 처리
            actualErrorCode == "COMMON500" && (resultMessage.contains("Bad credentials", ignoreCase = true) || extractedResultMessage.contains("Bad credentials", ignoreCase = true)) -> 
                "비밀번호가 틀렸습니다."
            
            actualErrorCode == "COMMON500" -> 
                "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            
            actualErrorCode == "SSU4000" -> 
                "숭실대학교 유세인트 로그인에 실패했습니다."
            
            // ===== HTTP 상태 코드 기반 에러 (fallback) =====
            httpStatusCode == "400" ->
                "잘못된 요청입니다. 입력 정보를 확인해주세요."
            
            httpStatusCode == "401" ->
                "인증에 실패했습니다."
            
            httpStatusCode == "403" ->
                "접근 권한이 없습니다."
            
            httpStatusCode == "404" ->
                "요청한 정보를 찾을 수 없습니다."
            
            httpStatusCode == "500" ->
                "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            
            // 기본 에러 메시지
            else -> {
                Log.w("LoginErrorMessageMapper", "매핑되지 않은 로그인 에러: HTTP code=$httpStatusCode, actual code=$actualErrorCode, message=$serverMessage")
                "로그인에 실패했습니다. 잠시 후 다시 시도해주세요."
            }
        }
    }
    
    /**
     * 서버 메시지에서 실제 에러 코드와 결과 메시지를 추출
     * @param serverMessage 서버에서 받은 메시지 (JSON 형태일 수 있음)
     * @return Pair<에러코드, 결과메시지>
     */
    private fun extractErrorDataFromMessage(serverMessage: String): Pair<String, String> {
        return try {
            // JSON 형태의 메시지인지 확인
            if (serverMessage.startsWith("{") && serverMessage.endsWith("}")) {
                val jsonObject = JSONObject(serverMessage)
                val errorCode = jsonObject.optString("code", "")
                val resultMessage = jsonObject.optString("result", "")
                Log.d("LoginErrorMessageMapper", "JSON에서 추출된 에러 코드: '$errorCode', 결과: '$resultMessage'")
                Pair(errorCode, resultMessage)
            } else {
                // JSON이 아닌 경우 원본 메시지 반환
                Log.d("LoginErrorMessageMapper", "JSON이 아닌 메시지: '$serverMessage'")
                Pair(serverMessage, "")
            }
        } catch (e: Exception) {
            Log.w("LoginErrorMessageMapper", "JSON 파싱 실패: ${e.message}")
            Pair(serverMessage, "")
        }
    }
}
