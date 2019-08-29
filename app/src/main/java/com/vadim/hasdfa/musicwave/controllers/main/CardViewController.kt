package com.vadim.hasdfa.musicwave.controllers.main

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.vadim.hasdfa.musicwave.R
import com.vadim.hasdfa.musicwave.base.IViewController
import com.vadim.hasdfa.musicwave.view.transformers.CarouselEffectTransformer

class CardViewController(
    private val mViewPager: ViewPager,
    fm: FragmentManager?,
    private val source: List<Int>
): IViewController(mViewPager) {

    private val mTransformer: ViewPager.PageTransformer by lazy {
        CarouselEffectTransformer(context)
    }
    private val mAdapter: PagerAdapter by lazy {
        ViewPagerAdapter(fm)
    }

    fun initialize() {
        mViewPager.setPageTransformer(false, mTransformer)
        mViewPager.adapter = mAdapter
    }

    inner class ViewPagerAdapter(fm: FragmentManager?): FragmentPagerAdapter(fm) {

        override fun getItem(position: Int) = PageFragment().apply {
            arguments = Bundle().apply {
                putInt("drawable", source[position])
            }
        }

        override fun getCount() = source.size
    }

    public class PageFragment: Fragment() {

        private val drawableRes: Int
            get() = arguments?.getInt("drawable") ?: 0

        private val imageDrawable: Drawable?
            get() = ContextCompat.getDrawable(context!!, drawableRes)

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(
                R.layout.item_card, container, false
            )
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            view.findViewById<AppCompatImageView>(R.id.mImageView)
                .setImageDrawable(imageDrawable)
        }
    }

}