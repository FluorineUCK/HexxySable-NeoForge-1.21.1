package io.github.techtastic.hexxysable.casting.patterns.moreiotas

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import io.github.techtastic.hexxysable.sablecompat.MatrixConversion
import io.github.techtastic.hexxysable.util.assertShipInRange
import io.github.techtastic.hexxysable.util.getShip
import org.ejml.data.DMatrixRMaj
import org.ejml.simple.SimpleMatrix
import org.joml.Matrix4d
import ram.talia.moreiotas.api.casting.iota.MatrixIota

class OpShipGetMatrix(private val type: Type) : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val ship = args.getShip(env.world, 0, argc)
        env.assertShipInRange(ship)
        val matrix = when (type) {
            Type.WORLD_TO_SHIP -> ship.logicalPose().bakeIntoMatrix(Matrix4d()).invert().toSimpleMatrix()
            Type.SHIP_TO_WORLD -> ship.logicalPose().bakeIntoMatrix(Matrix4d()).toSimpleMatrix()
            Type.MOMENT_OF_INERTIA_TENSOR -> SimpleMatrix.wrap(
                DMatrixRMaj(3, 3, true, *MatrixConversion.toRowMajor(ship.massTracker.inertiaTensor)),
            )
        }
        return listOf(MatrixIota(matrix))
    }

    private fun Matrix4d.toSimpleMatrix(): SimpleMatrix =
        SimpleMatrix.wrap(DMatrixRMaj(4, 4, true, *MatrixConversion.toRowMajor(this)))

    enum class Type {
        WORLD_TO_SHIP,
        SHIP_TO_WORLD,
        MOMENT_OF_INERTIA_TENSOR,
    }
}
