package fastcampus.aop.part3.chapter06.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part3.chapter06.R
import fastcampus.aop.part3.chapter06.databinding.FragmentMypageBinding

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding

        // 로그인 로그 아웃 버튼 클릭 리스너
        fragmentMypageBinding.signInOutButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()


                //signInWithEmailAndPassword 사용 해서 아이디 유효성 검사
                // 현재 로그인 한 사용자가 없다면 로그인 진행
                if (auth.currentUser == null) {
                    //로그인
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                successSignIn()
                            } else {
                                Toast.makeText(
                                    context,
                                    "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    // 로그인 되어 있을 경우에
                } else {
                    // 로그아웃 진행
                    auth.signOut()
                    // 이메일 텍스트 , 패스워드 텍스트 초기화및 활성화
                    binding.emailEditText.text.clear()
                    binding.emailEditText.isEnabled = true
                    binding.passwordEditText.text.clear()
                    binding.passwordEditText.isEnabled = true

                    // 로그인 텍스트 로그인으로 변경
                    binding.signInOutButton.text = "로그인"

                    // false값이 초기화 , 아이디 비밀번호 입력하면 활성화됨
                    binding.signInOutButton.isEnabled = false
                    binding.signUpButton.isEnabled = false

                }

            }

        }

        // 가입 버튼 클릭 리스너
        // 이메일 형식이 잘못되거나 , 비밀번호가 6자리 미만이면 사용 불가
        fragmentMypageBinding.signUpButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "회원가입에 성공했습니다. 로그인 버튼을 눌러주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "회원가입에 실패했습니다. 이미 가입한 이메일일 수 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

            }

        }

        // 이메일 텍스트 입력
        fragmentMypageBinding.emailEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                // 회원 가입 , 로그인 버튼 활성화/비 활성화
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }
        }

        // 패스 워드 입력
        fragmentMypageBinding.passwordEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                // 회원 가입 , 로그인 버튼 활성화/비 활성화
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // 로그인 되어있지 않으면
        if (auth.currentUser == null) {
            binding?.let { binding ->
                binding.emailEditText.text.clear()
                binding.passwordEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.isEnabled = true
                binding.signInOutButton.text = "로그인"
                binding.signInOutButton.isEnabled = false
                binding.signUpButton.isEnabled = false
            }
        } else {
            // 로그인 되어있다면 비밀번호 **** 로 표시 , 유저 아이디 이메일 입력 , 나머지 비활성화 , 텍스트 로그아웃으로 변경 (프래그먼트 다른탭 갔다 왔을때 동작)
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser.email)
                binding.passwordEditText.setText("***********")
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false
                binding.signInOutButton.text = "로그아웃"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled = false
            }
        }
    }


    private fun successSignIn() {
        if (auth.currentUser == null) {
            // 예외 처리 실패
            Toast.makeText(context, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 전체 비 활성화 , 로그인 버튼 텍스트 로그아웃으로 변경

        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpButton?.isEnabled = false
        binding?.signInOutButton?.text = "로그아웃"

    }

}