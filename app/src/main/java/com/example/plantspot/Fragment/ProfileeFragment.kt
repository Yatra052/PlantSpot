package com.example.plantspot.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.plantspot.R
import com.example.plantspot.databinding.FragmentProfileeBinding
import kotlinx.coroutines.processNextEventInCurrentThread


class ProfileeFragment : Fragment(){
    private lateinit var binding :FragmentProfileeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileeBinding.inflate(layoutInflater)
        onbackbuttonclicked()
        onOrdersLayoutClicked()
        return (binding.root)

    }

    private fun onOrdersLayoutClicked() {
        findNavController().navigate(R.id.action_orderFragment_to_profileeFragment)
    }

    private fun onbackbuttonclicked() {
        binding.tbProfileFragment.setOnClickListener{
            findNavController().navigate(R.id.action_profileeFragment_to_orderFragment)
        }
    }


}