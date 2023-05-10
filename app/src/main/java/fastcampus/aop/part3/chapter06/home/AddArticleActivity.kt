package fastcampus.aop.part3.chapter06.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import fastcampus.aop.part3.chapter06.DBKey.Companion.DB_ARTICLES
import fastcampus.aop.part3.chapter06.R

// 아이템 작성하는 액티비티
class AddArticleActivity : AppCompatActivity() {

    // 받아온 사진 uri
    private var selectedUri: Uri? = null

    // 사용자 정보 인스턴스
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    // 저장소 인스턴스
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    // DB 인스턴스
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_article)


        // 이미지 추가 버튼 리스너 정의
        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
            // 권한이 부여 되어 있는지 확인
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 권한이 부여 되어 있다면
                    startContentProvider()
                }

                // 사용자 에게 한번더 물어 보기
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                // 권한이 없으면 권한 요청창 띄우기
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }


            }

        }

        // 등록 버튼 클릭 이벤트 정의
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val price = findViewById<EditText>(R.id.priceEditText).text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()

            // 프로그래스바 띄우기
            showProgress()

            // 중간에 이미지가 있으면 업로드 과정을 추가
            // selectedUri -> 사진 선택했을때 가져왔던 uri 값
            // 데이터 스트림으로 보내는 경우도 있다고함..
            if (selectedUri != null) {
                // 엘비스 연산자 이용해서 값이 null이면 종료
                val photoUri = selectedUri ?: return@setOnClickListener
                // 인자로 uri값 , 람다 함수 전달
                uploadPhoto(photoUri,
                    successHandler = { uri ->
                        uploadArticle(sellerId, title, price, uri)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                )
            } else {
                uploadArticle(sellerId, title, price, "")
            }
        }
    }


    // 고차 함수 사용
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png" // 현재 시간명으로 파일명 생성
        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            // 업로드가 완료된 콜백
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        // url 전달하는 콜백
                        .addOnSuccessListener { uri ->
                            // 성공일때 uploadArticle 실행 , 이떄 uri는 저장소에 저장된 이미지 파일의 url주소
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            // 에러메시지 출력
                            errorHandler()
                        }
                } else {
//                    서버에 업로드 실패
                    errorHandler()
                }
            }
    }

    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String) {

        // 서버로 모델채로 전송
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "$price 원", imageUrl)
        articleDB.push().setValue(model)

        // 프로그래스바 종료
        hideProgress()

        // 게시물 등록 액티비티 종료
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            // 권한 처리 결과
            1010 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 허용O
                    startContentProvider()
                } else {
                    // 권한 허용X
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startContentProvider() {
        // 사진 선택 해서 uri 가져오기
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)

    }

    // 프로그래스바 보이기
    private fun showProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true
    }


    // 프로그래스바 숨김
    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }

    // 사진 가져올 때 uri 결과 반환
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 취소이거나 실패일 때
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            // 정상적으로 가져 왔을 때
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    // 미리보기 창에 사진 띄우기
                    findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
                    // 전역 변수에 uri 값 담기 (사진 저장소에 전달할때 사용하기위해서)
                    selectedUri = uri
                } else {
                    // 예외 처리 실패
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }

            }
            else -> {
                // 예외 처리 실패
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        // 권한이 필요한 이유 설명 , 취소 버튼은 생략
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()

    }

}