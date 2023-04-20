package com.hermanowicz.pantry.data.remote.dataSource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hermanowicz.pantry.data.local.model.ProductEntity
import com.hermanowicz.pantry.di.remote.dataSource.ProductRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRemoteDataSourceImpl @Inject constructor() : ProductRemoteDataSource {

    private val databaseReference = FirebaseDatabase.getInstance().reference.child("products")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun observeAll(callback: (products: Flow<List<ProductEntity>>) -> Unit) {
        if (userId.isNotEmpty()) {
            databaseReference.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                    TODO("not implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val products: MutableList<ProductEntity> = mutableListOf()
                    val children = snapshot.children
                    children.forEach { product ->
                        product.getValue(ProductEntity::class.java)?.let { products.add(it) }
                    }
                    callback(flowOf(products))
                }
            })
        }
    }

    override fun observeById(id: Int, callback: (products: Flow<ProductEntity>) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun insert(products: List<ProductEntity>) {
        if (userId.isNotEmpty()) {
            products.forEach { product ->
                databaseReference.child(userId).child(product.id.toString()).setValue(product)
            }
        }
    }

    override suspend fun update(products: List<ProductEntity>) {
        if (userId.isNotEmpty()) {
            products.forEach { product ->
                databaseReference.child(userId).child(product.id.toString()).setValue(product)
            }
        }
    }

    override suspend fun delete(products: List<ProductEntity>) {
        if (userId.isNotEmpty()) {
            products.forEach { product ->
                databaseReference.child(userId).child(product.id.toString()).removeValue()
            }
        }
    }

    override suspend fun deleteAll() {
        if (userId.isNotEmpty()) {
            databaseReference.child(userId).removeValue()
        }
    }
}
