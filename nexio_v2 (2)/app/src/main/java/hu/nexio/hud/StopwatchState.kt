package hu.nexio.hud

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Saját időmérő (tizedmásodperc pontosság). Folyamaton belül él, a HUD ezt jeleníti meg. */
object StopwatchState {
    var running by mutableStateOf(false); private set
    var elapsedMs by mutableLongStateOf(0L); private set

    private var base = 0L
    private var startedAt = 0L

    fun toggle() {
        if (running) {
            base += SystemClock.elapsedRealtime() - startedAt
            elapsedMs = base
            running = false
        } else {
            startedAt = SystemClock.elapsedRealtime()
            running = true
        }
    }

    fun reset() { running = false; base = 0L; elapsedMs = 0L }

    fun tick() { if (running) elapsedMs = base + (SystemClock.elapsedRealtime() - startedAt) }

    fun format(): String {
        val t = elapsedMs / 100
        val tenths = t % 10
        val sec = (t / 10) % 60
        val min = t / 600
        return "%02d:%02d.%d".format(min, sec, tenths)
    }
}
