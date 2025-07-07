package com.tech.hive.ui.for_room_mate.profile

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.data.api.Constants
import com.tech.hive.databinding.FragmentProfileBinding
import com.tech.hive.ui.for_room_mate.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    override fun onCreateView(view: View) {
        // view
        initView()
        // click
        initOnClick()
        // observer
        initObserver()
        when (Constants.userType) {
            1 -> {
                binding.typeCheck = 1

            }

            2 -> {
                binding.typeCheck = 2
            }

            else -> {
                binding.typeCheck = 3
            }
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    /** handle view **/
    private fun initView() {

    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // setting button
                R.id.ivSettings -> {
                    startActivity(Intent(requireContext(), SettingsActivity::class.java))
                }
                // edit profile
                R.id.tvEditProfile, R.id.ivEditProfile -> {
                    when (Constants.userType) {
                        1 -> {
                            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
                        }

                        2 -> {
                            startActivity(
                                Intent(
                                    requireContext(), EditProfileSecondTypeActivity::class.java
                                )
                            )
                        }

                        else -> {
                            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
                        }
                    }
                }
                // change password screen
                R.id.tvChangePassword, R.id.ivPass -> {
                    startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
                }
            }

        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.profileObserver.observe(viewLifecycleOwner) {

        }

    }

}