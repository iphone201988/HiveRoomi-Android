package com.tech.hive.ui.for_room_mate.profile

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.GetUserProfileResponse
import com.tech.hive.databinding.FragmentProfileBinding
import com.tech.hive.ui.for_room_mate.settings.SettingsActivity
import com.tech.hive.ui.role.ChangeRoleActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private var userRole = 0
    private var userLanguage = ""

    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // observer
        initObserver()

    }

    override fun onResume() {
        super.onResume()
        // api call for profile
        viewModel.getUserProfile(Constants.USER_ME)
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
                            val intent = Intent(requireContext(), EditProfileSecondTypeActivity::class.java)
                            startActivity(intent)
                        }
                        else -> {
                            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
                        }
                    }
                }
                // change role
                R.id.tvChangeRole, R.id.ivChangeRole -> {
                    val intent = Intent(requireContext(), ChangeRoleActivity::class.java)
                    intent.putExtra("roleType", "role")
                    intent.putExtra("userRole", userRole.toString())
                    startActivity(intent)
                }
                // change language
                R.id.tvChangeLanguage, R.id.ivChangeLanguage -> {
                    val intent = Intent(requireContext(), ChangeRoleActivity::class.java)
                    intent.putExtra("roleType", "language")
                    intent.putExtra("userLanguage", userLanguage)
                    startActivity(intent)
                }
                // change password
                R.id.tvChangePassword, R.id.ivPass -> {
                    startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
                }
            }

        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.profileObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getUserProfile" -> {
                            try {
                                val myDataModel: GetUserProfileResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    sharedPrefManager.saveRole(myDataModel.data.profileRole)
                                    when (myDataModel.data.profileRole) {
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
                                    if (myDataModel.data.instagram?.isEmpty() == true) {
                                        binding.tvInstagram.text = "Not Connected"
                                    } else {
                                        binding.tvInstagram.text = myDataModel.data.instagram
                                    }
                                    if (myDataModel.data.linkedin?.isEmpty() == true) {
                                        binding.tvLinkedin.text = "Not Connected"
                                    } else {
                                        binding.tvLinkedin.text = myDataModel.data.linkedin
                                    }
                                    binding.bean = myDataModel.data
                                    userRole = myDataModel.data.profileRole ?: 0
                                    userLanguage = myDataModel.data.language ?: ""
                                    for (i in myDataModel.data.quizs!!) {
                                        when (i?.title) {
                                            "budget" -> {
                                                binding.tvBudget.text = "${i.answer}"
                                                binding.tvBudget2.text = "${i.answer}"
                                            }

                                            "havePets" -> {
                                                binding.tvPets.text = "${i.answer}"
                                            }

                                            "doISmoke" -> {
                                                binding.tvSmoking.text = "${i.answer}"
                                            }

                                            "guests" -> {
                                                binding.tvOverNight.text = "${i.answer}"
                                            }

                                            "cleaning" -> {
                                                binding.tvClean.text = "${i.answer}"
                                                binding.tvClean2.text = "${i.answer}"

                                            }

                                            "lifestyle" -> {
                                                binding.tvSleep.text = "${i.answer}"
                                            }

                                            "roomType" -> {
                                                binding.tvRoom.text = "${i.answer}"
                                            }
                                        }

                                    }


                                }
                            } catch (e: Exception) {
                                binding.clNested.visibility= View.GONE
                                binding.tvTitle.visibility= View.GONE
                                binding.ivSettings.visibility= View.GONE
                                binding.ivCheck.visibility= View.GONE
                                binding.ivPerson.visibility= View.GONE
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                binding.clNested.visibility= View.VISIBLE
                                binding.tvTitle.visibility= View.VISIBLE
                                binding.ivSettings.visibility= View.VISIBLE
                                binding.ivCheck.visibility= View.VISIBLE
                                binding.ivPerson.visibility= View.VISIBLE
                                hideLoading()
                            }
                        }

                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    binding.clNested.visibility= View.GONE
                    binding.tvTitle.visibility= View.GONE
                    binding.ivSettings.visibility= View.GONE
                    binding.ivCheck.visibility= View.GONE
                    binding.ivPerson.visibility= View.GONE
                    showErrorToast(it.message.toString())
                }

                else -> {

                }
            }
        }
    }

}