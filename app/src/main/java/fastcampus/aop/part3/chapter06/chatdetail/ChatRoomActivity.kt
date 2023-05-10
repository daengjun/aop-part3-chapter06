package fastcampus.aop.part3.chapter06.chatdetail

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part3.chapter06.DBKey.Companion.DB_CHATS
import fastcampus.aop.part3.chapter06.R

class ChatRoomActivity : AppCompatActivity() {

    // 사용자 정보 인스턴스 생성
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()
    private var chatDB: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        // 인텐트로 받아온 키
        val chatKey = intent.getLongExtra("chatKey", -1)

        // db 경로 채팅방
        chatDB = Firebase.database.reference.child(DB_CHATS).child("$chatKey")

        chatDB?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // 데이터 가져옴
                val chatItem = snapshot.getValue(ChatItem::class.java)
                // 데이터 값이 null 일땐 리턴
                chatItem ?: return

                // 리스트에 데이터 추가
                chatList.add(chatItem)
                // 리스트 어댑터에 데이터 전달
                adapter.submitList(chatList)
                // 갱신
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}


        })

        // 리사이클러뷰 초기화
        findViewById<RecyclerView>(R.id.chatRecyclerView).adapter = adapter
        findViewById<RecyclerView>(R.id.chatRecyclerView).layoutManager = LinearLayoutManager(this)

        // 리사이클러뷰 전송 클릭 리스너 설정
        findViewById<Button>(R.id.sendButton).setOnClickListener {
            val chatItem = ChatItem(
                // 현재 내 아이디 값
                senderId = auth.currentUser.uid,
                // 메세지
                message = findViewById<EditText>(R.id.messageEditText).text.toString()
            )

            chatDB?.push()?.setValue(chatItem)

        }


    }
}