package info.malignantshadow.api.util.pagination

class Paginator<T>(var elements: List<T> = ArrayList(), var perPage: Int = 7) {

    val pages: Int
        get() {
            if(elements.isEmpty()) return 0
            if(elements.size < perPage) return 1
            return (elements.size / perPage) + (if(elements.size.rem(perPage) > 0) 1 else 0)
        }

    fun getPage(page: Int): List<T> {
        val page_ = Math.max(1, Math.min(page, pages))
        val start = (page - 1) * perPage
        var pageList:List<T> = ArrayList()
        for(i in start until elements.size) {
            if(i == start + perPage) break
            pageList += elements[i]
        }
        return pageList
    }

    operator fun contains(page: Int): Boolean = page in 1..pages

}