package nl.avisi.tech.lab.inventory.services

import org.springframework.stereotype.Service

@Service
// Simple stub that indicates whether the requested supplies are a positive number.
class SupplierService {
    fun requestHop(amount: Int): Boolean = amount > 0

    fun requestMalt(amount: Int): Boolean = amount > 0
}
