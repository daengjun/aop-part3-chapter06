package fastcampus.aop.part3.chapter06.home

// 데이터 클래스 (게시글)
data class ArticleModel(
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: String,
    val imageUrl: String
) {

    constructor(): this("", "", 0, "", "")

}