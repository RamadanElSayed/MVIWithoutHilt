package com.instant.mviwithouthilt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.instant.mviwithouthilt.view.screens.UserListScreen
import com.instant.mviwithouthilt.view.viewmodel.UserListViewModel


class MainActivity : ComponentActivity() {

    private val userListViewModel: UserListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserListScreen(userListViewModel = userListViewModel)
        }
    }
}












