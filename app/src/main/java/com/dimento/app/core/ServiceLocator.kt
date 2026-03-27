package com.dimento.app.core

import android.content.Context

object ServiceLocator {
    @Volatile
    private var _container: AppContainer? = null

    val container: AppContainer
        get() = checkNotNull(_container) { "AppContainer is not initialized." }

    fun init(context: Context) {
        if (_container == null) {
            synchronized(this) {
                if (_container == null) {
                    _container = AppContainer(context.applicationContext)
                }
            }
        }
    }
}
