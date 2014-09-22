package scala.collection.immutable.vectorbenchmarks


import java.io._

private[vectorbenchmarks] object RandomDataSet {

    private final val DOUBLES_FILES = "src/test/resources/pseudo-random-doubles"
    private final val RANDOM_SET_SIZE = 2 << 14

    def getDoubles(n: Int) = {
        for (i <- 0 until n) yield doubles(i % RANDOM_SET_SIZE)
    }

    private lazy val doubles: Array[Double] = {
        if (!new File(DOUBLES_FILES).exists())
            genDoublesFile()
        val arr = new Array[Double](RANDOM_SET_SIZE)
        var reader: DataInputStream = null
        try {
            reader = new DataInputStream(new FileInputStream(DOUBLES_FILES))
            for (i <- 0 until RANDOM_SET_SIZE)
                arr(i) = reader.readDouble()
        } catch {
            case _ =>
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch {
                    case _ =>
                }
            }
        }
        arr
    }

    private def genDoublesFile() = {
        var writer: DataOutputStream = null
        try {
            writer = new DataOutputStream(new FileOutputStream(DOUBLES_FILES))
            try {
                for (i <- 0 until RANDOM_SET_SIZE)
                    writer.writeDouble(Math.random())
                writer.close()
            } catch {
                case _ =>
            } finally {
                if (writer != null) {
                    try {
                        writer.close()
                    } catch {
                        case _ =>
                    }
                }
            }
        }
    }
}