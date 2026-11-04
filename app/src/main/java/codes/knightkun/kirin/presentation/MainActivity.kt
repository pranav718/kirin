package codes.knightkun.kirin.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import codes.knightkun.kirin.R
import codes.knightkun.kirin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        var systemBarsBottom = 0

        // Setup edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainCoordinator) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            systemBarsBottom = systemBars.bottom
            binding.appBarLayout.setPadding(0, systemBars.top, 0, 0)
            binding.bottomNav.setPadding(0, 0, 0, systemBars.bottom)
            if (binding.bottomNav.visibility == View.GONE) {
                binding.navHostFragment.setPadding(0, 0, 0, systemBars.bottom)
            } else {
                binding.navHostFragment.setPadding(0, 0, 0, 0)
            }
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val topLevelDestinations = setOf(
            R.id.home_dest,
            R.id.projects_dest,
            R.id.blog_dest,
            R.id.about_dest
        )
        val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        // Dynamically toggle bottom navigation visibility on detail screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.project_detail_dest) {
                binding.bottomNav.visibility = View.GONE
                binding.bottomNavDivider.visibility = View.GONE
                binding.navHostFragment.setPadding(0, 0, 0, systemBarsBottom)
            } else {
                binding.bottomNav.visibility = View.VISIBLE
                binding.bottomNavDivider.visibility = View.VISIBLE
                binding.navHostFragment.setPadding(0, 0, 0, 0)
            }
        }

        setupMenuProvider()
    }

    private fun setupMenuProvider() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_web -> {
                        val customTabsIntent = CustomTabsIntent.Builder().setShowTitle(true).build()
                        customTabsIntent.launchUrl(
                            this@MainActivity,
                            Uri.parse("https://portfolio.knightkun.codes")
                        )
                        true
                    }
                    R.id.action_share -> {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Pranav Ray - Systems & Distributed Backend Portfolio\nhttps://portfolio.knightkun.codes"
                            )
                        }
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
                        true
                    }
                    else -> false
                }
            }
        }, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
