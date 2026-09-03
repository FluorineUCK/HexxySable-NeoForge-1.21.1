package io.github.techtastic.hexxysable.sablecompat

import org.joml.Matrix3d
import org.joml.Matrix4d
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

class MatrixConversionTest {
    @Test
    fun `matrix4 conversion is row-major for MoreIotas`() {
        val matrix = Matrix4d().set(
            1.0, 2.0, 3.0, 4.0,
            5.0, 6.0, 7.0, 8.0,
            9.0, 10.0, 11.0, 12.0,
            13.0, 14.0, 15.0, 16.0,
        )

        assertArrayEquals(
            doubleArrayOf(
                1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 8.0,
                9.0, 10.0, 11.0, 12.0,
                13.0, 14.0, 15.0, 16.0,
            ),
            MatrixConversion.toRowMajor(matrix),
        )
    }

    @Test
    fun `matrix3 conversion is row-major for MoreIotas`() {
        val matrix = Matrix3d().set(
            1.0, 2.0, 3.0,
            4.0, 5.0, 6.0,
            7.0, 8.0, 9.0,
        )

        assertArrayEquals(
            doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0),
            MatrixConversion.toRowMajor(matrix),
        )
    }
}
