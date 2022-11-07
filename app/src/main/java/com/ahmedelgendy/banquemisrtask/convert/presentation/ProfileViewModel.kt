package com.inn.eta.more.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inn.eta.general.Event
import com.inn.eta.loginAndRegister.data.model.login.LoginResponse
import com.inn.eta.loginAndRegister.data.model.registration.Profile
import com.inn.eta.loginAndRegister.data.model.registration.ProfileResponse
import com.inn.eta.more.data.model.ChangePasswordModel
import com.inn.eta.more.data.model.DeleteAccountResponse
import com.inn.eta.more.domain.repositories.MoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import retrofit2.Response

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: MoreRepo
) : ViewModel() {

    var editProfileResponse: MutableLiveData<Event<Response<Profile>>> = MutableLiveData()
        get() = _editProfileResponse
    private var _editProfileResponse: MutableLiveData<Event<Response<Profile>>> =
        MutableLiveData()

    var profileResponse: MutableLiveData<Event<Response<ProfileResponse>>> = MutableLiveData()
        get() = _profileResponse
    private var _profileResponse: MutableLiveData<Event<Response<ProfileResponse>>> =
        MutableLiveData()

    var changePasswordResponse: MutableLiveData<Event<Response<LoginResponse>>> =
        MutableLiveData()
        get() = _changePasswordResponse
    private var _changePasswordResponse: MutableLiveData<Event<Response<LoginResponse>>> =
        MutableLiveData()

    var deleteAccountResponse: MutableLiveData<Event<Response<DeleteAccountResponse>>> =
        MutableLiveData()
        get() = _deleteAccountResponse
    private var _deleteAccountResponse: MutableLiveData<Event<Response<DeleteAccountResponse>>> =
        MutableLiveData()

    fun editProfile(userModel: Profile) = viewModelScope.launch {
        _editProfileResponse.postValue(Event(repo.editProfile(userModel)))
    }

    fun getProfile() = viewModelScope.launch {
        _profileResponse.postValue(Event(repo.getProfile()))
    }

    fun changePassword(changePasswordModel: ChangePasswordModel) = viewModelScope.launch {
        _changePasswordResponse.postValue(Event(repo.changePassword(changePasswordModel)))
    }

    fun deleteAccount() = viewModelScope.launch {
        _deleteAccountResponse.postValue(Event(repo.deleteAccount()))
    }

    fun clearResponses() {
        _profileResponse = MutableLiveData()
        _changePasswordResponse = MutableLiveData()
        _editProfileResponse = MutableLiveData()
        _deleteAccountResponse = MutableLiveData()
    }
}
