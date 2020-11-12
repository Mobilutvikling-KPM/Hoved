package RecyclerView.RecyclerView

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 *
 * @author Patrick S. Lorentzen - 151685
 * @author Mikael Wenneck Rønnevik - 226804
 *
 * Denne klassen lager spacing mellom hvert recyclerview objekt
 * @property padding padding som skal brukes i recyclerview
 */
class BottomSpacingItemDecoration(private val padding: Int): RecyclerView.ItemDecoration(

) {
    /**
     * Send inn info om recylcerview og endre offsets
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
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