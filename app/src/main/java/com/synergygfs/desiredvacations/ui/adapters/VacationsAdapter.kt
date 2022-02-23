package com.synergygfs.desiredvacations.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.databinding.ItemVacationBinding
import java.util.*

interface ItemViewListeners {
    fun onLongClick(vacationId: Int)
}

class VacationsAdapter(
    private var vacationsCollection: Vector<Vacation>,
    private val itemViewListeners: ItemViewListeners
) :
    RecyclerView.Adapter<VacationsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemVacationBinding =
            ItemVacationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val vacation = vacationsCollection[position]

        holder.binding?.image?.let {
            Glide.with(holder.itemView.context)
                .load("${holder.itemView.context.cacheDir.path}/vacations_images/${vacation.imageName}")
                .centerCrop().placeholder(
                    R.color.gray
                ).into(it)
        }

        holder.binding?.name?.text = vacation.name
        holder.binding?.location?.text = vacation.location

        holder.itemView.setOnLongClickListener {
            itemViewListeners.onLongClick(vacation.id)
            true
        }
    }

    class MyViewHolder(binding: ItemVacationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: ItemVacationBinding? = binding
    }

    override fun getItemCount() = vacationsCollection.size
}

