package codegen

/**
 * Created by nicolasstucki on 10/10/2014.
 */
trait VectorProperties {
    val blockIndexBits = 5

    def blockWidth = 1 << blockIndexBits

    def blockMask = (1 << blockIndexBits) - 1

    val blockInvariants = 1

    val useAssertions = false

    val vectorPointerClassName = TypeName("VectorPointer")

}
