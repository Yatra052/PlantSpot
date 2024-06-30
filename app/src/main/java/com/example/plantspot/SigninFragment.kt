package com.example.plantspot

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.plantspot.databinding.FragmentSigninBinding


class SigninFragment : Fragment() {
    private lateinit var binding: FragmentSigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSigninBinding.inflate(inflater, container, false)
        setStatusBarColor()
        getUserNumber()
        onContinueButtonClick()


        return binding.root
    }

    private fun onContinueButtonClick() {
       binding.btnContinue.setOnClickListener {
           val number = binding.etUserNumber.text.toString()

           if (number.isEmpty() || number.length != 10)
           {
               utils.showToast(requireContext(), "Please enter valid number")
           }
           else{
               val bundle = Bundle()
               bundle.putString("number", number)
               findNavController().navigate(R.id.action_signinFragment_to_OTPFragment, bundle)
           }
       }
    }

    private fun getUserNumber() {
       binding.etUserNumber.addTextChangedListener(object : TextWatcher {
           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

           }

           override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                val len = number?.length

               if (len == 10)
               {
                   binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
               }
               else{
                   binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
               }
           }

           override fun afterTextChanged(s: Editable?) {

           }

       })
    }


    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.green)
            statusBarColor = statusBarColors

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}



