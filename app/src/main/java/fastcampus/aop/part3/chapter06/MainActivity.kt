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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        replaceFragment(homeFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList -> replaceFragment(chatListFragment)
                R.id.myPage -> replaceFragment(myPageFragment)
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }


    }

}