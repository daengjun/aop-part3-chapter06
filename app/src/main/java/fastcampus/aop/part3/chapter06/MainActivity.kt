package fastcampus.aop.part3.chapter06

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fastcampus.aop.part3.chapter06.chatlist.ChatListFragment
import fastcampus.aop.part3.chapter06.home.HomeFragment
import fastcampus.aop.part3.chapter06.mypage.MyPageFragment

/**
 * 인트로 (완성앱 & 구현 기능 소개)
메인 페이지 Tab UI 구성하기
상품 목록 페이지 UI 구성하기
Firebase Realtime Database 를 활용하여 DB 구조 구상하기
Firebase에서 상품 목록 가져와 보여주기
Firebase Storage 를 이용하여 사진 업로드 추가하기
마이페이지 구현하기
채팅 리스트 구현하기
채팅 페이지 구현하기 **/


// 채팅방 키값이 시간 이라 , 중복으로 생성되는데 다른 키값들을 조합해서 키를 사용해서 중복을 방지해야됨..
// 채팅방 목록에서 , 채팅방 마지막 말 표시해주면 좀더채팅방 같은 느낌..
// 채팅 룸에서 uid값 말고 닉네임을 사용하고 , 내 id에는 색을 준다던지 좌우로 ui를 나오게 만들어서 구현하면 좋을듯함

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 프래그먼트 인스턴스 생성
        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()

        // 바텀 네비게이션 인스턴스 생성
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)


        // 초기 진입시 홈프래그먼트 띄우기
        replaceFragment(homeFragment)

        // 네비게이션바 선택에따라 프래그먼트 보여줌
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList -> replaceFragment(chatListFragment)
                R.id.myPage -> replaceFragment(myPageFragment)
            }
            true
        }

    }


    // 보여줄 프래그먼트 변경
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }


    }

}