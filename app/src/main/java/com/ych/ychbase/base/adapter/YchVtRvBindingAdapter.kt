package com.ych.ychbase.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ych.ychbase.app.context
import com.ych.ychbase.util.RxBus

abstract class YchVtRvBindingAdapter<D, B: ViewDataBinding> : RecyclerView.Adapter<YchVtRvBindingAdapter<D, B>.BaseViewHolder<B>>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
        return create(parent, viewType)
    }

    /**
     * 提供给子类创建ViewHolder的方法
     *
     * @param parent
     * @param viewType
     *
     * @return
     */
    abstract fun create(parent: ViewGroup, viewType: Int): BaseViewHolder<B>

    /**
     * 绑定viewHolder
     *
     * @param layoutId
     * @param parent
     *
     * @return
     */
    fun bindViewHolder(@LayoutRes layoutId: Int, parent: ViewGroup): BaseViewHolder<B> {
        return BaseViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    layoutId,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<B>, position: Int) {
        val binding = holder.binding!!
        val item = list[position]
        RxBus.clicks(binding.root) {
            onItemClickListener.invoke(item, position)
        }
        bindItem(holder.binding!!, item, position)
    }

    /**
     * 绑定item
     *
     * @param binding
     * @param data
     * @param position
     */
    abstract fun bindItem(binding: B, data: D, position: Int)

    override fun getItemCount(): Int = if (list.isNullOrEmpty()) 0 else list.size

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

    /** ViewHolder的基类 **/
    inner class BaseViewHolder<B: ViewDataBinding>(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        val binding: B? by lazy {
            DataBindingUtil.bind<B>(itemView)
        }
    }
}