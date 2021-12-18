package carlosgsouza.autoverification.invariants

import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class IsNullInvariantHandlerTest {

    @Test
    fun testInitialization() {
        val handler = IsNullInvariantHandler<String?>("field")
        assertThat(handler.isInitialized, equalTo(false))
        assertThat(handler.isObsolete, equalTo(false))
        assertThat(handler.isComplete, equalTo(false))
    }

    @Test
    fun testMining_isAlwaysNull() {
        val handler = IsNullInvariantHandler<String?>("field")

        assertThat(handler.isInitialized, equalTo(false))
        assertThat(handler.isObsolete, equalTo(false))
        assertThat(handler.isComplete, equalTo(false))

        handler.mine(null)

        assertThat(handler.invariant.isAlwaysNull, equalTo(true))
        assertThat(handler.invariant.isNeverNull, equalTo(false))
        assertThat(handler.isInitialized, equalTo(true))
        assertThat(handler.isObsolete, equalTo(false))
        assertThat(handler.isComplete, equalTo(false))

    }

    @Test
    fun testMining_isNeverNull() {
        val handler = IsNullInvariantHandler<String?>("field")
        handler.mine("")

        assertThat(handler.invariant.isNeverNull, equalTo(true))
        assertThat(handler.invariant.isAlwaysNull, equalTo(false))
        assertThat(handler.isInitialized, equalTo(true))
        assertThat(handler.isObsolete, equalTo(false))
        assertThat(handler.isComplete, equalTo(false))
    }

    @Test
    fun testIsObsolete() {
        val handler = IsNullInvariantHandler<String?>("field")
        handler.mine("")
        handler.mine(null)

        assertThat(handler.isObsolete, equalTo(false))
        assertThat(handler.invariant.isNeverNull, equalTo(false))
        assertThat(handler.invariant.isAlwaysNull, equalTo(false))
        assertThat(handler.isInitialized, equalTo(true))
        assertThat(handler.isComplete, equalTo(false))
    }

    @Test
    fun testVerify() {
        val handler_isAlwaysNull = IsNullInvariantHandler<String?>("field")
        handler_isAlwaysNull.mine(null)

        assertThat(handler_isAlwaysNull.verify(null), equalTo(true))
        assertThat(handler_isAlwaysNull.verify(""), equalTo(false))

        val handler_isNeverNull = IsNullInvariantHandler<String?>("field")
        handler_isNeverNull.mine("")

        assertThat(handler_isNeverNull.verify(null), equalTo(false))
        assertThat(handler_isNeverNull.verify(""), equalTo(true))
    }

    @Test
    fun testMerge() {
        val alwaysNull = IsNullInvariant("field", isAlwaysNull = true, isNeverNull = false)
        val neverNull = IsNullInvariant("field", isAlwaysNull = false, isNeverNull = true)
        val obsolete = IsNullInvariant("field", isAlwaysNull = false, isNeverNull = false)
        val uninitialized = IsNullInvariant("field", isAlwaysNull = true, isNeverNull = true)

        // Merging equal invariants should result in the same invariant
        testMerge(alwaysNull, alwaysNull, alwaysNull)
        testMerge(neverNull, neverNull, neverNull)
        testMerge(obsolete, obsolete, obsolete)
        testMerge(uninitialized, uninitialized, uninitialized)

        // Merging two opposite invariants should result in obsolete invariants
        testMerge(alwaysNull, neverNull, obsolete)
        testMerge(neverNull, alwaysNull, obsolete)

        // Merging with obsolete should result in obsolete
        testMerge(obsolete, alwaysNull, obsolete)
        testMerge(obsolete, neverNull, obsolete)

        testMerge(alwaysNull, obsolete, obsolete)
        testMerge(neverNull, obsolete, obsolete)

        testMerge(uninitialized, obsolete, obsolete)
        testMerge(obsolete, uninitialized, obsolete)

        // Merging with uninitialized should result in itself
        testMerge(alwaysNull, uninitialized, alwaysNull)
        testMerge(uninitialized, alwaysNull, alwaysNull)

        testMerge(neverNull, uninitialized, neverNull)
        testMerge(uninitialized, neverNull, neverNull)

        testMerge(uninitialized, obsolete, obsolete)
        testMerge(obsolete, uninitialized, obsolete)
    }

    private fun testMerge(i1: IsNullInvariant, i2: IsNullInvariant, expected: IsNullInvariant) {
        val result = i1.merge(i2)
        assertThat(i1, equalTo(expected))
    }
}
