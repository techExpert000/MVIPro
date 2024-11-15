package com.abhishek.pathak.kotlin.android.githubcompose.ui.feature.repos.composables

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.abhishek.pathak.kotlin.android.githubcompose.R
import com.abhishek.pathak.kotlin.android.githubcompose.data.model.UserDetail
import com.abhishek.pathak.kotlin.android.githubcompose.data.model.buildUserDetailPreview
import com.abhishek.pathak.kotlin.android.githubcompose.common.buildUrlIntent
import com.abhishek.pathak.kotlin.android.githubcompose.ui.feature.common.RoundedImage
import com.abhishek.pathak.kotlin.android.githubcompose.ui.theme.OnSurfaceBackgroundAlpha
import com.abhishek.pathak.kotlin.android.githubcompose.ui.theme.OnSurfaceTextAlpha
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ReposListHeader(userDetail: UserDetail) {
    val paddingMedium = dimensionResource(id = R.dimen.padding_medium)
    val paddingXSmall = dimensionResource(id = R.dimen.padding_xsmall)

    Column{
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingMedium)
        ) {
            ScoreSession(userDetail)

            Spacer(modifier = Modifier.size(paddingXSmall))

            UserDetailSession(userDetail)

            Spacer(modifier = Modifier.size(paddingMedium))

            ButtonsSession(userDetail)
        }

        Divider(
            thickness = paddingMedium,
            color = MaterialTheme.colors.onSurface.copy(alpha = OnSurfaceBackgroundAlpha)
        )
    }
}

@Composable
fun ScoreSession(userDetail: UserDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        RoundedImage(
            url = userDetail.avatarUrl,
            placeholder = R.drawable.avatar_placeholder,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.avatar_size_large))
                .padding(end = dimensionResource(id = R.dimen.padding_medium))
        )

        ScoreItem(count = userDetail.publicRepos, description = stringResource(R.string.repos_score_title))
        ScoreItem(count = userDetail.followers, description = stringResource(R.string.followers_score_title))
        ScoreItem(count = userDetail.following, description = stringResource(R.string.following_score_title))
    }
}

@Composable
fun UserDetailSession(userDetail: UserDetail) {

    Text(
        text = userDetail.name.orEmpty(),
        style = MaterialTheme.typography.h5,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = userDetail.location ?: stringResource(R.string.location_not_defined),
        style = MaterialTheme.typography.subtitle1,
        color = MaterialTheme.colors.onSurface.copy(alpha = OnSurfaceTextAlpha)
    )
}

@Composable
fun ButtonsSession(userDetail: UserDetail) {
    // See all button
    val context = LocalContext.current
    val profileIntent = remember{ buildUrlIntent(userDetail.htmlUrl) }
    //val blogIntent = remember{ buildUrlIntent(userDetail.blogUrl) }
    val blog = userDetail.blogUrl
    val loadBlog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if(loadBlog.value){
        LoadWebUrl(context = context, url = blog)
    }

    // View Blog
    val blogNotFoundDialog = remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = {
            context.startActivity(profileIntent)
        }) {
            Text(text = stringResource(R.string.button_see_all_title))
        }

        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))

        OutlinedButton(onClick = {
            if (userDetail.blogUrl.isEmpty()) {
                blogNotFoundDialog.value = true
            } else {
                //Open a webview - @bitbyAlex
                //context.startActivity(blogIntent)
                scope.launch {
                    loadBlog.value = true
                }
            }
        }) {
            Text(text = stringResource(R.string.button_view_blog_title))
        }
    }

    if (blogNotFoundDialog.value) {
        AlertDialog(onDismissRequest = {
            blogNotFoundDialog.value = false
        },
            title = {
                Text(text = stringResource(R.string.blog_not_found_dialog_title))
            },
            text = {
                Text(text = stringResource(R.string.blog_not_found_dialog_text))
            },
            confirmButton = {
                Text(text = stringResource(R.string.blog_not_found_dialog_confirm_button).uppercase(
                    Locale.getDefault()
                ))
            })
    }
}

@Composable
fun ScoreItem(count: Int, description: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface.copy(alpha = OnSurfaceTextAlpha)
        )
    }
}

@Composable
fun LoadWebUrl(context: Context, url: String){

    AndroidView(factory = {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    })
}
/*
@Composable
fun LoadWebUrl(context: Context, url: String) {

    AndroidView(factory = {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true // to enable youtube

            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}
 */

@Preview(showBackground = true)
@Composable
fun ReposUserDetailPreview() {
    ReposListHeader(userDetail = buildUserDetailPreview())
}
