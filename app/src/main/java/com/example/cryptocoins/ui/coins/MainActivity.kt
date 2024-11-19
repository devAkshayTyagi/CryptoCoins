package com.example.cryptocoins.ui.coins

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptocoins.R
import com.example.cryptocoins.data.model.Coin
import com.example.cryptocoins.databinding.ActivityMainBinding
import com.example.cryptocoins.ui.UiState
import com.example.cryptocoins.utils.getQueryTextChangeStateFlow
import com.example.cryptocoins.utils.hideView
import com.example.cryptocoins.utils.showToast
import com.example.cryptocoins.utils.showView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var coinViewModel: CoinViewModel

    @Inject
    lateinit var coinsAdapter: CoinsAdapter

    private lateinit var binding: ActivityMainBinding

    companion object {
        fun getStartingIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        coinViewModel = ViewModelProvider(this)[CoinViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpUi()
        collectFlows()
        initClickListeners()
        coinViewModel.fetchCoins()
        coinViewModel.setUpSearchStateFlow(binding.searchView.getQueryTextChangeStateFlow())

    }

    private fun initClickListeners() {
        binding.filterChipGroup.forEach { child ->
            (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
                registerFilterChanged()
            }
        }
    }

    private fun setUpUi() {
        binding.rvCoins.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
            adapter = coinsAdapter
        }
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                coinViewModel.uiStateCoins.collect {
                    when (it) {
                        is UiState.Loading -> {
                            binding.progressCircular.showView()
                            binding.rvCoins.hideView()
                            binding.searchView.hideView()
                            binding.filterChipGroup.hideView()
                        }

                        is UiState.Success -> {
                            binding.progressCircular.hideView()
                            binding.searchView.showView()
                            binding.filterChipGroup.showView()
                            if (it.data.isEmpty() && binding.searchView.query.isEmpty().not()){
                                showEmptyState()
                            }else{
                                hideEmptyState()
                                renderList(it.data)
                            }
                        }

                        is UiState.Error -> {
                            binding.progressCircular.hideView()
                            this@MainActivity.showToast(it.message)
                            Log.e("Error",it.message)
                        }
                    }
                }
            }
        }
    }

    private fun registerFilterChanged() {
        val ids = binding.filterChipGroup.checkedChipIds
        val titles = mutableListOf<String>()

        ids.forEach { id ->
            titles.add(binding.filterChipGroup.findViewById<Chip>(id).tag.toString())
        }

        coinViewModel.fetchFilteredCoins(titles.toList())
    }

    private fun showEmptyState() {
        with(binding){
            tvEmptyTitle.showView()
            tvEmptyDesc.showView()
            rvCoins.hideView()
        }
    }

    private fun hideEmptyState() {
        with(binding){
            tvEmptyTitle.hideView()
            tvEmptyDesc.hideView()
            rvCoins.showView()
        }
    }

    private fun renderList(articleList: List<Coin>) {
        coinsAdapter.addData(articleList)
        coinsAdapter.notifyDataSetChanged()
    }

}