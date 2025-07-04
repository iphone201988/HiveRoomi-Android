package com.tech.hive.ui.for_room_mate.home.second

import android.view.View
import androidx.fragment.app.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.databinding.FragmentFirstPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstPageFragment : BaseFragment<FragmentFirstPageBinding>() {
    private val viewModel: FirstPageFragmentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_first_page
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        val imageList: ArrayList<Int> =
            arguments?.getIntegerArrayList("imageList") as ArrayList<Int>
        val position = arguments?.getInt("position", -1)

        if (position != null && position >= 0 && position < imageList.size) {
            binding.img.setImageResource(imageList[position])
        }
    }


}