package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.CoParentMember
import com.nestcheck.app.data.model.CoParentRole
import com.nestcheck.app.data.model.HouseholdInviteCode
import com.nestcheck.app.data.model.InviteStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FamilySharingRepository @Inject constructor() {

    private val _members = MutableStateFlow<List<CoParentMember>>(
        listOf(
            CoParentMember(
                id = "mem_1",
                name = "Dev Raj",
                email = "devramj2006@gmail.com",
                role = CoParentRole.ADMIN,
                status = InviteStatus.ACTIVE,
                invitedAt = "01 Oct 2026",
                lastActive = "Active Now (Primary Owner)"
            ),
            CoParentMember(
                id = "mem_2",
                name = "Priya",
                email = "priya@nestcheck.app",
                role = CoParentRole.ADMIN,
                status = InviteStatus.ACTIVE,
                invitedAt = "03 Oct 2026",
                lastActive = "12 mins ago"
            ),
            CoParentMember(
                id = "mem_3",
                name = "Grandma Elena",
                email = "elena.guardian@example.com",
                role = CoParentRole.GUARDIAN,
                status = InviteStatus.ACTIVE,
                invitedAt = "07 Oct 2026",
                lastActive = "Yesterday at 04:15 PM"
            )
        )
    )
    val members: StateFlow<List<CoParentMember>> = _members.asStateFlow()

    private val _activeInvite = MutableStateFlow<HouseholdInviteCode?>(
        HouseholdInviteCode(
            code = "NC-8294",
            expiresAtFormatted = "Expires in 48 hours",
            targetRole = CoParentRole.GUARDIAN
        )
    )
    val activeInvite: StateFlow<HouseholdInviteCode?> = _activeInvite.asStateFlow()

    fun generateNewInviteCode(role: CoParentRole): HouseholdInviteCode {
        val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ" // non-ambiguous chars
        val random = java.security.SecureRandom()
        val codeString = (1..6).map { chars[random.nextInt(chars.length)] }.joinToString("")
        val invite = HouseholdInviteCode(
            code = "NC-$codeString",
            expiresAtFormatted = "Expires in 15 minutes",
            targetRole = role
        )
        _activeInvite.value = invite
        return invite
    }

    fun inviteByEmail(name: String, email: String, role: CoParentRole) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val newMember = CoParentMember(
            id = "mem_${System.currentTimeMillis()}",
            name = name.trim(),
            email = email.trim(),
            role = role,
            status = InviteStatus.PENDING,
            invitedAt = sdf.format(Date()),
            lastActive = "Invite Email Sent"
        )
        _members.value = _members.value + newMember
    }

    fun removeMember(memberId: String) {
        _members.value = _members.value.filter { it.id != memberId }
    }

    fun updateRole(memberId: String, newRole: CoParentRole) {
        _members.value = _members.value.map {
            if (it.id == memberId) it.copy(role = newRole) else it
        }
    }
}
