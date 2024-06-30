package com.example.plantspot.Fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.example.plantspot.Adapter.AdapterCategory
import com.example.plantspot.Constants
import com.example.plantspot.R
import com.example.plantspot.ViewModel.UserViewModel
import com.example.plantspot.databinding.FragmentHomeBinding
import com.example.plantspot.model.Category

class HomeFragment : Fragment() {

   private lateinit var binding: FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setStatusBarColor()
        setAllCategoryList()
        navigatingToSearchFragment()
        onProfileClicked()
//        get()

        return binding.root

    }

    private fun onProfileClicked() {
        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileeFragment)

        }
    }

    //    private fun get()
//    {
//         viewModel.getAll().observe(viewLifecycleOwner){
//             for (i in it)
//             {
//                 Log.d("vvv", i.productTitle.toString())
//                 Log.d("vvv", i.productCount.toString())
//             }
//         }
//    }
    private fun navigatingToSearchFragment() {
       binding.searchCv.setOnClickListener{
         findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
       }
    }

    private fun setAllCategoryList() {
        val categoryList = ArrayList<Category>()

        for (i in 0 until Constants.allProductsCategory.size)
        {
            categoryList.add(Category(Constants.allProductsCategory[i], Constants.allproductCategoryIcon[i]))
        }

        binding.rvCategories.adapter = AdapterCategory(categoryList, ::onCategoryIconClicked)
    }


    fun onCategoryIconClicked(category: Category)
    {
//           getCategoryProduct(category.title)
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoriesFragment, bundle)
    }

//    private fun getCategoryProduct(title: String?) {
//
//    }

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
