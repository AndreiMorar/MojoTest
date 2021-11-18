package com.mab.mojoapp.ui.customviews

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class ObservableScrollView : ScrollView {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    companion object {
        const val SCROLL_THRESHOLD_PERCENT = 0.3
        const val SCROLL_MILLIS = 200L
        const val SCROLL_INCREMENT_PX = 30
    }

    val _moveUpRunnable = Runnable {
        scrollBy(0, -1 * SCROLL_INCREMENT_PX)
        startAutoScrollUp()
    }
    val _moveDownRunnable = Runnable {
        scrollBy(0, SCROLL_INCREMENT_PX)
        startAutoScrollDown()
    }

    val _handler = Handler(Looper.getMainLooper())

    var _listener: IObservableScrollView? = null

    fun setListener(listener: IObservableScrollView) {
        _listener = listener
    }

    private var _dragModeEnabled = false

    fun isInDragMode(dragModeEnabled: Boolean) {
        _dragModeEnabled = dragModeEnabled
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_UP -> {
                println("POSITION :: up")
                if (_dragModeEnabled) {
                    _listener?.onDragEnded(ev.y, scrollY)
                }
                _dragModeEnabled = false
                stopAutoScroll()
                _dragModeEnabled
            }
            MotionEvent.ACTION_MOVE -> {

                if (_dragModeEnabled) {
                    if (ev.y <= height * SCROLL_THRESHOLD_PERCENT) {
                        startAutoScrollUp()
                    } else if (ev.y >= height - height * SCROLL_THRESHOLD_PERCENT) {
                        startAutoScrollDown()
                    } else {
                        stopAutoScroll()
                    }

                    println("POSITION :: move ${ev.y} | ${height * SCROLL_THRESHOLD_PERCENT}")

                    _listener?.onScrolledY(ev.y, scrollY)
                }

                if (_dragModeEnabled) false else super.onTouchEvent(ev)
            }
            MotionEvent.ACTION_DOWN -> {
                println("POSITION :: down ${ev.y}")
                super.onTouchEvent(ev)
            }
            else -> super.onTouchEvent(ev)
        }
    }

    fun startAutoScrollUp() {
        _handler.postDelayed(_moveUpRunnable, SCROLL_MILLIS)
    }

    fun startAutoScrollDown() {
        _handler.postDelayed(_moveDownRunnable, SCROLL_MILLIS)
    }

    fun stopAutoScroll() {
        _handler.removeCallbacks(_moveUpRunnable)
        _handler.removeCallbacks(_moveDownRunnable)
    }

}

interface IObservableScrollView {
    fun onScrolledY(y: Float, scrollY: Int)
    fun onDragEnded(y: Float, scrollY : Int)
}