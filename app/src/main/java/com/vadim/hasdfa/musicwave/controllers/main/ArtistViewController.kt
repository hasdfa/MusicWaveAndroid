package com.vadim.hasdfa.musicwave.controllers.main

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
import com.vadim.hasdfa.musicwave.R
import com.vadim.hasdfa.musicwave.base.IViewController
import com.vadim.hasdfa.musicwave.models.Artist
import com.vadim.hasdfa.musicwave.models.TopArtists
import com.vadim.hasdfa.musicwave.repositories.NetworkRepository
import kotlinx.android.synthetic.main.fragment_artists.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ArtistViewController(
    activity: AppCompatActivity,
    @LayoutRes res: Int
) : IViewController(activity, res) {

    private var onLoadComplete: (() -> Unit)? = null
    private var onChoseListener: ((Artist) -> Unit)? = null

    private val mRecyclerView by lazy { view.findViewById<RecyclerView>(R.id.mRecyclerView) }
    private val mViewPager by lazy { view.findViewById<ViewPager>(R.id.mViewPager) }

    private val cardViewController: CardViewController by lazy {
        CardViewController(
            mViewPager, appCompatActivity?.supportFragmentManager,
            listOf(
                R.drawable.card_1,
                R.drawable.card_2,
                R.drawable.card_3
            )
        )
    }


    private val mAdapter: InnerAdapter by lazy {
        InnerAdapter()
    }

    init {
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        cardViewController.initialize()

        GlobalScope.launch {
            val top = NetworkRepository.getTopAuthors()
            mAdapter.mSource.artists = top.artists
            MainScope().launch {
                mAdapter.notifyDataSetChanged()
                onLoadComplete?.invoke()
            }
        }
    }

    fun setOnLoadCompleteListener(l: (() -> Unit)?) {
        onLoadComplete = l

        if (mAdapter.mSource.artists.artist.isNotEmpty())
            onLoadComplete?.invoke()
    }

    fun setOnChoseListener(l: ((Artist) -> Unit)?) {
        onChoseListener = l
    }

    inner class InnerAdapter: RecyclerView.Adapter<ViewHolder>() {
        val mSource = TopArtists()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_artist, parent, false
                )
            )
        }

        override fun getItemCount(): Int
                = mSource.artists.artist.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = mSource.artists.artist[position]
            holder.apply {
                val imageUrl = item.image.first().url
                Picasso.get()
                    .load(imageUrl)
                    .into(imageView)

                nameText.text = item.name
                playcountText.text = item.playcountValue
                listenersText.text = item.listenersValue

                itemView.setOnClickListener {
                    onChoseListener?.invoke(item)
                }
            }
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val nameText: TextView = view.findViewById(R.id.name)

        val playcountText: TextView = view.findViewById(R.id.playcountText)
        val listenersText: TextView = view.findViewById(R.id.listenersText)
    }
}