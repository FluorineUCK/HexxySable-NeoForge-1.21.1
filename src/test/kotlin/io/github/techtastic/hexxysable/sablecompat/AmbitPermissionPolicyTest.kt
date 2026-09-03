package io.github.techtastic.hexxysable.sablecompat

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AmbitPermissionPolicyTest {
    @Test
    fun `existing permission is never revoked`() {
        assertTrue(AmbitPermissionPolicy.resolve(current = true, managedPosition = false, projectedPermission = false))
    }

    @Test
    fun `unmanaged denied position remains denied`() {
        assertFalse(AmbitPermissionPolicy.resolve(current = false, managedPosition = false, projectedPermission = true))
    }

    @Test
    fun `managed position inherits denied projected permission`() {
        assertFalse(AmbitPermissionPolicy.resolve(current = false, managedPosition = true, projectedPermission = false))
    }

    @Test
    fun `managed position cannot keep an accidental plot permission when projected position is denied`() {
        assertFalse(AmbitPermissionPolicy.resolve(current = true, managedPosition = true, projectedPermission = false))
    }

    @Test
    fun `managed position inherits allowed projected permission`() {
        assertTrue(AmbitPermissionPolicy.resolve(current = false, managedPosition = true, projectedPermission = true))
    }
}
