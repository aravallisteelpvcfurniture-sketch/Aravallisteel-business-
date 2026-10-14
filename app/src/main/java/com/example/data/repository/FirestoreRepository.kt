package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.ConfirmedOrder
import com.example.data.model.Estimate
import com.example.data.model.Visitor
import com.example.data.model.Carpenter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val context: Context) {
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(context.getString(R.string.firestore_database_id))
    }

    private val auth by lazy { Firebase.auth }

    val currentUser get() = auth.currentUser

    fun requireUserId(): String {
        return auth.currentUser?.uid ?: "unauthenticated"
    }

    suspend fun syncEstimate(estimate: Estimate) {
        val uid = auth.currentUser?.uid ?: return
        val map = hashMapOf(
            "userId" to uid,
            "customerName" to estimate.customerName,
            "customerPhone" to estimate.customerPhone,
            "customerAddress" to estimate.customerAddress,
            "productType" to estimate.productType,
            "dimensions" to estimate.dimensions,
            "areaSqFt" to estimate.areaSqFt,
            "ratePerSqFt" to estimate.ratePerSqFt,
            "hardwareCost" to estimate.hardwareCost,
            "discountAmount" to estimate.discountAmount,
            "totalAmount" to estimate.totalAmount,
            "createdDate" to estimate.createdDate,
            "status" to estimate.status,
            "notes" to estimate.notes
        )
        db.collection("users").document(uid).collection("estimates")
            .document(estimate.id.toString())
            .set(map)
            .await()
    }

    suspend fun syncOrder(order: ConfirmedOrder) {
        val uid = auth.currentUser?.uid ?: return
        val map = hashMapOf(
            "userId" to uid,
            "orderNumber" to order.orderNumber,
            "customerName" to order.customerName,
            "customerPhone" to order.customerPhone,
            "deliveryAddress" to order.deliveryAddress,
            "productSummary" to order.productSummary,
            "totalAmount" to order.totalAmount,
            "advancePaid" to order.advancePaid,
            "balanceDue" to order.balanceDue,
            "orderDate" to order.orderDate,
            "deliveryDate" to order.deliveryDate,
            "stage" to order.stage,
            "paymentMode" to order.paymentMode
        )
        db.collection("users").document(uid).collection("orders")
            .document(order.id.toString())
            .set(map)
            .await()
    }

    suspend fun syncVisitor(visitor: Visitor) {
        val uid = auth.currentUser?.uid ?: return
        val map = hashMapOf(
            "userId" to uid,
            "name" to visitor.name,
            "phone" to visitor.phone,
            "address" to visitor.address,
            "visitDate" to visitor.visitDate,
            "interestedProduct" to visitor.interestedProduct,
            "budget" to visitor.budget,
            "priority" to visitor.priority,
            "followUpDate" to visitor.followUpDate,
            "notes" to visitor.notes,
            "status" to visitor.status
        )
        db.collection("users").document(uid).collection("visitors")
            .document(visitor.id.toString())
            .set(map)
            .await()
    }

    suspend fun syncCarpenter(carpenter: Carpenter) {
        val uid = auth.currentUser?.uid ?: return
        val map = hashMapOf(
            "userId" to uid,
            "name" to carpenter.name,
            "phone" to carpenter.phone,
            "location" to carpenter.location,
            "totalReferrals" to carpenter.totalReferrals,
            "pointsEarned" to carpenter.pointsEarned,
            "totalCommissionEarned" to carpenter.totalCommissionEarned,
            "pendingPayout" to carpenter.pendingPayout,
            "joinDate" to carpenter.joinDate
        )
        db.collection("users").document(uid).collection("carpenters")
            .document(carpenter.id.toString())
            .set(map)
            .await()
    }
}
