package carlosgsouza.autoverification.invariants

/**
 * An InvariantHandler implements all the logic to mine invariants and verify them. Subclasses must implement the
 * methods {@code mineImpl}, {@code mergeImpl} and {@code verifyImpl}
 */
abstract class InvariantHandler<INVARIANT, DATA>(var invariant: INVARIANT) {

    /**
     * The handler has already mined at least one record or been merged with an initialized invariant.
     */
    open var isInitialized: Boolean = false

    /**
     * It is no longer possible to mine an invariant.
     */
    open var isObsolete: Boolean = false

    /**
     * The handler has already mined everything that it could from existing data and no longer need to process any
     * records.
     */
    open var isComplete: Boolean = false

    /**
     * Mines an invariant from a data record.
     */
    abstract fun mine(data: DATA)

    /**
     * Merges another {@code invariant} into this invariant.
     */
    abstract fun merge(other: InvariantHandler<INVARIANT, DATA>)

    /**
     * Verify that the record conforms to this invariant.
     */
    abstract fun verify(data: DATA): Boolean


}