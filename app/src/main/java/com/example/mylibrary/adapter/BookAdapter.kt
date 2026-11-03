package com.example.mylibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mylibrary.R
import com.example.mylibrary.data.local.BookEntity

class BookAdapter(
    private val onBookClick: (BookEntity) -> Unit
) : ListAdapter<BookEntity, BookAdapter.BookViewHolder>(DIFF_CALLBACK) {

    class BookViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvTitle: TextView =
            view.findViewById(R.id.tvBookTitle)

        val tvAuthor: TextView =
            view.findViewById(R.id.tvBookAuthor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)

        return BookViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BookViewHolder,
        position: Int
    ) {
        val book = getItem(position)

        holder.tvTitle.text = book.title
        holder.tvAuthor.text = book.author

        holder.itemView.setOnClickListener {
            onBookClick(book)
        }
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<BookEntity>() {

                override fun areItemsTheSame(
                    oldItem: BookEntity,
                    newItem: BookEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: BookEntity,
                    newItem: BookEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
