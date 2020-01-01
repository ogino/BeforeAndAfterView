package com.d42gmail.cavar.beforeandafter.custom_view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.d42gmail.cavar.beforeandafter.R
import com.d42gmail.cavar.beforeandafter.utils.convertDpToPix
import java.lang.Exception


class BeforeAndAfterView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : RelativeLayout(context, attrs, defStyleAttr), ClipDrawableAsync.LoadingListener {

    constructor(context: Context?) : this(context, null, INIT_INT, INIT_INT)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, INIT_INT)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, INIT_INT)

    private var beforeSrc: Int? = INIT_INT
    private var afterSrc: Int? = INIT_INT
    private var progress: Int? = DEFAULT_PROGRESS

    private var progressDrawable: Int? = R.drawable.seek_bar_thumb
    private var cornerMaskDrawable: Int? = R.drawable.round_edge_mask
    private var progressPaddingStart: Float? = convertDpToPix(DEFAULT_PROGRESS_PADDING, context!!).toFloat()
    private var progressPaddingEnd: Float? = convertDpToPix(DEFAULT_PROGRESS_PADDING, context!!).toFloat()
    private var placeHolder: Int? = DEFAULT_INT
    private var beforeUrl: String? = null
    private var afterUrl: String? = INIT_STRING
    private var roundCorners: Boolean? = false

    private var ptSeekBar: SeekBar
    private var ivPlaceHolder: ImageView
    private var ptBackgroundImageLeft: ImageView
    private var ptBackgroundImageRight: ImageView
    private var vMask: View

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.before_and_after_layout, this)
        ptSeekBar = view.findViewById(R.id.ptSeekBar)
        ivPlaceHolder = view.findViewById(R.id.ivPlaceHolder)
        ptBackgroundImageLeft = view.findViewById(R.id.ptBackgroundImageLeft)
        ptBackgroundImageRight = view.findViewById(R.id.ptBackgroundImageRight)
        vMask = view.findViewById(R.id.vMask)

        getAttrsValues(context, attrs)
    }

    private fun getAttrsValues(context: Context?, attrs: AttributeSet?) {
        val attributes: TypedArray? = context?.theme?.obtainStyledAttributes(attrs, R.styleable.BeforeAndAfterView, DEFAULT_INT, DEFAULT_INT)
        try {
            beforeSrc = attributes?.getResourceId(R.styleable.BeforeAndAfterView_rightImageSrc, INIT_INT)
            afterSrc = attributes?.getResourceId(R.styleable.BeforeAndAfterView_leftImageSrc, INIT_INT)

            beforeUrl = attributes?.getString(R.styleable.BeforeAndAfterView_rightImageUrl)
            afterUrl = attributes?.getString(R.styleable.BeforeAndAfterView_leftImageUrl)

            roundCorners = attributes?.getBoolean(R.styleable.BeforeAndAfterView_roundCorners, false)
            cornerMaskDrawable = attributes?.getResourceId(R.styleable.BeforeAndAfterView_cornerMask, R.drawable.round_edge_mask)

            progress = attributes?.getInt(R.styleable.BeforeAndAfterView_progress, DEFAULT_PROGRESS)
            progressDrawable = attributes?.getInt(R.styleable.BeforeAndAfterView_progressDrawable, R.drawable.seek_bar_thumb)

            progressPaddingStart = attributes?.getDimension(R.styleable.BeforeAndAfterView_progressPaddingStart, convertDpToPix(DEFAULT_PROGRESS_PADDING, context).toFloat())
            progressPaddingEnd = attributes?.getDimension(R.styleable.BeforeAndAfterView_progressPaddingEnd, convertDpToPix(DEFAULT_PROGRESS_PADDING, context).toFloat())

            placeHolder = attributes?.getResourceId(R.styleable.BeforeAndAfterView_placeHolderSrc, DEFAULT_INT)
        } finally {
            attributes?.recycle()
        }
        applyStyle()
        loadingArbitrar()
    }

    private fun validateProgress(progress: Int): Int {
        if (progress < 0)
            return 0
        if (progress > 100)
            return 10000
        return progress * 100
    }

    private fun applyStyle() {
        setRoundCorners(roundCorners!!)
        setProgress(this.progress!!)
        setProgressThumb(ContextCompat.getDrawable(context, progressDrawable!!)!!)
        setProgressPadding(progressPaddingStart!!.toInt(), 0, progressPaddingEnd!!.toInt(), 0)
        setMask(ContextCompat.getDrawable(context, cornerMaskDrawable!!)!!)
        if (placeHolder != DEFAULT_INT)
            setPlaceHolder(ContextCompat.getDrawable(context, placeHolder!!)!!)
    }

    private fun setProgress(progress: Int) {
        ptSeekBar.progress = validateProgress(progress)
    }

    private fun loadingArbitrar() {
        Handler().post {
            if (beforeSrc != INIT_INT && afterSrc != INIT_INT) {
                loadImagesBySrc(afterSrc!!, beforeSrc!!)
            } else if (beforeUrl != null && afterUrl != null) {
                loadImagesByUrl(afterUrl!!, beforeUrl!!)
            }
        }
    }

    fun setProgressThumb(drawable: Drawable) {
        ptSeekBar.thumb = drawable
    }

    fun setRoundCorners(roundCorners: Boolean) {
        if (roundCorners)
            vMask.visibility = VISIBLE
    }

    fun setProgressPadding(start: Int, top: Int, end: Int, bottom: Int) {
        ptSeekBar.setPadding(start, top, end, bottom)
    }

    fun setMask(drawable: Drawable) {
        vMask.background = drawable
    }

    fun setPlaceHolder(drawable: Drawable) {
        ivPlaceHolder.setImageDrawable(drawable)
    }

    fun loadImagesByUrl(imageLeftUrl: String, imageRightUrl: String) = ClipDrawableAsync<String>(ptBackgroundImageLeft, ptBackgroundImageRight, ptSeekBar, validateProgress(progress!!), context,this).execute(imageLeftUrl, imageRightUrl)

    fun loadImagesBySrc(imageLeftSrc: Int, imageRightSrc: Int) = ClipDrawableAsync<Int>(ptBackgroundImageLeft, ptBackgroundImageRight, ptSeekBar, validateProgress(progress!!), context,this).execute(imageLeftSrc, imageRightSrc)

    fun loadImagesByUrl(imageLeftUrl: String, imageRightUrl: String, progress: Int) {
        setProgress(progress)
        ClipDrawableAsync<String>(ptBackgroundImageLeft, ptBackgroundImageRight, ptSeekBar, validateProgress(progress), context, this).execute(imageLeftUrl, imageRightUrl)
    }

    fun loadImagesBySrc(imageLeftSrc: Int, imageRightSrc: Int, progress: Int) {
        setProgress(progress)
        ClipDrawableAsync<Int>(ptBackgroundImageLeft, ptBackgroundImageRight, ptSeekBar, validateProgress(progress), context, this).execute(imageLeftSrc, imageRightSrc)
    }

    override fun loadingStatus(loadedSuccess: Boolean) {
        Handler().post {
            try {
                if (loadedSuccess) {
                    recalculateNewImageHeight(ptBackgroundImageRight)
                    recalculateNewImageHeight(ptBackgroundImageLeft)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun recalculateNewImageHeight(view: View) {
        view.let {
            view.layoutParams.height = calculateNewImageHeight(view.layoutParams.width)
            ptSeekBar.visibility = View.VISIBLE
            ivPlaceHolder.visibility = View.GONE
        }
    }

    private fun calculateNewImageHeight(width: Int): Int = (width / 0.6875).toInt()
}