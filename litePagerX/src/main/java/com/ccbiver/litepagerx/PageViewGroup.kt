package com.ccbiver.litepagerx

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.ccbiver.litepagerx.databinding.LayoutPageBinding


class PageViewGroup : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    lateinit var binding: LayoutPageBinding
    lateinit var mFragment: Fragment
    private var mReflectionSize = 20

    private fun init(context: Context, attrs: AttributeSet?) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_page,
            this,
            true
        )
        LiveDataBus.with(LiveDataBusConstants.UPDATE, String::class.java)
            .observe(context as AppCompatActivity, Observer {
                if (it == mFragment.tag) {
                    binding.layout.drawToBitmap().let {
                        var load =
                            Glide.with(context).load(createReflectedImage(it, mReflectionSize))
                        load.into(binding.reflection)
                    }
                }
            })
    }

    //reflectionSize=倒影高度 radius=圆角
    fun showFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        reflectionSize: Int = 20
    ) {
        mFragment = fragment
        mReflectionSize = reflectionSize
        binding.layout.id = (10000..99999).random()
        val constraintSet = ConstraintSet()
        fragmentManager.beginTransaction()
            .add(binding.layout.id, fragment, fragment::class.java.simpleName).commit()

        constraintSet.connect(
            binding.reflection.id,
            ConstraintSet.TOP,
            binding.layout.id,
            ConstraintSet.BOTTOM
        )
        constraintSet.connect(
            binding.reflection.id,
            ConstraintSet.LEFT,
            binding.layout.id,
            ConstraintSet.LEFT
        )
        constraintSet.connect(
            binding.reflection.id,
            ConstraintSet.RIGHT,
            binding.layout.id,
            ConstraintSet.RIGHT
        )
        constraintSet.setMargin(binding.reflection.id, 3, 10)
        constraintSet.constrainHeight(
            binding.reflection.id,
            UnitUtils.dp2px(context, reflectionSize)
        )
        constraintSet.applyTo(binding.clRoot)

        post {
            binding.layout.drawToBitmap().let {
                var load = Glide.with(context).load(createReflectedImage(it, reflectionSize))
                load.into(binding.reflection)
            }
        }
    }

    //倒影
    private fun createReflectedImage(originalImage: Bitmap, size: Int = 20): Bitmap {
        // The gap we want between the reflection and the original image
        val reflectionGap = 4
        val width = originalImage.width
        val height = originalImage.height

        // This will not scale but will flip on the Y axis
        val matrix = Matrix()
        matrix.preScale(1f, -1f)
        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        val reflectionImage = Bitmap.createBitmap(
            originalImage, 0, height / 2, width,
            height / 2, matrix, false
        )

        // Create a new bitmap with same width but taller to fit reflection
        val bitmapWithReflection = Bitmap.createBitmap(
            width, height + height / 2,
            Bitmap.Config.ARGB_8888
        )

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        val canvas = Canvas(bitmapWithReflection)
        // Draw in the original image
//        canvas.drawBitmap(originalImage, 0f, 0f, null)
        // Draw in the gap
        val defaultPaint = Paint()
        canvas.drawRect(
            0f,
            height.toFloat(),
            width.toFloat(),
            height.toFloat() + reflectionGap,
            defaultPaint
        )
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0f, 0f, null)

        // Create a shader that is a linear gradient that covers the reflection
        val paint = Paint()
        val shader = LinearGradient(
            0f, 0f, 0f,
            UnitUtils.dp2px(context, size).toFloat(), 0x70ffffff, 0x00ffffff,
            TileMode.CLAMP
        )
        // Set the paint to use this shader (linear gradient)
        paint.shader = shader
        // Set the Transfer mode to be porter duff and destination in
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0f, 0f, width.toFloat(), UnitUtils.dp2px(context, size).toFloat(), paint)
        return bitmapWithReflection
    }


}
