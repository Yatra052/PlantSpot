package com.example.plantspot

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.plantspot.ViewModel.AuthViewModel
import com.example.plantspot.activity.UserMainActivity
import com.example.plantspot.databinding.FragmentSplashBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(inflater, container, false)


        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
             viewModel.isACurrentUser.collect{
              if (it)
              {
                  startActivity(Intent(requireActivity(), UserMainActivity::class.java))
                  requireActivity().finish()

              }
                 else
              {
                  findNavController().navigate(R.id.action_splashFragment_to_signinFragment)
              }
             }
            }
        }, 2000)


        return binding.root
    }

        private fun setStatusBarColor(){
            activity?.window?.apply {
                val statusBarColors = ContextCompat.getColor(requireContext(), R.color.green)
                statusBarColor = statusBarColors

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }


            }




    }



}