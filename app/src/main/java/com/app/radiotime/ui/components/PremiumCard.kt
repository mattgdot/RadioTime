package com.app.radiotime.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith

@Composable
fun PremiumCard(isPremium:Boolean,onSuccess:()->Unit) {
    fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
    val context = LocalContext.current
    if(!isPremium) {
        OutlinedCard(
            onClick = {
                context.findActivity()?.let { activity ->
                    Purchases.sharedInstance.getOfferingsWith(
                        onError = {
                            Toast.makeText(activity,"Unable to open purchase dialog",Toast.LENGTH_SHORT).show()
                        },
                        onSuccess = { offerings ->
                            try{
                                Purchases.sharedInstance.purchaseWith(
                                    PurchaseParams.Builder(activity, offerings.current!!.lifetime!!).build(),
                                    onError = { error, userCancelled -> },
                                    onSuccess = { storeTransaction, customerInfo ->
                                        if (customerInfo.entitlements["Premium"]?.isActive == true) {
                                            onSuccess()
                                        }
                                    }
                                )
                            } catch(e:Exception){
                                Toast.makeText(activity,"Unable to open purchase dialog",Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                }
            },
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Text("Remove Ads", style = MaterialTheme.typography.titleMedium, fontSize = TextUnit(23f,
                TextUnitType.Sp), modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp), color = MaterialTheme.colorScheme.primary)
            Text("Pay once. Remove ads forever.",modifier = Modifier.padding(horizontal = 15.dp).padding(bottom = 10.dp))
        }
    }
}