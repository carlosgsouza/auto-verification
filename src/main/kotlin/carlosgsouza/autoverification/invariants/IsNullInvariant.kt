package carlosgsouza.autoverification.invariants

data class IsNullInvariant(val fieldName: String, var isAlwaysNull: Boolean = true, var isNeverNull: Boolean = true) {
    internal fun merge(other: IsNullInvariant) {
        this.isAlwaysNull = this.isAlwaysNull && other.isAlwaysNull
        this.isNeverNull = this.isNeverNull && other.isNeverNull
    }

    override fun toString(): String {
        if(isAlwaysNull && !isNeverNull) return "$fieldName == null"
        if(!isAlwaysNull && isNeverNull) return "$fieldName != null"
        else return super.toString()
    }
}

class IsNullInvariantHandler<DATA>(fieldName: String) : InvariantHandler<IsNullInvariant, DATA>(IsNullInvariant(fieldName)) {

    override var isInitialized = false
    override var isObsolete = isInitialized && invariant.isAlwaysNull && invariant.isNeverNull

    override fun mine(data: DATA) {
        isInitialized = true

        invariant.isAlwaysNull = invariant.isAlwaysNull && (data == null)
        invariant.isNeverNull = invariant.isNeverNull && (data != null)
    }

    override fun merge(other: InvariantHandler<IsNullInvariant, DATA>) {
        if(other.isObsolete) return
        if(!other.isInitialized) return

        this.invariant.merge(other.invariant)
    }

    override fun verify(data: DATA): Boolean {
        if(isObsolete) throw IllegalStateException("Cannot verify an obsolete invariant")
        if(!isInitialized) throw IllegalStateException("Cannot verify an uninitialized invariant")

        if(invariant.isAlwaysNull) return (data == null) else return (data != null)
    }
}