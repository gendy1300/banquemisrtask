package com.inn.eta.more.domain.repositories

import com.inn.eta.loginAndRegister.data.model.login.LoginResponse
import com.inn.eta.loginAndRegister.data.model.registration.Profile
import com.inn.eta.loginAndRegister.data.model.registration.ProfileResponse
import com.inn.eta.more.data.model.ChangePasswordModel
import com.inn.eta.more.data.model.DeleteAccountResponse
import retrofit2.Response

interface MoreRepo {

    suspend fun editProfile(userModel: Profile): Response<Profile>

    suspend fun getProfile(): Response<ProfileResponse>

    suspend fun changePassword(changePasswordModel: ChangePasswordModel): Response<LoginResponse>

    suspend fun deleteAccount(): Response<DeleteAccountResponse>
}
