package com.example.musik

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.musik.ui.components.MainContainer
import com.example.musik.ui.components.MusikTopAppBar

@Composable
fun MusikApp(
	//viewModel: MusikViewModel = viewModel(),
	navController: NavHostController = rememberNavController()
) {
	Scaffold(
		containerColor = MaterialTheme.colorScheme.background,
		topBar = { MusikTopAppBar() }
	) { innerPadding ->
		//Column(modifier = Modifier.padding(innerPadding)) { }
		//val uiState by viewModel.uiState.collectAsState()
		MainContainer(modifier = Modifier.padding(innerPadding))
		/*
		NavHost(
			navController = navController,
			startDestination = Routes.EntryPoint.name,
			modifier = Modifier.padding(innerPadding)
		) {
			composable(route = Routes.EntryPoint.name) {
				val context = LocalContext.current

				MainContainer()
			}
		}
		 */
	}
}