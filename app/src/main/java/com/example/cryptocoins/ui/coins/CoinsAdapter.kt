package com.example.cryptocoins.ui.coins

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptocoins.data.model.Coin
import com.example.cryptocoins.databinding.ItemCoinBinding

class CoinsAdapter(private val coinList: ArrayList<Coin>) :
    RecyclerView.Adapter<CoinsAdapter.CoinViewHolder>() {
    class CoinViewHolder(private val binding: ItemCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            coin: Coin
        ) {
            binding.tvName.text = coin.name
            binding.tvSymbol.text = coin.symbol

            /*Glide.with(binding.iv.context)
                .load(imageUrl)
                .placeholder(android.R.color.background_dark)
                .into(binding.iv)*/
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder =
        CoinViewHolder(
            ItemCoinBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        holder.bind(coinList[position])
    }

    override fun getItemCount(): Int = coinList.size

    fun addData(list: List<Coin>) {
        coinList.addAll(list)
    }


}