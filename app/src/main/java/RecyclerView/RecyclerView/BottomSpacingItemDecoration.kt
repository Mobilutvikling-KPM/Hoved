package RecyclerView.RecyclerView

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

//Denne klassen lager spacing mellom hvert recyclerview objekt
class BottomSpacingItemDecoration(private val padding: Int): RecyclerView.ItemDecoration(

) {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = padding
    }

}