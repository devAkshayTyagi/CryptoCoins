package com.example.cryptocoins.ui.coins

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptocoins.data.local.entity.Coin
import com.example.cryptocoins.databinding.ItemCoinBinding
import com.example.cryptocoins.utils.invisible
import com.example.cryptocoins.utils.showView

class CoinsAdapter(private val coinList: ArrayList<Coin>) :
    RecyclerView.Adapter<CoinsAdapter.CoinViewHolder>() {
    class CoinViewHolder(private val binding: ItemCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            coin: Coin
        ) {
            with(binding){
                itemView.setBackgroundColor(coin.itemBackGroundColor)
                tvName.text = coin.name
                tvSymbol.text = coin.symbol

                ivCoin.setImageResource(coin.icon)

                if (coin.isNew) ivNew.showView()
                else ivNew.invisible()
            }
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
        coinList.clear()
        coinList.addAll(list)
    }
}