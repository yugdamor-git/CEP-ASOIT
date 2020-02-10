package com.asoit.cep_asoit.utils

import android.content.Context

class Preference(context: Context) {

    private val APPDATA = "APPDATA"

    private val LOGGEDIN = "LOGGEDIN"
    private val COOKIES = "COOKIES"
    private val VIEWSTATE = "VIEWSTATE"
    private val VALIDATION = "VALIDATION"
    private val PASSWORD = "PASSWORD"
    private val USERNAME = "USERNAME"
    private val VIEWSTATEGEN = "VIEWSTATEGEN"
    private val INSTITUTE = "INSTITUTE"
    private val BRANCH = "BRANCH"
    private val SEMESTER = "SEMESTER"
    private val DIVISION = "DIVISION"
    private val USERFRAGUPDATED = "USERFRAGUPDATED"
    private val NAME = "NAME"
    private val LASTCOOKIEUPDATED = "LASTCOOKIEUPDATED"


    val preference = context.getSharedPreferences(APPDATA, Context.MODE_PRIVATE)

    fun getCookie(): String? {
        return preference.getString(COOKIES, null)
    }


    fun saveCookie(cookie: String?) {
        preference.edit().putString(COOKIES, cookie).apply()
    }

    fun getUsername(): String? {
        return preference.getString(USERNAME, null)
    }

    fun getPassword(): String? {
        return preference.getString(PASSWORD, null)
    }

    fun saveUsername(uname: String) {
        preference.edit().putString(USERNAME, uname).apply()
    }

    fun savePassword(pwd: String) {
        preference.edit().putString(PASSWORD, pwd).apply()
    }

    fun clearAllSharedPref() {
        preference.edit().clear().apply()
    }

    fun saveViewStateGen(viewstategen: String) {
        preference.edit().putString(VIEWSTATEGEN, viewstategen).apply()
    }

    fun getViewStateGen(): String? {

        return preference.getString(VIEWSTATEGEN, null)
    }

    fun getLoggedIn(): Boolean? {
        return preference.getBoolean(LOGGEDIN, false)
    }

    fun saveLoggedIn(loggedIn: Boolean) {
        preference.edit().putBoolean(LOGGEDIN, loggedIn).apply()
    }

    fun getViewstate(): String? {
        return preference.getString(VIEWSTATE, null)
    }

    fun saveViewstate(viewstate: String) {
        preference.edit().putString(VIEWSTATE, viewstate).apply()
    }


    fun getValidation(): String? {
        return preference.getString(VALIDATION, null)
    }

    fun saveValidation(validation: String) {
        preference.edit().putString(VALIDATION, validation).apply()
    }

    fun getInstitute(): String? {
        return preference.getString(INSTITUTE, null)
    }

    fun saveInstitute(institute: String) {
        preference.edit().putString(INSTITUTE, institute).apply()
    }


    fun getBranch(): String? {
        return preference.getString(BRANCH, null)
    }

    fun saveBranch(branch: String) {
        preference.edit().putString(BRANCH, branch).apply()
    }


    fun getSem(): String? {
        return preference.getString(SEMESTER, null)
    }

    fun saveSem(sem: String) {
        preference.edit().putString(SEMESTER, sem).apply()
    }


    fun getDivision(): String? {
        return preference.getString(DIVISION, null)
    }

    fun saveDivision(div: String) {
        preference.edit().putString(DIVISION, div).apply()
    }

    fun userFragUpdated(): Boolean? {
        return preference.getBoolean(USERFRAGUPDATED, false)
    }

    fun saveUserFrag(data: Boolean) {
        preference.edit().putBoolean(USERFRAGUPDATED, data).apply()
    }

    fun getName(): String? {
        return preference.getString(NAME, null)
    }

    fun saveName(name: String) {
        preference.edit().putString(NAME, name).apply()
    }

    fun getLastCookieUpdated(): Long? {
        return preference.getLong(LASTCOOKIEUPDATED, 0)
    }

    fun saveLastCookieUpdated(time: Long) {
        preference.edit().putLong(LASTCOOKIEUPDATED, time).apply()
    }


}