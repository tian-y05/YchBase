package com.ych.ychbase.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ych.ychbase.R
import com.ych.ychbase.app.context
import com.ych.ychbase.util.RxBus
import kotlinx.android.synthetic.main.layout_empty.view.*

/**
 * 使用dataBinding的adapter基类
 *
 * @author lmy
 */
abstract class YchRvBindingAdapter<D, B: ViewDataBinding>
    : RecyclerView.Adapter<YchRvBindingAdapter<D, B>.BaseViewHolder<B>>() {

    private var list: ArrayList<D> = ArrayList()

    val currentList: List<D>
        get() = list

    /**
     * 刷新数据
     *
     * @param list 要更新的数据
     */
    open fun submitList(list: ArrayList<D>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * 合并list （用于加载更多）
     *
     * @param list
     */
    open fun mergeList(list: ArrayList<D>) {
        this.list.addAll(list)
        notifyItemRangeInserted(this.list.size - list.size, list.size)
    }

    /**
     * 添加一条数据
     *
     * @param element 添加的元素
     */
    open fun insert(element: D) {
        list.add(element)
        notifyItemInserted(list.size - 1)
    }

    /**
     * 移除一条数据
     *
     * @param element 移除的元素
     * @param position 集合中的位置
     */
    open fun remove(element: D, position: Int) {
        list.remove(element)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, list.size - 1)
    }

    /** 清空数据 **/
    open fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    /**
     * 子类必须实现，用于创建 view
     *
     * @return 布局文件 Id
     */
    @get:LayoutRes
    protected abstract val layoutId: Int

    protected var itemWidth: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
        parent.post {
            itemWidth = parent.width
        }
        return BaseViewHolder(
            LayoutInflater.from(context)
            .inflate(
                layoutId,
                parent,
                false
            ))
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