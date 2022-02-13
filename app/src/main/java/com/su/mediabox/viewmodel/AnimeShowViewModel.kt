package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.ResponseDataType
import com.su.mediabox.util.showToast
import com.su.mediabox.view.adapter.SerializableRecycledViewPool
import com.su.mediabox.plugin.interfaces.IAnimeShowModel
import com.su.mediabox.plugin.standard.been.IAnimeShowBean
import com.su.mediabox.plugin.standard.been.PageNumberBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AnimeShowViewModel : ViewModel() {
    private val animeShowModel: IAnimeShowModel by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IAnimeShowModel::class.java)
    }
    var childViewPool: SerializableRecycledViewPool? = null
    var viewPool: SerializableRecycledViewPool? = null
    var animeShowList: MutableList<IAnimeShowBean> = ArrayList()
    var mldGetAnimeShowList: MutableLiveData<Pair<ResponseDataType, MutableList<IAnimeShowBean>>> =
        MutableLiveData()   // value：-1错误；0重新获取；1刷新
    var pageNumberBean: PageNumberBean? = null

    private var isRequesting = false

    //http://www.yhdm.io版本
    fun getAnimeShowData(partUrl: String, isRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true
                pageNumberBean = null
                animeShowModel.getAnimeShowData(partUrl).apply {
                    pageNumberBean = second
                    mldGetAnimeShowList.postValue(
                        Pair(
                            if (isRefresh) ResponseDataType.REFRESH else ResponseDataType.LOAD_MORE, first
                        )
                    )
                    isRequesting = false
                }
            } catch (e: Exception) {
                mldGetAnimeShowList.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
                isRequesting = false
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    companion object {
        const val TAG = "AnimeShowViewModel"
    }
}