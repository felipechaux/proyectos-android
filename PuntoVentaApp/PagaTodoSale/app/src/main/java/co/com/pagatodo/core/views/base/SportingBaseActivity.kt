package co.com.pagatodo.core.views.base

import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.views.sporting.SportingSpacingItemDecoration

open class SportingBaseActivity: BaseActivity(){

    fun paddingForSportingItems(left: Int, top:Int, right: Int, bottom: Int, recyclerView: RecyclerView){
        if (DeviceUtil.isSalePoint()){
            recyclerView.setPadding(left,top,right,bottom)
            recyclerView.addItemDecoration(SportingSpacingItemDecoration())
        }
    }
}
