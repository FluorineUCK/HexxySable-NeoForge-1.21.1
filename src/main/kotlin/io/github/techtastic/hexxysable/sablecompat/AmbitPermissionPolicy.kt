package io.github.techtastic.hexxysable.sablecompat

object AmbitPermissionPolicy {
    fun resolve(current: Boolean, managedPosition: Boolean, projectedPermission: Boolean): Boolean =
        if (managedPosition) projectedPermission else current
}
