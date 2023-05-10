package fastcampus.aop.part3.chapter06.chatdetail

// 데이터 클래스 (채팅)
data class ChatItem(
    val senderId: String,
    val message: String
) {

    constructor() : this("", "")
}
