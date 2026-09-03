package io.github.techtastic.hexxysable.sablecompat

import org.joml.Matrix3dc
import org.joml.Matrix4dc

object MatrixConversion {
    fun toRowMajor(matrix: Matrix4dc): DoubleArray = doubleArrayOf(
        matrix.m00(), matrix.m01(), matrix.m02(), matrix.m03(),
        matrix.m10(), matrix.m11(), matrix.m12(), matrix.m13(),
        matrix.m20(), matrix.m21(), matrix.m22(), matrix.m23(),
        matrix.m30(), matrix.m31(), matrix.m32(), matrix.m33(),
    )

    fun toRowMajor(matrix: Matrix3dc): DoubleArray = doubleArrayOf(
        matrix.m00(), matrix.m01(), matrix.m02(),
        matrix.m10(), matrix.m11(), matrix.m12(),
        matrix.m20(), matrix.m21(), matrix.m22(),
    )
}
