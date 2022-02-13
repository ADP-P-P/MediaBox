package com.su.mediabox.view.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.config.Const.ViewHolderTypeInt
import com.su.mediabox.util.UpnpDevice1ViewHolder
import com.su.mediabox.util.showToast
import com.su.mediabox.view.activity.DlnaActivity
import com.su.mediabox.view.activity.DlnaControlActivity
import org.fourthline.cling.model.meta.Device

class UpnpAdapter(
    val activity: DlnaActivity,
    private val dataList: List<Device<*, *, *>>
) : SkinRvAdapter() {

    override fun getItemViewType(position: Int): Int = ViewHolderTypeInt.UPNP_DEVICE_1

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is UpnpDevice1ViewHolder -> {
                holder.tvUpnpDevice1Title.text = item.details?.friendlyName
                holder.itemView.setOnClickListener {
                    val key = System.currentTimeMillis().toString()
                    DlnaControlActivity.deviceHashMap[key] = item
                    activity.startActivity(
                        Intent(activity, DlnaControlActivity::class.java)
                            .putExtra("url", activity.url)
                            .putExtra("title", activity.title)
                            .putExtra("deviceKey", key)
                    )
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}