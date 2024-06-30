package com.example.plantspot

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.plantspot.ViewModel.AuthViewModel
import com.example.plantspot.activity.UserMainActivity
import com.example.plantspot.databinding.FragmentOTPBinding
import com.example.plantspot.model.Users
import kotlinx.coroutines.launch


class OTPFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding:FragmentOTPBinding
    private lateinit var userNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentOTPBinding.inflate(inflater, container, false)
        getUserNumber()
        customizingEnteringOTP()
        onBackButtonClicked()
        onLoginButtonClicked()
        sendOTP()


        return binding.root
      }

    private fun onLoginButtonClicked() {
          binding.btnLogin.setOnClickListener{
               utils.showDialog(requireContext(), "Signing you...")

              val editTexts = arrayOf(
                  binding.etotp1,
                  binding.etotp2,
                  binding.etotp3,
                  binding.etotp4,
                  binding.etotp5,
                  binding.etotp6
              )

               val otp = editTexts.joinToString(""){
                   it.text.toString()
               }

              if (otp.length < editTexts.size)
              {
                   utils.showToast(requireContext(), "Please enter correct otp")
              }

              else{
                  editTexts.forEach {
                      it.text?.clear()
                      it.clearFocus()
                  }
                  verifyOtp(otp)
              }


          }
    }

    private fun verifyOtp(otp: String) {
//        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        val user = Users(uid = null, userPhoneNumber = userNumber, userAddress = " ")
         viewModel.signInWithPhoneAuthCredential(otp,userNumber, user)


        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect{
                if (it)
                {
                    utils.hideDialog()
                    utils.showToast(requireContext(), "Login Successful")
                    startActivity(Intent(requireActivity(), UserMainActivity::class.java))
                     requireActivity().finish()

                }
            }
        }

    }

    private fun sendOTP() {
         utils.showDialog(requireContext(), "Sending OTP...")
//         viewModel.sendOTP(userNumber, requireActivity())
     viewModel.apply{
         sendOTP(userNumber, requireActivity())
         lifecycleScope.launch{
             otpSent.collect{
                   if (it)
                   {
                       utils.hideDialog()
                       utils.showToast(requireContext(), "OTP Sent...")

                   }
             }
         }

     }


    }

    private fun onBackButtonClicked() {
        binding.tbOtpFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_OTPFragment_to_signinFragment)
        }
    }

    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(
            binding.etotp1,
            binding.etotp2,
            binding.etotp3,
            binding.etotp4,
            binding.etotp5,
            binding.etotp6
        )


        for (i in editTexts.indices)
        {
            editTexts[i].addTextChangedListener (object :TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1)
                    {
                        if(i < editTexts.size -1)
                        {
                            editTexts[i+1].requestFocus()
                        }
                    }
                    else if(s?.length == 0)
                    {
                        if (i > 0)
                        {
                            editTexts[i-1].requestFocus()
                        }
                    }
                }

            } )
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text = userNumber



    }


}