package com.tech.hive.ui.auth.personal_information.provider

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.Answer
import com.tech.hive.databinding.FragmentProviderTypeBinding
import com.tech.hive.databinding.RvAnswerItemBinding
import com.tech.hive.databinding.RvTypeProviderItemBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProviderTypeFragment : BaseFragment<FragmentProviderTypeBinding>() {
    private val viewModel: ProviderTypeFragmentVM by viewModels()
    private lateinit var providerTypeAdapter: SimpleRecyclerViewAdapter<Answer, RvTypeProviderItemBinding>
    private var selectType = ""

    override fun getLayoutResource(): Int {
        return R.layout.fragment_provider_type
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initClick()
        // adapter
        initAdapter()
    }

    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // btnContinue button click
                R.id.btnContinue -> {
                    if (selectType.isNotEmpty()) {
                        val bundle = Bundle().apply {
                            putString("selected_type", selectType)
                        }

                        BindingUtils.navigateWithSlide(
                            findNavController(),
                            R.id.navigateToPersonalFragment,
                            bundle
                        )
                    } else {
                        showInfoToast("Please select type")
                    }

                }

            }
        }

    }


    /** handle adapter **/
    private fun initAdapter() {
        providerTypeAdapter = SimpleRecyclerViewAdapter(R.layout.rv_type_provider_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clProviderType -> {
                    selectType =  m.answer
                    providerTypeAdapter.list.forEachIndexed { index, answer ->
                        answer.selectedAnswer = index == pos
                    }
                    providerTypeAdapter.notifyDataSetChanged()
                }
            }
        }
        providerTypeAdapter.list = getAnswerList()
        binding.rvProviderType.adapter = providerTypeAdapter

    }


    // question List
    private fun getAnswerList(): ArrayList<Answer> {
        val list = ArrayList<Answer>()
        list.add(Answer(1, "Landlord"))
        list.add(Answer(1, "Tenant Subletting"))
        list.add(Answer(1, "Real Estate Agency"))
        list.add(Answer(1, "Property Owner"))
        list.add(Answer(1, "Current Tenant Leaving The Room"))
        return list
    }

}