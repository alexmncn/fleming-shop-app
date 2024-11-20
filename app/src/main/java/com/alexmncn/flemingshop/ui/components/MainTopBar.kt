package com.alexmncn.flemingshop.ui.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alexmncn.flemingshop.R
import com.alexmncn.flemingshop.data.network.AuthManager
import com.alexmncn.flemingshop.ui.main.FeaturedArticlesActivity
import com.alexmncn.flemingshop.ui.main.LoginActivity
import com.alexmncn.flemingshop.ui.main.UserPanelActivity
import com.alexmncn.flemingshop.ui.main.UserPanelScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar() {
    val context = LocalContext.current

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fleming_logo),
                    contentDescription = "Tienda Fleming",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxHeight()
                )


                // User
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable {
                            var intent = Intent(context, LoginActivity::class.java)

                            if (AuthManager.isAuthenticated()) { intent = Intent(context, UserPanelActivity::class.java) }

                            context.startActivity(intent)
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(20.dp)
                    )

                    if (AuthManager.isAuthenticated()) {
                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${AuthManager.getUsername()}",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        ),
        modifier = Modifier.height(50.dp)
    )
}