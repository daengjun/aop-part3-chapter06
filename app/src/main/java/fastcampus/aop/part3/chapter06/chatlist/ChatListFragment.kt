package fastcampus.aop.part3.chapter06.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part3.chapter06.DBKey.Companion.CHILD_CHAT
import fastcampus.aop.part3.chapter06.DBKey.Companion.DB_USERS
import fastcampus.aop.part3.chapter06.R
import fastcampus.aop.part3.chapter06.chatdetail.ChatRoomActivity
import fastcampus.aop.part3.chapter06.databinding.FragmentChatlistBinding


// 채팅방 목록 표시
class ChatListFragment : Fragment(R.layout.fragment_chatlist) {

    private var binding: FragmentChatlistBinding? = null
    private lateinit var chatListAdapter: ChatListAdapter

    // 채팅방 데이터 담을 리스트 생성성
    private val chatRoomList = mutableListOf<ChatListItem>()

    // 사용자 정보 인스턴스 생성
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatlistBinding = FragmentChatlistBinding.bind(view)
        binding = fragmentChatlistBinding


        // 클릭시 채팅방으로 이동
        chatListAdapter = ChatListAdapter(onItemClicked = { chatRoom ->
            // 채팅방으로 이동 하는 코드
            context?.let {
                val intent = Intent(it, ChatRoomActivity::class.java)
                // 채팅방 키값 전달
                intent.putExtra("chatKey", chatRoom.key)
                startActivity(intent)
            }
        })

        // 탭 변경 하면 값이 계속 쌓이는거 방지하기위해서 초기화
        chatRoomList.clear()

        fragmentChatlistBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatlistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        // 로그인 정보가 없으면 리턴
        if (auth.currentUser == null) {
            return
        }


        // 내 db chat 경로
        val chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser.uid)
            .child(CHILD_CHAT)

        // 내 db에서 chat에 있는 전체 데이터 가져오기
        chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 여러개의 데이터를 한번에 넘어오기 때문에 foreach로 잘라서 chatroomlist에 넣어주기
                snapshot.children.forEach {
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }

                // 리스트 어댑터에 데이터 전달
                chatListAdapter.submitList(chatRoomList)
                // 화면 갱신 , 딱히 필요 x
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })


    }


    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }
}