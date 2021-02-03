package com.ccBiver.litepagerxdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ccbiver.litepagerx.LiveDataBus
import com.ccbiver.litepagerx.LiveDataBusConstants

import kotlinx.android.synthetic.main.fragment_test.*


/**
 * com.test.cardtest
 */
class TestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv.setOnClickListener {
            Toast.makeText(activity,"点击了",Toast.LENGTH_LONG).show()
            LiveDataBus.with(LiveDataBusConstants.UPDATE).value=tag
        }
    }
}