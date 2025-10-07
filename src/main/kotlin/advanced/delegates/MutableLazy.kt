package advanced.delegates.mutablelazy

import org.junit.Assert
import org.junit.Test
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.system.measureTimeMillis

fun <T> mutableLazy(
    initializer: () -> T
): ReadWriteProperty<Any?, T> = MutableLazy(initializer)

class MutableLazy<T>(private var initializer: (() -> T)? = null): ReadWriteProperty<Any?, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if(initializer != null) {
            value = initializer?.invoke()
            initializer = null
        }

        return value as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        initializer = null
        this.value = value
    }
}

class MutableLazyTest {

    @Test
    fun `Do not initialize if initialized`() {
        val time = measureTimeMillis {
            var game: Game? by mutableLazy { readGameFromSave() }
            game = Game()
            print(game)
        }
        assert(time in 0..100)
    }

    @Test
    fun `Initializes if not initialized`() {
        val time = measureTimeMillis {
            val game: Game? by mutableLazy { readGameFromSave() }
            print(game)
        }
        assert(time in 450..550)
    }

    @Test
    fun `Do not initialize again if already initialized`() {
        val time = measureTimeMillis {
            val game: Game? by mutableLazy { readGameFromSave() }
            print(game)
            print(game)
            print(game)
        }
        assert(time in 450..550)
    }

    @Test
    fun `MutableLazy should accept nullable values`() {
        val lazy by mutableLazy<String?> { null }
        Assert.assertNull(lazy)

        var lazy2 by mutableLazy<String?> { "A" }
        lazy2 = null
        Assert.assertNull(lazy2)
    }

    private class Game()

    private fun readGameFromSave(): Game? {
        Thread.sleep(500)
        return Game()
    }
}
