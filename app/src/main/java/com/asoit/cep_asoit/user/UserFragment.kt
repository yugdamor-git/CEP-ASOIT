package com.asoit.cep_asoit.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.asoit.cep_asoit.R
import com.asoit.cep_asoit.utils.Preference
import kotlinx.android.synthetic.main.user_fragment.*

class UserFragment : Fragment() {

    companion object {
        fun newInstance() = UserFragment()
    }


    lateinit var preference: Preference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        preference = Preference(this.requireContext())

        tv_institute.text = preference.getInstitute()
        tv_branch.text = preference.getBranch()
        tv_sem.text = preference.getSem()
        tv_devision.text = preference.getDivision()
        tv_user_name.text = preference.getName()
    }

}
