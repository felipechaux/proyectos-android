package co.com.pagatodo.core.util.print

/**
 * Created by Administrator on 2017/5/24.
 */

class TableItem {
    var text: Array<String>? = null
    var width: IntArray? = null
    var align: IntArray? = null

    init {
        text = arrayOf("test", "test", "test")
        width = intArrayOf(1, 1, 1)
        align = intArrayOf(0, 0, 0)
    }
}
