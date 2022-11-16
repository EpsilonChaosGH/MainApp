package com.example.mainapp.screens.main.tabs.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mainapp.R
import com.example.mainapp.databinding.ItemElementBinding
import com.example.mainapp.model.elements.entities.Element
import com.example.mainapp.model.elements.entities.ElementListItem

interface ElementActionListener {
    fun delete(id: Long)
    fun editElementText(id: Long, text: String)
}

class ElementsAdapter(
    private val elementActionListener: ElementActionListener
) : RecyclerView.Adapter<ElementsAdapter.ElementsViewHolder>(), View.OnClickListener {
    class ElementsViewHolder(
        val binding: ItemElementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }

    var elements = listOf<ElementListItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onClick(view: View) {
        val element = view.tag as Element

        when (view.id) {
            R.id.deleteButton -> elementActionListener.delete(element.id)
            R.id.elementTextView -> elementActionListener.editElementText(element.id, element.text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemElementBinding.inflate(inflater, parent, false)

        binding.deleteButton.setOnClickListener(this)
        binding.elementTextView.setOnClickListener(this)

        return ElementsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ElementsViewHolder, position: Int) {
        val element = elements[position]

        holder.binding.deleteButton.tag = element.element
        holder.binding.elementTextView.tag = element.element

        if (element.isInProgress) {
            holder.binding.deleteButton.visibility = View.INVISIBLE
            holder.binding.progressBar.visibility = View.VISIBLE
            holder.binding.root.setOnClickListener(null)
        } else {
            holder.binding.deleteButton.visibility = View.VISIBLE
            holder.binding.progressBar.visibility = View.GONE
            holder.binding.root.setOnClickListener(this)
        }

        holder.binding.elementTextView.text = element.element.text
    }

    override fun getItemCount(): Int = elements.size
}