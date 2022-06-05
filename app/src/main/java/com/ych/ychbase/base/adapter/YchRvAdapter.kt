package com.ych.ychbase.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.ych.ychbase.app.context
import com.ych.ychbase.util.RxBus

/**
 * RecyclerView Adapter的基类
 *
 * @author lmy
 */
abstract class YchRvAdapter<D> : RecyclerView.Adapter<YchRvAdapter<D>.BaseViewHolder>() {

    private var list: ArrayList<D> = ArrayList()

    val currentList: List<D>
        get() = list

    /**
     * 刷新数据
     *
     * @param list 要更新的数据
     */
    fun submitList(list: ArrayList<D>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * 合并list （用于加载更多）
     *
     * @param list
     */
    fun mergeList(list: ArrayList<D>) {
        this.list.addAll(list)
        notifyItemRangeInserted(this.list.size - list.size, list.size)
    }

    /**
     * 添加一条数据
     *
     * @param element 添加的元素
     */
    fun insert(element: D) {
        list.add(element)
        notifyItemInserted(list.size - 1)
    }

    /**
     * 移除一条数据
     *
     * @param element 移除的元素
     * @param position 集合中的位置
     */
    fun remove(element: D, position: Int) {
        list.remove(element)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, list.size - 1)
    }

    /** 清空数据 **/
    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(context)
            .inflate(
                layoutId,
                parent,
                false
            ))
    }

    /**
     * 子类必须实现，用于创建 view
     *
     * @return 布局文件 Id
     */
    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val itemView = holder.itemView
        val item = list[position]
        RxBus.clicks(itemView) {
            onItemClickListener.invoke(item, position)
        }
        bindItem(holder.itemView, item, position)
    }

    /**
     * 绑定item
     *
     * @param itemView
     * @param data
     * @param position
     */
    abstract fun bindItem(itemView: View, data: D, position: Int)

    override fun getItemCount(): Int = if (list.isNullOrEmpty()) 0 else list.size

    /** ViewHolder的基类 **/
    inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /** 设置item点击事件 **/
    private var onItemClickListener: (
        data: D,
        position: Int
    ) -> Unit = { _, _ -> }
    fun setOnItemClickListener(onItemClickListener: (
        data: D,
        position: Int
    ) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }
}