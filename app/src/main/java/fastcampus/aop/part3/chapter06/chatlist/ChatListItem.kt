package fastcampus.aop.part3.chapter06.chatlist

data class ChatListItem(
    val buyerId: String,
    val sellerId: String,
    val itemTitle: String,
    val key: Long //현재 시간값
) {


    // 서버로 전송할 때는 생성자가 있어야 함
    constructor() : this("", "", "", 0)
}